package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void findById_shouldReturnUser_whenFound() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .admin(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void findById_shouldReturnNull_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.findById(99L);

        assertThat(result).isNull();
    }
}
