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

}
