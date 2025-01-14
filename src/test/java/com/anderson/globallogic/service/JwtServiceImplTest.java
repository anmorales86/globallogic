package com.anderson.globallogic.service;

import com.anderson.globallogic.model.dto.PhoneDto;
import com.anderson.globallogic.model.entities.PhoneEntity;
import com.anderson.globallogic.model.entities.UserEntity;
import com.anderson.globallogic.repositories.IUserRepository;
import com.anderson.globallogic.service.impl.JwtServiceImpl;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceImplTest {

    @Mock
    private IUserRepository iUserRepository;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private static final String SECRET_KEY = "secret";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateToken_Success() {
        // Arrange
        String email = "user@example.com";

        // Act
        String token = jwtService.generateToken(email);

        // Assert
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        // Act
        boolean isValid = jwtService.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.value";

        // Act
        boolean isValid = jwtService.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testGetInfoUserFromToken_ValidToken() {
        // Arrange
        LocalDateTime localDateTime = LocalDateTime.now();
        String email = "user@example.com";
        UUID uuid = UUID.randomUUID();
        String token = jwtService.generateToken(email);
        UserEntity mockUser = new UserEntity();
        mockUser.setEmail(email);
        mockUser.setToken(token);

        mockUser.setEmail("john.doe@example.com");
        mockUser.setName("John Doe");
        mockUser.setId(uuid);
        mockUser.setActive(true);
        mockUser.setCreated(localDateTime);
        mockUser.setLastLogin(localDateTime);
        mockUser.setToken(token);
        PhoneEntity phone = new PhoneEntity();
        phone.setId(123L);
        phone.setNumber(34567L);
        phone.setCityCode(1);
        phone.setCountryCode("+57");
        mockUser.setPhones(List.of(phone));


        when(iUserRepository.findByToken(token)).thenReturn(Optional.of(mockUser));

        // Act
        UserEntity user = jwtService.getInfoUserFromToken("Bearer " + token);

        // Assert
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals(uuid, user.getId());
        assertEquals(localDateTime.toString(), user.getCreated().toString());
        assertEquals(localDateTime.toString(), user.getLastLogin().toString());
        assertTrue(user.isActive());
        assertEquals(123L, user.getPhones().get(0).getId());
        assertEquals(34567L, user.getPhones().get(0).getNumber());
        assertEquals(1, user.getPhones().get(0).getCityCode());
        assertEquals("+57", user.getPhones().get(0).getCountryCode());
        verify(iUserRepository, times(1)).findByToken(token);
    }

    @Test
    void testGetInfoUserFromToken_InvalidTokenFormat() {
        // Arrange
        String token = "invalid.token.value";

        // Act & Assert
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> jwtService.getInfoUserFromToken("Bearer " + token));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid or expired token", exception.getStatusText());
    }

    @Test
    void testGetInfoUserFromToken_TokenNotFoundInDatabase() {
        // Arrange
        String email = "user@example.com";
        String token = jwtService.generateToken(email);

        when(iUserRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act & Assert
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> jwtService.getInfoUserFromToken("Bearer " + token));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Token not found or mismatched", exception.getStatusText());
    }
}
