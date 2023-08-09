package com.anczykowski.assigner.integrationTests;

import com.anczykowski.assigner.auth.controllers.AuthController;
import com.anczykowski.assigner.auth.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.json.JSONObject;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class BaseIntegrationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MapSessionRepository sessionRepository;

    @MockBean
    private AuthService authService;

    @InjectMocks
    @SuppressWarnings("unused")
    private AuthController authController;

    protected Cookie cookie = null;

    protected final String courseName = "PZSP3";

    protected final String edition = "21l";

    protected final String editionPath = "/courses/%s/editions/%s".formatted(courseName, edition);

    protected final Integer testUserUsosId = 12345678;

    private MapSession createSession() {
        var session = sessionRepository.createSession();
        sessionRepository.save(session);
        return session;
    }

    private void verify() {
        var accessToken = "100";
        var accessTokenSecret = "101";
        var usosId = "12345678";
        var session = sessionRepository.findById(cookie.getValue());
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("accessTokenSecret", accessTokenSecret);
        session.setAttribute("usosId", usosId);
        sessionRepository.save(session);
    }

    protected void authenticate() throws Exception {
        cookie = null;
        Mockito.when(authService.createSession()).thenAnswer((Answer<MapSession>) invocation -> createSession());
        doAnswer(invocation -> {
            verify();
            return null;
        }).when(authService).verify(any(String.class), any(String.class));

        var request = post("/auth")
                .content(new JSONObject().put("callbackUrl", "oob").toString());
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(r -> cookie = r.getResponse().getCookie("SESSION"))
        ;
        var verifyRequest = post("/verify")
                .content(new JSONObject().put("verifier", "104").toString());
        mockMvc.perform(verifyRequest)
                .andExpect(status().isOk());
    }

    protected void setupCourseAndCourseEdition() throws Exception {
        var request = post("/courses").param("name", courseName);
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var file = new MockMultipartFile(
                "file",
                "students.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "nazwisko;imie;imie2;skreslony;rezygnacja;login_office365\nKowalski;Jan;;0;0;%d@pw.edu.pl".formatted(testUserUsosId).getBytes()
        );
        var courseEditionRequest = multipart("/courses/PZSP3/editions")
                .file(file)
                .param("edition", edition)
                .cookie(cookie);

        mockMvc.perform(courseEditionRequest)
                .andExpect(status().isOk());

    }

    protected MockHttpServletRequestBuilder get(String url, Object... uriVars) {
        if (cookie != null) {
            return MockMvcRequestBuilders.get(url, uriVars)
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
        return MockMvcRequestBuilders.get(url, uriVars)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder put(String url, Object... uriVars) {
        if (cookie != null) {
            return MockMvcRequestBuilders.put(url, uriVars)
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
        return MockMvcRequestBuilders.put(url, uriVars)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    protected MockHttpServletRequestBuilder post(String url, Object... uriVars) {
        if (cookie != null) {
            return MockMvcRequestBuilders.post(url, uriVars)
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
        return MockMvcRequestBuilders.post(url, uriVars)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @SuppressWarnings("unused")
    protected MockHttpServletRequestBuilder delete(String url, Object... uriVars) {
        if (cookie != null) {
            return MockMvcRequestBuilders.delete(url, uriVars)
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);
        }
        return MockMvcRequestBuilders.delete(url, uriVars)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    protected <T> T getFromResult(MvcResult result, String path) throws Exception {
        return JsonPath.read(result.getResponse().getContentAsString(), path);
    }

}
