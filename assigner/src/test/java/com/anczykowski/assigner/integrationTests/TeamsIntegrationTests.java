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

        var request = post(editionGroupPath + "/teams")
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

        var accessTokenRequest = put("%s/teams/%d/access-token".formatted(editionGroupPath, teamId));

        var accessTokenResult = mockMvc.perform(accessTokenRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("accessToken").isPresent())
                .andExpect(json().node("accessTokenExpirationDate").isPresent())
                .andReturn();

        var accessToken = getFromResult(accessTokenResult, "accessToken");
        var getAccessTokenRequest = get("%s/teams/%d/access-token".formatted(editionGroupPath, teamId));

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

        var accessTokenRequest = put("%s/teams/%d/access-token".formatted(editionGroupPath, teamId));

        var accessTokenResult = mockMvc.perform(accessTokenRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("accessToken").isPresent())
                .andReturn();

        var accessToken = getFromResult(accessTokenResult, "accessToken");

        authenticate(testUser2UsosId);

        var addTeamMemberRequest = post("%s/teams/%d/members".formatted(editionGroupPath, teamId))
                .param("access-token", accessToken.toString());

        mockMvc.perform(addTeamMemberRequest)
                .andExpect(status().isOk());

        var getTeamMemberRequest = get("%s/teams/%d/members".formatted(editionGroupPath, teamId));

        mockMvc.perform(getTeamMemberRequest)
                .andExpect(status().isOk())
                .andExpect(jsonIgnoringWrapper().isEqualTo(jsonArray(
                        new JSONObject().put("usosId", testUserUsosId),
                        new JSONObject().put("usosId", testUser2UsosId)
                )));

    }

    @Test
    @DirtiesContext
    void manualTeamToUserAssign() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();

        var manualTeamAssignRequest = post("%s/teams/manual-reassignment".formatted(editionGroupPath))
                .param("team-id", teamId.toString())
                .param("usos-id", testUser2UsosId.toString());

        mockMvc.perform(manualTeamAssignRequest)
                .andExpect(status().isOk());

        var getTeamMemberRequest = get("%s/teams/%d/members".formatted(editionGroupPath, teamId));

        mockMvc.perform(getTeamMemberRequest)
                .andExpect(status().isOk())
                .andExpect(jsonIgnoringWrapper().isEqualTo(jsonArray(
                        new JSONObject().put("usosId", testUserUsosId),
                        new JSONObject().put("usosId", testUser2UsosId)
                )));

    }

    @Test
    @DirtiesContext
    void manualTeamToUserAssignRemoveAssignment() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();

        var manualTeamAssignRequest = post("%s/teams/manual-reassignment".formatted(editionGroupPath))
                .param("previous-team-id", teamId.toString())
                .param("usos-id", testUserUsosId.toString());

        mockMvc.perform(manualTeamAssignRequest)
                .andExpect(status().isOk());

        var getTeamMemberRequest = get("%s/teams/%d/members".formatted(editionGroupPath, teamId));

        mockMvc.perform(getTeamMemberRequest)
                .andExpect(status().isOk())
                .andExpect(jsonIgnoringWrapper().isEqualTo(jsonArray()));

    }

    @Test
    @DirtiesContext
    void leaveTeam() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();

        var leaveTeamRequest = post("%s/teams/%d/leave".formatted(editionGroupPath, teamId));

        mockMvc.perform(leaveTeamRequest)
                .andExpect(status().isOk());

        var getTeamMemberRequest = get("%s/teams/%d/members".formatted(editionGroupPath, teamId));

        mockMvc.perform(getTeamMemberRequest)
                .andExpect(status().isOk())
                .andExpect(jsonIgnoringWrapper().isEqualTo(jsonArray()));

    }

    @Test
    @DirtiesContext
    void addProjectPreference() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();
        setupProject();

        var request = put("%s/teams/%d/project-ratings".formatted(editionGroupPath, teamId))
                .param("rating", "3")
                .param("project-id", projectId.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("rating").isEqualTo(3));
    }

    @Test
    @DirtiesContext
    void assignProjectToTeam() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();
        setupProject();

        var request = put("%s/teams/%d/assigned-project".formatted(editionGroupPath, teamId))
                .param("project-id", projectId.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("assignedProject.id").isEqualTo(projectId));
    }

    @Test
    @DirtiesContext
    void addProjectPreferenceGetAll() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();
        setupProject();

        var request = put("%s/teams/%d/project-ratings".formatted(editionGroupPath, teamId))
                .param("rating", "3")
                .param("project-id", projectId.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("rating").isEqualTo(3));

        var getRequest = get("%s/teams/%d/project-ratings".formatted(editionGroupPath, teamId));

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].rating").isEqualTo(3));

    }

    @Test
    @DirtiesContext
    void removeTeam() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupTeam();

        var deleteTeamRequest = delete("%s/teams/%d".formatted(editionGroupPath, teamId));

        mockMvc.perform(deleteTeamRequest)
                .andExpect(status().isOk());

        var getTeamRequest = get("%s/teams/%d".formatted(editionGroupPath, teamId));

        mockMvc.perform(getTeamRequest)
                .andExpect(status().isNotFound());
    }

}
