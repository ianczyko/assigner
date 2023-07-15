package com.anczykowski.assigner.integrationTests;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTests extends BaseIntegrationTests {

    @Test
    void protectedEndpointReturns403() throws Exception {
        var request = get("/profile");
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointAfterAuthReturns200() throws Exception {
        authenticate();
        var request = get("/profile");
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointAfterAuthAndLogoutReturns403() throws Exception {
        authenticate();

        var request = get("/profile");
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var logoutRequest = get("/logout");
        mockMvc.perform(logoutRequest)
                .andExpect(status().isOk());

        var profileRequest = get("/profile");
        mockMvc.perform(profileRequest)
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointAfterAuthAndLogoutAndAuthReturns200() throws Exception {
        authenticate();

        var request = get("/profile");
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var logoutRequest = get("/logout");
        mockMvc.perform(logoutRequest)
                .andExpect(status().isOk());

        authenticate();
        var profileRequest = get("/profile");
        mockMvc.perform(profileRequest)
                .andExpect(status().isOk());
    }

}