package com.anczykowski.assigner.integrationTests;

import com.anczykowski.assigner.solver.dto.AssignOptimizationDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SolverIntegrationTests extends BaseIntegrationTests {
    @Test
    @DirtiesContext
    void AssignTwoTeamsToTwoProjects() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();

        setupProject();
        setupSecondProject();

        // user #1 / team #1
        setupTeam();

        var rateProjectRequest = put("%s/teams/%d/project-ratings".formatted(editionGroupPath, teamId))
                .param("rating", "5")
                .param("project-id", projectId.toString());

        mockMvc.perform(rateProjectRequest).andExpect(status().isOk());

        // user #2 / team #2
        authenticate(testUser2UsosId);
        setupSecondTeam();

        var rateSecondProjectRequest = put("%s/teams/%d/project-ratings".formatted(editionGroupPath, secondTeamId))
                .param("rating", "5")
                .param("project-id", secondProjectId.toString());

        mockMvc.perform(rateSecondProjectRequest).andExpect(status().isOk());


        var assignRequest = post("%s/team-project-assignment".formatted(editionGroupPath));

        var assignmentResponse = mockMvc.perform(assignRequest)
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = assignmentResponse.getResponse().getContentAsString();
        var responseDto = objectMapper.readValue(responseBody, AssignOptimizationDto.class);

        responseDto.getTeams().stream().filter(t -> t.getId().equals(teamId)).findAny().ifPresent(
                t -> assertThat(t.getAssignedProject().getId()).isEqualTo(projectId)
        );

        responseDto.getTeams().stream().filter(t -> t.getId().equals(secondTeamId)).findAny().ifPresent(
                t -> assertThat(t.getAssignedProject().getId()).isEqualTo(secondProjectId)
        );
    }

    @Test
    @DirtiesContext
    void AssignTwoTeamsToTwoProjectsWithIsAssignmentFinal() throws Exception {
        authenticate();
        setupCourseAndCourseEdition();

        setupProject();
        setupSecondProject();

        // user #1 / team #1
        setupTeam();

        var rateProjectRequest = put("%s/teams/%d/project-ratings".formatted(editionGroupPath, teamId))
                .param("rating", "4")
                .param("project-id", projectId.toString());

        mockMvc.perform(rateProjectRequest).andExpect(status().isOk());

        // assign second project manually and set it to final
        var manualAssignRequest = put("%s/teams/%d/assigned-project".formatted(editionGroupPath, teamId))
                .param("project-id", secondProjectId.toString());

        mockMvc.perform(manualAssignRequest).andExpect(status().isOk());

        var setIsFinalRequest = put("%s/teams/%d/assignment-final".formatted(editionGroupPath, teamId))
                .param("is-final", "true");

        mockMvc.perform(setIsFinalRequest).andExpect(status().isOk());

        // user #2 / team #2
        authenticate(testUser2UsosId);
        setupSecondTeam();

        var rateSecondProjectRequest = put("%s/teams/%d/project-ratings".formatted(editionGroupPath, secondTeamId))
                .param("rating", "4")
                .param("project-id", secondProjectId.toString());

        mockMvc.perform(rateSecondProjectRequest).andExpect(status().isOk());


        var assignRequest = post("%s/team-project-assignment".formatted(editionGroupPath));

        var assignmentResponse = mockMvc.perform(assignRequest)
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = assignmentResponse.getResponse().getContentAsString();
        var responseDto = objectMapper.readValue(responseBody, AssignOptimizationDto.class);

        responseDto.getTeams().stream().filter(t -> t.getId().equals(teamId)).findAny().ifPresent(
                t -> assertThat(t.getAssignedProject().getId()).isEqualTo(secondProjectId)
        );

        responseDto.getTeams().stream().filter(t -> t.getId().equals(secondTeamId)).findAny().ifPresent(
                t -> assertThat(t.getAssignedProject().getId()).isEqualTo(secondProjectId)
        );
    }

}
