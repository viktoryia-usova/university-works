package server.server.business.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import server.server.business.dto.finance.CategoryResponse;
import server.server.business.dto.finance.CreateCategoryRequest;
import server.server.business.entity.enums.CategoryType;
import server.server.business.facade.finance.CategoryFacade;
import server.server.common.config.SecurityConfig;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CategoryFacade categoryFacade;

    // Additional beans required by SecurityConfig
    @MockBean server.server.common.config.jwt.AuthTokenFilter authTokenFilter;
    @MockBean server.server.common.config.jwt.AuthEntryPointJwt authEntryPointJwt;
    @MockBean server.server.common.config.oauth2.OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    // ---- GET /api/v1/categories ----

    @Test
    @WithMockUser(authorities = "USER")
    void getAll_returnsPageOfCategories() throws Exception {
        var page = new PageImpl<>(
                List.of(new CategoryResponse(1L, "Food", "#f00", CategoryType.EXPENSE, null)),
                PageRequest.of(0, 10), 1);

        when(categoryFacade.getMyCategories(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Food"))
                .andExpect(jsonPath("$.content[0].type").value("EXPENSE"));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isUnauthorized());
    }

    // ---- POST /api/v1/categories ----

    @Test
    @WithMockUser(authorities = "USER")
    void create_validRequest_returns201() throws Exception {
        var req  = new CreateCategoryRequest("Salary", null, CategoryType.INCOME);
        var resp = new CategoryResponse(2L, "Salary", null, CategoryType.INCOME, null);

        when(categoryFacade.create(any())).thenReturn(resp);

        mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Salary"));
    }

    // ---- DELETE /api/v1/categories/{id} ----

    @Test
    @WithMockUser(authorities = "USER")
    void delete_existingId_returns204() throws Exception {
        doNothing().when(categoryFacade).delete(5L);

        mockMvc.perform(delete("/api/v1/categories/5").with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryFacade).delete(5L);
    }
}
