package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        session = Session.builder()
                .id(1L)
                .name("Yoga Morning")
                .date(new Date())
                .description("A relaxing morning session")
                .users(Collections.emptyList())
                .build();

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Morning");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setDescription("A relaxing morning session");
        sessionDto.setUsers(Collections.emptyList());
    }

    // ─── GET /api/session/{id} ────────────────────────────────────────────────

    @Test
    @WithMockUser
    void findById_shouldReturn200_whenSessionFound() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Morning"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturn404_whenSessionNotFound() throws Exception {
        when(sessionService.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/session/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /api/session ─────────────────────────────────────────────────────

    @Test
    @WithMockUser
    void findAll_shouldReturn200WithList() throws Exception {
        when(sessionService.findAll()).thenReturn(Arrays.asList(session));
        when(sessionMapper.toDto(Arrays.asList(session))).thenReturn(Arrays.asList(sessionDto));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ─── POST /api/session ────────────────────────────────────────────────────

    @Test
    @WithMockUser
    void create_shouldReturn200_withCreatedSession() throws Exception {
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.create(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Morning"));
    }

    // ─── PUT /api/session/{id} ────────────────────────────────────────────────

    @Test
    @WithMockUser
    void update_shouldReturn200_withUpdatedSession() throws Exception {
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.update(eq(1L), any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(sessionDto);

        mockMvc.perform(put("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Morning"));
    }

    @Test
    @WithMockUser
    void update_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(put("/api/session/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    // ─── DELETE /api/session/{id} ─────────────────────────────────────────────

    @Test
    @WithMockUser
    void delete_shouldReturn200_whenSessionFound() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_shouldReturn404_whenSessionNotFound() throws Exception {
        when(sessionService.getById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void delete_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(delete("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    // ─── POST /api/session/{id}/participate/{userId} ──────────────────────────

    @Test
    @WithMockUser
    void participate_shouldReturn200_whenValid() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void participate_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/2"))
                .andExpect(status().isBadRequest());
    }

    // ─── DELETE /api/session/{id}/participate/{userId} ────────────────────────

    @Test
    @WithMockUser
    void noLongerParticipate_shouldReturn200_whenValid() throws Exception {
        mockMvc.perform(delete("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void noLongerParticipate_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(delete("/api/session/abc/participate/2"))
                .andExpect(status().isBadRequest());
    }
}
