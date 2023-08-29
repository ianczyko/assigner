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

        var request = post(editionPath + "/projects")
                .content(new JSONObject()
                        .put("name", "name1")
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

        var request = get(editionPath + "/projects");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].name").isEqualTo("name1"))
                .andExpect(json().node("[0].description").isEqualTo("desc1"));
    }

    @Test
    @DirtiesContext
    void addProjectForumComment() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();
        setupProject();

        var request = post("%s/projects/%d/forum-comments".formatted(editionPath, projectId))
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

        var request = post("%s/projects/%d/forum-comments".formatted(editionPath, projectId))
                .content(new JSONObject()
                        .put("content", "comment_content")
                        .toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var getRequest = get("%s/projects/%d/forum-comments".formatted(editionPath, projectId));

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].content").isEqualTo("comment_content"))
                .andExpect(json().node("[0].createdDate").isNotNull())
                .andExpect(json().node("[0].author.surname").isEqualTo("Kowalski"));
    }

}
