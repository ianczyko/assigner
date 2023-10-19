package com.anczykowski.assigner.auth.services;


import com.anczykowski.assigner.auth.dto.ProfileResponse;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;


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

    private OAuthConsumer consumer;

    private OAuthProvider provider;

    public AuthService(
            MapSessionRepository sessionRepository,
            UsersRepository usersRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.usersRepository = usersRepository;
    }

    MapSessionRepository sessionRepository;

    UsersRepository usersRepository;

    @PostConstruct
    private void postConstruct() {
        consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
        provider = new DefaultOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZATION_URL);
    }

    public String getToken(String callbackUrl) {
        try {
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

    public void verify(String verifier, String sessionId) {
        try {
            provider.retrieveAccessToken(consumer, verifier);

            var accessToken = consumer.getToken();
            var accessTokenSecret = consumer.getTokenSecret();
            var profileData = getProfileData(accessToken, accessTokenSecret);

            var session = sessionRepository.findById(sessionId);

            if (session != null) {
                session.setAttribute("accessToken", accessToken);
                session.setAttribute("accessTokenSecret", accessTokenSecret);
                session.setAttribute("usosId", profileData.getId());

                var userType = UserType.STUDENT.ordinal();
                if (profileData.getStaff_status() > 0) {
                    userType = UserType.TEACHER.ordinal();
                }
                var user = usersRepository.getByUsosId(Integer.valueOf(profileData.getId()));
                if (user.isPresent()) {
                    userType = user.get().getUserType().ordinal();
                }
                session.setAttribute("userType", String.valueOf(userType));

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
        return getProfileData(accessToken, accessTokenSecret);
    }

    private ProfileResponse getProfileData(String accessToken, String accessTokenSecret) {
        consumer.setTokenWithSecret(accessToken, accessTokenSecret);
        var okConsumer = new OkHttpOAuthConsumer(consumerKey, consumerSecret);
        okConsumer.setTokenWithSecret(accessToken, accessTokenSecret);
        var client = new OkHttpClient.Builder().addInterceptor(new SigningInterceptor(okConsumer)).build();

        var urlBuilder = Objects.requireNonNull(HttpUrl.parse(USER_URL)).newBuilder();
        urlBuilder.addQueryParameter("fields", "id|first_name|last_name|student_status|staff_status");

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
