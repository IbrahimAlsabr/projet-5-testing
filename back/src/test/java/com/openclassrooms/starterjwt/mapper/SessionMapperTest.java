package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class SessionMapperTest {

    @Autowired
    private SessionMapper sessionMapper;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private UserService userService;

    // ─── toDto (Session → SessionDto) ────────────────────────────────────────

    @Test
    void toDto_shouldReturnNull_whenSessionIsNull() {
        SessionDto dto = sessionMapper.toDto((Session) null);
        assertThat(dto).isNull();
    }

    @Test
    void toDto_shouldMapSession_withNullTeacher() {
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date()).description("Desc")
                .teacher(null)
                .users(Collections.emptyList())
                .build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto).isNotNull();
        assertThat(dto.getTeacher_id()).isNull();
    }

    @Test
    void toDto_shouldMapSession_withTeacher() {
        Teacher teacher = Teacher.builder().id(1L).firstName("A").lastName("B").build();
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date()).description("Desc")
                .teacher(teacher)
                .users(Collections.emptyList())
                .build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto).isNotNull();
        assertThat(dto.getTeacher_id()).isEqualTo(1L);
    }

    @Test
    void toDto_shouldMapSession_withNullUsersList() {
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date()).description("Desc")
                .teacher(null)
                .users(null)
                .build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto).isNotNull();
        assertThat(dto.getUsers()).isEmpty();
    }

    @Test
    void toDto_shouldMapSession_withUsers() {
        User user = User.builder().id(1L).email("u@test.com").firstName("U").lastName("U")
                .password("pwd").admin(false).build();
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date()).description("Desc")
                .teacher(null)
                .users(Arrays.asList(user))
                .build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getUsers()).containsExactly(1L);
    }

    @Test
    void toDto_shouldReturnNull_whenTeacherHasNullId() {
        Teacher teacher = Teacher.builder().firstName("A").lastName("B").build();
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date()).description("Desc")
                .teacher(teacher)
                .users(Collections.emptyList())
                .build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getTeacher_id()).isNull();
    }

    @Test
    void toDto_list_shouldReturnNull_whenListIsNull() {
        List<SessionDto> dtos = sessionMapper.toDto((List<Session>) null);
        assertThat(dtos).isNull();
    }

    @Test
    void toDto_list_shouldMapListOfSessions() {
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date()).description("Desc")
                .teacher(null).users(Collections.emptyList()).build();

        List<SessionDto> dtos = sessionMapper.toDto(Arrays.asList(session));

        assertThat(dtos).hasSize(1);
    }

    // ─── toEntity (SessionDto → Session) ─────────────────────────────────────

    @Test
    void toEntity_shouldReturnNull_whenSessionDtoIsNull() {
        Session session = sessionMapper.toEntity((SessionDto) null);
        assertThat(session).isNull();
    }

    @Test
    void toEntity_shouldMapDto_withNullTeacherId() {
        SessionDto dto = new SessionDto();
        dto.setName("Yoga"); dto.setDate(new Date()); dto.setDescription("Desc");
        dto.setTeacher_id(null); dto.setUsers(Collections.emptyList());

        Session session = sessionMapper.toEntity(dto);

        assertThat(session).isNotNull();
        assertThat(session.getTeacher()).isNull();
    }

    @Test
    void toEntity_shouldMapDto_withExistingTeacherId() {
        Teacher teacher = Teacher.builder().id(1L).firstName("A").lastName("B").build();
        when(teacherService.findById(1L)).thenReturn(teacher);

        SessionDto dto = new SessionDto();
        dto.setName("Yoga"); dto.setDate(new Date()); dto.setDescription("Desc");
        dto.setTeacher_id(1L); dto.setUsers(Collections.emptyList());

        Session session = sessionMapper.toEntity(dto);

        assertThat(session.getTeacher()).isEqualTo(teacher);
    }

    @Test
    void toEntity_shouldMapDto_withNullUsersList() {
        SessionDto dto = new SessionDto();
        dto.setName("Yoga"); dto.setDate(new Date()); dto.setDescription("Desc");
        dto.setTeacher_id(null); dto.setUsers(null);

        Session session = sessionMapper.toEntity(dto);

        assertThat(session.getUsers()).isEmpty();
    }

    @Test
    void toEntity_shouldMapDto_withExistingUser() {
        User user = User.builder().id(1L).email("u@test.com").firstName("U").lastName("U")
                .password("pwd").admin(false).build();
        when(userService.findById(1L)).thenReturn(user);

        SessionDto dto = new SessionDto();
        dto.setName("Yoga"); dto.setDate(new Date()); dto.setDescription("Desc");
        dto.setTeacher_id(null); dto.setUsers(Arrays.asList(1L));

        Session session = sessionMapper.toEntity(dto);

        assertThat(session.getUsers()).contains(user);
    }

    @Test
    void toEntity_shouldMapDto_withNonExistingUser() {
        when(userService.findById(99L)).thenReturn(null);

        SessionDto dto = new SessionDto();
        dto.setName("Yoga"); dto.setDate(new Date()); dto.setDescription("Desc");
        dto.setTeacher_id(null); dto.setUsers(Arrays.asList(99L));

        Session session = sessionMapper.toEntity(dto);

        // user not found: lambda returns null
        assertThat(session.getUsers()).hasSize(1);
        assertThat(session.getUsers().get(0)).isNull();
    }

    @Test
    void toEntity_list_shouldReturnNull_whenListIsNull() {
        List<Session> sessions = sessionMapper.toEntity((List<SessionDto>) null);
        assertThat(sessions).isNull();
    }

    @Test
    void toEntity_list_shouldMapListOfDtos() {
        SessionDto dto = new SessionDto();
        dto.setName("Yoga"); dto.setDate(new Date()); dto.setDescription("Desc");
        dto.setTeacher_id(null); dto.setUsers(Collections.emptyList());

        List<Session> sessions = sessionMapper.toEntity(Arrays.asList(dto));

        assertThat(sessions).hasSize(1);
    }
}
