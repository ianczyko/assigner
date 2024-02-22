package com.anczykowski.assigner.integrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CoursesIntegrationTests extends BaseIntegrationTests {
    @Test
    @DirtiesContext
    void createCourse() throws Exception {
        authenticate();
        var request = post("/courses").param("name", "PZSP3");
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var getCoursesRequest = get("/courses");
        mockMvc.perform(getCoursesRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("[0].name").isEqualTo("PZSP3"));
    }

    @Test
    @DirtiesContext
    void createCourseEdition() throws Exception {
        authenticate();
        var request = post("/courses").param("name", courseName);
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var courseEditionRequest = post("/courses/%s/editions".formatted(courseName))
                .param("edition", edition)
                .cookie(cookie);

        mockMvc.perform(courseEditionRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("edition").isEqualTo(edition));
    }

}
