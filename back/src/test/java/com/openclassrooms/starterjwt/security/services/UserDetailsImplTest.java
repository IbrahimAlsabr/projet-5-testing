package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();
    }

    @Test
    void getAuthorities_shouldReturnEmptyCollection() {
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void equals_shouldReturnTrue_whenSameObject() {
        assertThat(userDetails.equals(userDetails)).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenNull() {
        assertThat(userDetails.equals(null)).isFalse();
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        assertThat(userDetails.equals("not a user")).isFalse();
    }

    @Test
    void equals_shouldReturnTrue_whenSameId() {
        UserDetailsImpl other = UserDetailsImpl.builder()
                .id(1L)
                .username("different@email.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("otherPwd")
                .build();

        assertThat(userDetails.equals(other)).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        UserDetailsImpl other = UserDetailsImpl.builder()
                .id(2L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .build();

        assertThat(userDetails.equals(other)).isFalse();
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("user@test.com");
        assertThat(userDetails.getFirstName()).isEqualTo("John");
        assertThat(userDetails.getLastName()).isEqualTo("Doe");
        assertThat(userDetails.getPassword()).isEqualTo("password");
    }
}
