package com.anczykowski.assigner.integrationTests;

import com.anczykowski.assigner.users.models.UserType;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsersIntegrationTests extends BaseIntegrationTests {
    @Test
    @DirtiesContext
    void manualAddAndGetUser() throws Exception {
        authenticate();

        manualAddUser(testUser2UsosId, UserType.STUDENT);

        var request = get("/users");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonIgnoringWrapper().isEqualTo(jsonArray(
                        new JSONObject().put("usosId", testUser2UsosId)
                )));

    }


    @Test
    @DirtiesContext
    void changeUserRole() throws Exception {
        authenticate();

        manualAddUser(testUser2UsosId, UserType.STUDENT);

        var getRequest = get("/users");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonIgnoringWrapper().isEqualTo(jsonArray(
                        new JSONObject()
                                .put("usosId", testUser2UsosId)
                                .put("userType", UserType.STUDENT.ordinal())
                )));

        var changeRoleRequest = put("/users/%s/role".formatted(testUser2UsosId))
                .queryParam("new-role", String.valueOf(UserType.TEACHER.ordinal()));

        mockMvc.perform(changeRoleRequest)
                .andExpect(status().isOk());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonIgnoringWrapper().isEqualTo(jsonArray(
                        new JSONObject()
                                .put("usosId", testUser2UsosId)
                                .put("userType", UserType.TEACHER.ordinal())
                )));

    }

    void manualAddUser(Integer usosId, UserType userType) throws Exception {
        var request = post("/users")
                .content(new JSONObject()
                        .put("usosId", usosId)
                        .put("userType", userType.ordinal())
                        .put("name", "test")
                        .put("surname", "test")
                        .toString());

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

}
