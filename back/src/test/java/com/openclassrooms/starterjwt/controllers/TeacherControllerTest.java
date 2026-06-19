package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeacherController.class)
@MockBean(JpaMetamodelMappingContext.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    // ─── GET /api/teacher/{id} ────────────────────────────────────────────────

    @Test
    @WithMockUser
    void findById_shouldReturn200_whenTeacherFound() throws Exception {
        Teacher teacher = Teacher.builder().id(1L).firstName("Alice").lastName("Smith").build();
        TeacherDto dto = new TeacherDto(1L, "Smith", "Alice", null, null);

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(dto);

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturn404_whenTeacherNotFound() throws Exception {
        when(teacherService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/teacher/abc"))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /api/teacher ─────────────────────────────────────────────────────

    @Test
    @WithMockUser
    void findAll_shouldReturn200WithListOfTeachers() throws Exception {
        Teacher t1 = Teacher.builder().id(1L).firstName("Alice").lastName("Smith").build();
        Teacher t2 = Teacher.builder().id(2L).firstName("Bob").lastName("Jones").build();
        List<Teacher> teachers = Arrays.asList(t1, t2);

        TeacherDto dto1 = new TeacherDto(1L, "Smith", "Alice", null, null);
        TeacherDto dto2 = new TeacherDto(2L, "Jones", "Bob", null, null);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    void findAll_shouldReturn200WithEmptyList_whenNoTeachers() throws Exception {
        when(teacherService.findAll()).thenReturn(Collections.emptyList());
        when(teacherMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
