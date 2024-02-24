package com.anczykowski.assigner.auth.services;


import com.anczykowski.assigner.auth.models.ProfileResponse;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import com.anczykowski.assigner.users.models.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import okhttp3.*;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Service
@Transactional(readOnly = true)
public class AuthService {

    final String OAUTH_BASE_URL = "https://apps.usos.pw.edu.pl";

    final String OAUTH_SERVICES = OAUTH_BASE_URL + "/services/oauth";

    final String REQUEST_TOKEN_URL = OAUTH_SERVICES + "/request_token";

    final String ACCESS_TOKEN_URL = OAUTH_SERVICES + "/access_token";

    final String AUTHORIZATION_URL = OAUTH_SERVICES + "/authorize";

    final String USER_URL = OAUTH_BASE_URL + "/services/users/user";

    @Value("${consumer.key}")
    private String consumerKey;

    @Value("${consumer.secret}")
    private String consumerSecret;

    @Value("${initial.coordinator.usos.id}")
    private Integer initialCoordinatorUsosId;

    final PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy<String, Pair<OAuthConsumer, OAuthProvider>>
            expirationPolicy = new PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy<>(
            8, TimeUnit.HOURS);

    final PassiveExpiringMap<String, Pair<OAuthConsumer, OAuthProvider>> consumerProviderMap = new PassiveExpiringMap<>(expirationPolicy, new HashMap<>());

    final MapSessionRepository sessionRepository;

    final UsersRepository usersRepository;

    public AuthService(
            MapSessionRepository sessionRepository,
            UsersRepository usersRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.usersRepository = usersRepository;
    }

    public String getToken(String callbackUrl, String sessionId) {
        try {
            var consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
            var provider = new DefaultOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZATION_URL);
            consumerProviderMap.put(sessionId, Pair.of(consumer, provider));

            return provider.retrieveRequestToken(consumer, callbackUrl);
        } catch (OAuthMessageSignerException |
                 OAuthNotAuthorizedException |
                 OAuthExpectationFailedException |
                 OAuthCommunicationException e) {
            throw new RuntimeException(e);
        }
    }

    public MapSession createSession() {
        var session = sessionRepository.createSession();
        session.setMaxInactiveInterval(Duration.ofMinutes(60));
        sessionRepository.save(session);
        return session;
    }

    @Transactional
    public void verify(String verifier, String sessionId) {
        try {

            var consumerAndProvider = consumerProviderMap.get(sessionId);
            if (consumerAndProvider == null) {
                throw new NotFoundException("Verify called on expired OAuth consumer/provider");
            }
            var consumer = consumerAndProvider.getFirst();
            var provider = consumerAndProvider.getSecond();

            provider.retrieveAccessToken(consumer, verifier);

            var accessToken = consumer.getToken();
            var accessTokenSecret = consumer.getTokenSecret();
            var profileData = getProfileData(accessToken, accessTokenSecret, sessionId);

            var session = sessionRepository.findById(sessionId);

            if (session != null) {
                session.setAttribute("accessToken", accessToken);
                session.setAttribute("accessTokenSecret", accessTokenSecret);
                session.setAttribute("usosId", profileData.getId());

                var userType = UserType.STUDENT;
                if (profileData.getStaff_status() > 0) {
                    userType = UserType.TEACHER;
                }

                if (Integer.valueOf(profileData.getId()).equals(initialCoordinatorUsosId)) {
                    userType = UserType.COORDINATOR;
                }

                var user = usersRepository.getByUsosId(Integer.valueOf(profileData.getId()));
                if (user.isPresent()) {
                    userType = user.get().getUserType();
                } else {
                    usersRepository.save(User.builder()
                            .name(profileData.getFirst_name())
                            .surname(profileData.getLast_name())
                            .secondName(profileData.getMiddle_names())
                            .userType(userType)
                            .usosId(Integer.valueOf(profileData.getId()))
                            .build()
                    );
                }
                session.setAttribute("userType", String.valueOf(userType.ordinal()));

                sessionRepository.save(session);
            }
        } catch (OAuthMessageSignerException |
                 OAuthNotAuthorizedException |
                 OAuthExpectationFailedException |
                 OAuthCommunicationException e) {
            throw new RuntimeException(e);
        }
    }

    public ProfileResponse userData(String sessionId) {
        var session = sessionRepository.findById(sessionId);
        String accessToken = session.getAttribute("accessToken");
        String accessTokenSecret = session.getAttribute("accessTokenSecret");
        if (accessToken == null) throw new RuntimeException();
        return getProfileData(accessToken, accessTokenSecret, sessionId);
    }

    private ProfileResponse getProfileData(String accessToken, String accessTokenSecret, String sessionId) {
        var consumerAndProvider = consumerProviderMap.get(sessionId);
        if (consumerAndProvider == null) {
            throw new NotFoundException("getProfileData called on expired OAuth consumer/provider");
        }
        var consumer = consumerAndProvider.getFirst();

        consumer.setTokenWithSecret(accessToken, accessTokenSecret);
        var okConsumer = new OkHttpOAuthConsumer(consumerKey, consumerSecret);
        okConsumer.setTokenWithSecret(accessToken, accessTokenSecret);
        var client = new OkHttpClient.Builder().addInterceptor(new SigningInterceptor(okConsumer)).build();

        var urlBuilder = Objects.requireNonNull(HttpUrl.parse(USER_URL)).newBuilder();
        urlBuilder.addQueryParameter("fields", "id|first_name|middle_names|last_name|student_status|staff_status");

        var request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseBody responseBody = response.body();
            return objectMapper.readValue(Objects.requireNonNull(responseBody).string(), ProfileResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
