package com.anczykowski.assigner.integrationTests;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectsIntegrationTests extends BaseIntegrationTests {
    @Test
    @DirtiesContext
    void createProject() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();

        var request = post(editionGroupPath + "/projects")
                .content(new JSONObject()
                        .put("name", "name1")
                        .put("teamLimit", 1)
                        .put("description", "desc1")
                        .toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("name").isEqualTo("name1"))
                .andExpect(json().node("description").isEqualTo("desc1"));
    }

    @Test
    @DirtiesContext
    void getProject() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupProject();

        var request = get(editionGroupPath + "/projects");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].name").isEqualTo("name1"));
    }

    @Test
    @DirtiesContext
    void createProjectWithManager() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();

        var request = post(editionGroupPath + "/projects")
                .content(new JSONObject()
                        .put("name", "name1")
                        .put("teamLimit", 1)
                        .put("description", "desc1")
                        .put("projectManager", "Bill")
                        .toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("name").isEqualTo("name1"))
                .andExpect(json().node("description").isEqualTo("desc1"))
                .andExpect(json().node("projectManager").isEqualTo("Bill"));
    }

    @Test
    @DirtiesContext
    void addProjectForumComment() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupProject();

        var request = post("%s/projects/%d/forum-comments".formatted(editionGroupPath, projectId))
                .content(new JSONObject()
                        .put("content", "comment_content")
                        .toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("content").isEqualTo("comment_content"))
                .andExpect(json().node("createdDate").isNotNull())
                .andExpect(json().node("author.surname").isEqualTo("Kowalski"));
    }

    @Test
    @DirtiesContext
    void getProjectForumComments() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupProject();

        var request = post("%s/projects/%d/forum-comments".formatted(editionGroupPath, projectId))
                .content(new JSONObject()
                        .put("content", "comment_content")
                        .toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var getRequest = get("%s/projects/%d/forum-comments".formatted(editionGroupPath, projectId));

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].content").isEqualTo("comment_content"))
                .andExpect(json().node("[0].createdDate").isNotNull())
                .andExpect(json().node("[0].author.surname").isEqualTo("Kowalski"));
    }

    @Test
    @DirtiesContext
    void removeTeam() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupProject();

        var deleteProjectRequest = delete("%s/projects/%d".formatted(editionGroupPath, projectId));

        mockMvc.perform(deleteProjectRequest)
                .andExpect(status().isOk());

        var getProjectRequest = get("%s/projects/%d".formatted(editionGroupPath, projectId));

        mockMvc.perform(getProjectRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void changeTeamLimit() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupProject();

        var changeTeamLimitRequest = put("%s/projects/%d/limit".formatted(editionGroupPath, projectId))
                .queryParam("new_limit", "11");

        mockMvc.perform(changeTeamLimitRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("teamLimit").isEqualTo(11));

    }

    @Test
    @DirtiesContext
    void updateDescription() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupProject();

        var updateDescriptionRequest = put(editionGroupPath + "/projects/%s/description".formatted(projectId))
                .queryParam("new-description", "XYZ");

        mockMvc.perform(updateDescriptionRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("name").isEqualTo("name1"))
                .andExpect(json().node("description").isEqualTo("XYZ"));

    }

}
