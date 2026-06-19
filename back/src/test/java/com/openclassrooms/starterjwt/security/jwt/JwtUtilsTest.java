package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyForJunitTests1234567890");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void generateJwtToken_shouldReturnNonEmptyToken() {
        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void getUserNameFromJwtToken_shouldReturnCorrectUsername() {
        String token = jwtUtils.generateJwtToken(authentication);

        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(username).isEqualTo("user@test.com");
    }

    @Test
    void validateJwtToken_shouldReturnTrue_forValidToken() {
        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forMalformedToken() {
        assertThat(jwtUtils.validateJwtToken("this.is.not.valid")).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forEmptyToken() {
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forTokenWithWrongSignature() {
        // Generate token with current secret
        String token = jwtUtils.generateJwtToken(authentication);
        // Change secret to simulate wrong signature
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "aCompletelyDifferentSecretKey9999");

        assertThat(jwtUtils.validateJwtToken(token)).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forExpiredToken() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);

        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(jwtUtils.validateJwtToken(token)).isFalse();
    }
}
