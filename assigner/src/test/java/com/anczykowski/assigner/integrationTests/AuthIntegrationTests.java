package com.anczykowski.assigner.integrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTests extends BaseIntegrationTests {

    @Test
    void protectedEndpointWithoutCredentialsReturns401() throws Exception {
        var request = get("/profile");
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void protectedEndpointAfterAuthReturns200() throws Exception {
        authenticate();
        var request = get("/profile");
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    void protectedEndpointAfterAuthAndLogoutReturns401() throws Exception {
        authenticate();

        var request = get("/profile");
        mockMvc.perform(request)
                .andExpect(status().isOk());

        var logoutRequest = get("/logout");
        mockMvc.perform(logoutRequest)
                .andExpect(status().isOk());

        var profileRequest = get("/profile");
        mockMvc.perform(profileRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
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
