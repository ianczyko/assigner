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

        var request = post(editionPath + "/projects")
                .content(new JSONObject()
                        .put("name", "name1")
                        .put("description", "desc1")
                        .toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var getRequest = get(editionPath + "/projects");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].name").isEqualTo("name1"))
                .andExpect(json().node("[0].description").isEqualTo("desc1"));
    }

}
