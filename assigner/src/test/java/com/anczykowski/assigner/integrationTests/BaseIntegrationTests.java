package com.anczykowski.assigner.integrationTests;

import com.anczykowski.assigner.auth.controllers.AuthController;
import com.anczykowski.assigner.auth.services.AuthService;
import com.anczykowski.assigner.users.models.UserType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers;
import org.json.JSONArray;
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

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
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

    protected Integer teamId = null;

    protected Integer secondTeamId = null;

    protected Integer projectId = null;

    protected Integer secondProjectId = null;

    protected final String courseName = "PZSP3";

    protected final String edition = "21l";

    protected final String groupName = "PRO101";

    protected final String editionGroupPath = "/courses/%s/editions/%s/groups/%s".formatted(courseName, edition, groupName);

    protected final Integer testUserUsosId = 12345678;

    protected final Integer testUser2UsosId = 22222222;


    private MapSession createSession() {
        var session = sessionRepository.createSession();
        sessionRepository.save(session);
        return session;
    }

    private void verify(Integer usosId, UserType userType) {
        var accessToken = "100";
        var accessTokenSecret = "101";
        var session = sessionRepository.findById(cookie.getValue());
        if(session != null){
            session.setAttribute("accessToken", accessToken);
            session.setAttribute("accessTokenSecret", accessTokenSecret);
            session.setAttribute("usosId", usosId.toString());
            session.setAttribute("userType", String.valueOf(userType.ordinal()));
            sessionRepository.save(session);
        }
    }


    protected void authenticate() throws Exception {
        authenticate(testUserUsosId);
    }

    protected void authenticate(Integer usosId) throws Exception {
        authenticate(usosId, UserType.COORDINATOR);
    }

    protected void authenticate(Integer usosId, UserType userType) throws Exception {
        cookie = null;
        Mockito.when(authService.createSession()).thenAnswer((Answer<MapSession>) invocation -> createSession());
        doAnswer(invocation -> {
            verify(usosId, userType);
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
                "nazwisko;imie;imie2;skreslony;rezygnacja;login_office365;grupy\nKowalski;Jan;;0;0;%d@pw.edu.pl;\"CWI101, PRO101, WYK1\"\nKowalski2;Jan2;;0;0;%d@pw.edu.pl;\"CWI101, PRO101, WYK1\"".formatted(testUserUsosId, testUser2UsosId).getBytes()
        );
        var courseEditionRequest = multipart("/courses/PZSP3/editions")
                .file(file)
                .param("edition", edition)
                .cookie(cookie);

        mockMvc.perform(courseEditionRequest)
                .andExpect(status().isOk());

    }

    protected void setupTeam() throws Exception {
        var request = post(editionGroupPath + "/teams")
                .content(new JSONObject().put("name", "team1").toString());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        teamId = getFromResult(result, "id");
    }

    protected void setupSecondTeam() throws Exception {
        var request = post(editionGroupPath + "/teams")
                .content(new JSONObject().put("name", "team2").toString());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        secondTeamId = getFromResult(result, "id");
    }

    protected void setupProject() throws Exception {
        var request = post(editionGroupPath + "/projects")
                .content(new JSONObject()
                        .put("name", "name1")
                        .put("teamLimit", 1)
                        .put("description", "desc1")
                        .toString());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        projectId = getFromResult(result, "id");

    }

    protected void setupSecondProject() throws Exception {
        var request = post(editionGroupPath + "/projects")
                .content(new JSONObject()
                        .put("name", "name2")
                        .put("teamLimit", 1)
                        .put("description", "desc2")
                        .toString());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        secondProjectId = getFromResult(result, "id");

    }

    protected JsonUnitResultMatchers jsonIgnoringWrapper() {
        return json().when(Option.IGNORING_EXTRA_FIELDS).when(Option.IGNORING_ARRAY_ORDER);
    }

    protected static JSONArray jsonArray(JSONObject... contents) {
        var result = new JSONArray();
        result.putAll(contents);
        return result;
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
