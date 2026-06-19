package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    private User buildUser(Long id, String email) {
        return User.builder()
                .id(id).email(email)
                .firstName("John").lastName("Doe")
                .password("pwd").admin(false).build();
    }

    private UserDto buildUserDto(Long id, String email) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setEmail(email);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAdmin(false);
        return dto;
    }

    // ─── GET /api/user/{id} ───────────────────────────────────────────────────

    @Test
    @WithMockUser
    void findById_shouldReturn200_whenUserFound() throws Exception {
        User user = buildUser(1L, "user@test.com");
        UserDto dto = buildUserDto(1L, "user@test.com");

        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    @WithMockUser
    void findById_shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/user/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/user/abc"))
                .andExpect(status().isBadRequest());
    }

    // ─── DELETE /api/user/{id} ────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "user@test.com")
    void delete_shouldReturn200_whenDeletingOwnAccount() throws Exception {
        User user = buildUser(1L, "user@test.com");
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void delete_shouldReturn401_whenDeletingAnotherUsersAccount() throws Exception {
        User user = buildUser(1L, "other@test.com");
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void delete_shouldReturn404_whenUserNotFound() throws Exception {
        when(userService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/user/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void delete_shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(delete("/api/user/abc"))
                .andExpect(status().isBadRequest());
    }
}
