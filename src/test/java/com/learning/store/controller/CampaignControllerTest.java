package com.learning.store.controller;

import com.learning.store.dto.CampaignRequestDto;
import com.learning.store.dto.CampaignSummaryDto;
import com.learning.store.exception.ConflictException;
import com.learning.store.exception.GlobalExceptionHandler;
import com.learning.store.exception.ResourceNotFoundException;
import com.learning.store.model.CampaignStatus;
import com.learning.store.service.CampaignService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Loads only the web layer with the service mocked, so the test covers request
 * mapping, the snake_case JSON the frontend relies on, and error translation --
 * all without a database. @WithMockUser stands in for a logged-in caller; the
 * real authorisation rules are exercised end to end by the Selenium suite.
 */
@WebMvcTest(controllers = CampaignController.class)
@Import({GlobalExceptionHandler.class, CampaignControllerTest.MockedServices.class})
@WithMockUser
class CampaignControllerTest {

    @TestConfiguration
    static class MockedServices {
        @Bean
        CampaignService campaignService() {
            return org.mockito.Mockito.mock(CampaignService.class);
        }

        /**
         * SecurityConfig is picked up by the slice and pulls in the JWT filter, so
         * the filter's own dependencies have to be satisfied even though no request
         * here carries a token -- @WithMockUser supplies the authentication instead.
         */
        @Bean
        com.learning.store.security.JwtService jwtService() {
            return new com.learning.store.security.JwtService(
                    "test-secret-test-secret-test-secret-test-secret", 60_000L);
        }

        @Bean
        com.learning.store.repository.UserRepository userRepository() {
            return org.mockito.Mockito.mock(com.learning.store.repository.UserRepository.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CampaignService campaignService;

    private CampaignSummaryDto sampleCampaign() {
        return new CampaignSummaryDto(
                10,
                "Clean Water",
                "Wells for rural villages",
                new BigDecimal("50000.00"),
                new BigDecimal("1200.00"),
                CampaignStatus.ACTIVE,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                null,
                null,
                null);
    }

    @Test
    @DisplayName("GET /campaigns wraps the list under a \"campaigns\" key")
    void getAllCampaignsReturnsWrappedList() throws Exception {
        when(campaignService.getAllCampaigns(isNull())).thenReturn(List.of(sampleCampaign()));

        mockMvc.perform(get("/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaigns").isArray())
                .andExpect(jsonPath("$.campaigns[0].title").value("Clean Water"))
                .andExpect(jsonPath("$.campaigns[0].target_amount").value(50000.00))
                .andExpect(jsonPath("$.campaigns[0].status").value("active"));
    }

    @Test
    @DisplayName("GET /campaigns passes the search term through to the service")
    void getAllCampaignsForwardsSearchTerm() throws Exception {
        when(campaignService.getAllCampaigns(eq("water"))).thenReturn(List.of(sampleCampaign()));

        mockMvc.perform(get("/campaigns").param("search", "water"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaigns[0].title").value("Clean Water"));
    }

    @Test
    @DisplayName("A missing campaign is translated to 404 with a message")
    void getCampaignByIdReturnsNotFound() throws Exception {
        when(campaignService.getCampaignById(404))
                .thenThrow(new ResourceNotFoundException("Campaign not found with id: 404"));

        mockMvc.perform(get("/campaigns/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Campaign not found with id: 404"));
    }

    @Test
    @DisplayName("Creating a campaign answers 201 with the created record")
    void createCampaignReturnsCreated() throws Exception {
        // The principal is resolved by Spring Security, so the test only pins the body.
        when(campaignService.createCampaign(any(CampaignRequestDto.class), any()))
                .thenReturn(sampleCampaign());

        String body = """
                {
                  "title": "Clean Water",
                  "description": "Wells for rural villages",
                  "target_amount": 50000.00,
                  "start_date": "2026-01-01",
                  "end_date": "2026-12-31"
                }
                """;

        mockMvc.perform(post("/campaigns")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campaign.id").value(10))
                .andExpect(jsonPath("$.campaign.title").value("Clean Water"));
    }

    @Test
    @DisplayName("Deleting a campaign that has donations answers 409 rather than losing the history")
    void deleteCampaignWithDonationsReturnsConflict() throws Exception {
        doThrow(new ConflictException("Campaign has 3 donation(s) and cannot be deleted"))
                .when(campaignService).deleteCampaign(10);

        mockMvc.perform(delete("/campaigns/10"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Campaign has 3 donation(s) and cannot be deleted"));
    }

    @Test
    @DisplayName("Deleting a campaign with no donations answers 204")
    void deleteCampaignReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/campaigns/10"))
                .andExpect(status().isNoContent());
    }
}
