package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build();

        session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("A nice session")
                .users(new ArrayList<>())
                .build();
    }

    @Test
    void create_shouldSaveAndReturnSession() {
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session result = sessionService.create(session);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Yoga Session");
        verify(sessionRepository).save(session);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void findAll_shouldReturnAllSessions() {
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(session));

        List<Session> result = sessionService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Yoga Session");
    }

    @Test
    void getById_shouldReturnSession_whenFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_shouldReturnNull_whenNotFound() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        Session result = sessionService.getById(99L);

        assertThat(result).isNull();
    }

    @Test
    void update_shouldSetIdAndSaveSession() {
        Session updated = Session.builder()
                .name("Updated Session")
                .date(new Date())
                .description("Updated")
                .users(new ArrayList<>())
                .build();

        when(sessionRepository.save(any(Session.class))).thenReturn(updated);

        Session result = sessionService.update(1L, updated);

        assertThat(updated.getId()).isEqualTo(1L);
        verify(sessionRepository).save(updated);
    }

    // ─── participate ──────────────────────────────────────────────────────────

    @Test
    void participate_shouldAddUserToSession_whenValid() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.participate(1L, 1L);

        assertThat(session.getUsers()).contains(user);
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldThrowNotFoundException_whenSessionNotFound() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> sessionService.participate(99L, 1L));
    }

    @Test
    void participate_shouldThrowNotFoundException_whenUserNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 99L));
    }

    @Test
    void participate_shouldThrowBadRequestException_whenAlreadyParticipating() {
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    }

    // ─── noLongerParticipate ─────────────────────────────────────────────────

    @Test
    void noLongerParticipate_shouldRemoveUserFromSession_whenValid() {
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.noLongerParticipate(1L, 1L);

        assertThat(session.getUsers()).doesNotContain(user);
        verify(sessionRepository).save(session);
    }

    @Test
    void noLongerParticipate_shouldThrowNotFoundException_whenSessionNotFound() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(99L, 1L));
    }

    @Test
    void noLongerParticipate_shouldThrowBadRequestException_whenNotParticipating() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }
}
