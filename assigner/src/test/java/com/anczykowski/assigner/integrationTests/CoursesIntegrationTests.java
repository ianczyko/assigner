package com.anczykowski.assigner.integrationTests;

import com.anczykowski.assigner.users.models.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        var request = post("/courses").param("name", "PZSP3");
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var file = new MockMultipartFile(
                "file",
                "students.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "nazwisko;imie;imie2;skreslony;rezygnacja;login_office365;grupy\nKowalski;Jan;;0;0;12345678@pw.edu.pl;\"CWI101, PRO101, WYK1\"".getBytes()
        );
        var courseEditionRequest = multipart("/courses/%s/editions".formatted(courseName))
                .file(file)
                .param("edition", "21l")
                .cookie(cookie);

        mockMvc.perform(courseEditionRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("edition").isEqualTo("21l"));

        var getCourseEditionGroupRequest = get(editionGroupPath);
        mockMvc.perform(getCourseEditionGroupRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("users[0].usosId").isEqualTo("12345678"))
                .andExpect(json().node("users[0].name").isEqualTo("Jan"))
                .andExpect(json().node("users[0].surname").isEqualTo("Kowalski"))
        ;
    }

    @Test
    @DirtiesContext
    void forbiddenAccessOnCourseEditionGroup() throws Exception {
        authenticate();
        var request = post("/courses").param("name", "PZSP3");
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var file = new MockMultipartFile(
                "file",
                "students.csv",
                MediaType.TEXT_PLAIN_VALUE,
                // Note the usosId different from one present in authenticate()
                "nazwisko;imie;imie2;skreslony;rezygnacja;login_office365;grupy\nKowalski;Jan;;0;0;11122233@pw.edu.pl;\"CWI101, PRO101, WYK1\"".getBytes()
        );
        var courseEditionRequest = multipart("/courses/PZSP3/editions")
                .file(file)
                .param("edition", "21l")
                .cookie(cookie);

        mockMvc.perform(courseEditionRequest)
                .andExpect(status().isOk())
                .andExpect(json().node("edition").isEqualTo("21l"));


        authenticate(testUserUsosId, UserType.STUDENT);
        var getCourseEditionGroupRequest = get(editionGroupPath);
        mockMvc.perform(getCourseEditionGroupRequest)
                .andExpect(status().isForbidden())
        ;
    }

}
