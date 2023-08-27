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
        setupTeam();

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

    @Test
    @DirtiesContext
    void addTeamMember() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();

        var accessTokenRequest = put("%s/teams/%d/access-token".formatted(editionPath, teamId));

        var accessTokenResult = mockMvc.perform(accessTokenRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("accessToken").isPresent())
                .andReturn();

        var accessToken = getFromResult(accessTokenResult, "accessToken");

        var addTeamMemberRequest = post("%s/teams/%d/members".formatted(editionPath, teamId))
                .param("access-token", accessToken.toString());

        mockMvc.perform(addTeamMemberRequest)
                .andExpect(status().isOk());

        var getTeamMemberRequest = get("%s/teams/%d/members".formatted(editionPath, teamId));

        mockMvc.perform(getTeamMemberRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].usosId").isEqualTo(testUserUsosId));

    }

    @Test
    @DirtiesContext
    void addProjectPreference() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();
        setupProject();

        var request = put("%s/teams/%d/project-ratings".formatted(editionPath, teamId))
                .param("rating", "3")
                .param("project-id", projectId.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("rating").isEqualTo(3));
    }

    @Test
    @DirtiesContext
    void addProjectPreferenceGetAll() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();
        setupProject();

        var request = put("%s/teams/%d/project-ratings".formatted(editionPath, teamId))
                .param("rating", "3")
                .param("project-id", projectId.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("rating").isEqualTo(3));

        var getRequest = get("%s/teams/%d/project-ratings".formatted(editionPath, teamId));

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].rating").isEqualTo(3));

    }

}
