package com.anczykowski.assigner.integrationTests;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TeamsIntegrationTests extends BaseIntegrationTests {
    @Test
    @DirtiesContext
    void createTeam() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();

        var request = post(editionPath + "/teams")
                .content(new JSONObject().put("name", "test1").toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("name").isEqualTo("test1"));
    }

    @Test
    @DirtiesContext
    void generateAccessToken() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();

        var request = post(editionPath + "/teams")
                .content(new JSONObject().put("name", "test1").toString());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var teamId = (Integer) getFromResult(result, "id");

        var accessTokenRequest = put("%s/teams/%d/access-token".formatted(editionPath, teamId));

        var accessTokenResult = mockMvc.perform(accessTokenRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("accessToken").isPresent())
                .andExpect(json().node("accessTokenExpirationDate").isPresent())
                .andReturn();

        var accessToken = getFromResult(accessTokenResult, "accessToken");
        var getAccessTokenRequest = get("%s/teams/%d/access-token".formatted(editionPath, teamId));

        mockMvc.perform(getAccessTokenRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("accessToken").isEqualTo(accessToken));

    }

}
