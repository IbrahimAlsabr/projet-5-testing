package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toDto_shouldMapUserToUserDto() {
        User user = User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        UserDto dto = userMapper.toDto(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("user@test.com");
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.isAdmin()).isFalse();
    }

    @Test
    void toEntity_shouldMapUserDtoToUser() {
        UserDto dto = new UserDto(1L, "user@test.com", "Doe", "John", false, "password", null, null);

        User user = userMapper.toEntity(dto);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("user@test.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
    }

    @Test
    void toDto_shouldMapListOfUsersToListOfDtos() {
        User u1 = User.builder().id(1L).email("a@test.com").firstName("A").lastName("A").password("pwd").admin(false).build();
        User u2 = User.builder().id(2L).email("b@test.com").firstName("B").lastName("B").password("pwd").admin(true).build();

        List<UserDto> dtos = userMapper.toDto(Arrays.asList(u1, u2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getEmail()).isEqualTo("a@test.com");
        assertThat(dtos.get(1).isAdmin()).isTrue();
    }

    @Test
    void toEntity_shouldMapListOfDtosToListOfUsers() {
        UserDto dto1 = new UserDto(1L, "a@test.com", "A", "A", false, "pwd", null, null);
        UserDto dto2 = new UserDto(2L, "b@test.com", "B", "B", true, "pwd", null, null);

        List<User> users = userMapper.toEntity(Arrays.asList(dto1, dto2));

        assertThat(users).hasSize(2);
        assertThat(users.get(0).getEmail()).isEqualTo("a@test.com");
    }

    @Test
    void toDto_shouldReturnNull_whenUserIsNull() {
        UserDto dto = userMapper.toDto((User) null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenUserDtoIsNull() {
        User user = userMapper.toEntity((UserDto) null);
        assertThat(user).isNull();
    }

    @Test
    void toDto_shouldReturnNull_whenListIsNull() {
        List<UserDto> dtos = userMapper.toDto((List<User>) null);
        assertThat(dtos).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenListIsNull() {
        List<User> users = userMapper.toEntity((List<UserDto>) null);
        assertThat(users).isNull();
    }
}
