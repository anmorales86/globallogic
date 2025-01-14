package com.anderson.globallogic.service;

import com.anderson.globallogic.model.dto.LoginUserResponseDto;
import com.anderson.globallogic.model.dto.SignUpUserResponseDto;
import com.anderson.globallogic.model.dto.UserDto;
import com.anderson.globallogic.model.entities.UserEntity;
import com.anderson.globallogic.repositories.IUserRepository;
import com.anderson.globallogic.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private IUserRepository iUserRepository;

    @Mock
    private IJwtService iJwtService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp_Success() {
        // Mock input
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("securePassword");

        // Mock repository response
        when(iUserRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(iJwtService.generateToken(userDto.getEmail())).thenReturn("mockedToken");

        // Execute method
        SignUpUserResponseDto response = userServiceImpl.signUp(userDto);

        // Assertions
        assertNotNull(response);
        assertEquals("mockedToken", response.getToken());
        verify(iUserRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testSignUp_UserAlreadyExists() {
        // Mock input
        UserDto userDto = new UserDto();
        userDto.setEmail("existing@example.com");

        // Mock repository response
        when(iUserRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new UserEntity()));

        // Execute method and expect exception
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> userServiceImpl.signUp(userDto));

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("User with email already exists", exception.getStatusText());
        verify(iUserRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testLogin_Success() {
        // Mock input
        String token = "mockedToken";

        // Mock user entity
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("john.doe@example.com");
        userEntity.setPassword("UGFzc3dvcmQxMg==");
        userEntity.setCreated(LocalDateTime.now());

        // Mock JWT service response
        when(iJwtService.getInfoUserFromToken(token)).thenReturn(userEntity);
        when(iJwtService.generateToken(userEntity.getEmail())).thenReturn("newMockedToken");

        // Execute method
        LoginUserResponseDto response = userServiceImpl.login(token);

        // Assertions
        assertNotNull(response);
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("newMockedToken", response.getToken());
        verify(iUserRepository, times(1)).save(userEntity);
    }

    @Test
    void testLogin_InvalidToken() {
        // Mock input
        String token = "invalidToken";

        // Mock JWT service response
        when(iJwtService.getInfoUserFromToken(token)).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        // Execute method and expect exception
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> userServiceImpl.login(token));

        // Assertions
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(iUserRepository, never()).save(any(UserEntity.class));
    }

}
