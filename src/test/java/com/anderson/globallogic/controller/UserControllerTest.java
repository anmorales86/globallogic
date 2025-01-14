package com.anderson.globallogic.controller;

import com.anderson.globallogic.model.dto.LoginUserResponseDto;
import com.anderson.globallogic.model.dto.PhoneDto;
import com.anderson.globallogic.model.dto.SignUpUserResponseDto;
import com.anderson.globallogic.model.dto.UserDto;
import com.anderson.globallogic.service.IJwtService;
import com.anderson.globallogic.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class UserControllerTest {

     @Mock
     private IUserService iUserService;
     @Mock
     private IJwtService iJwtService;

     @InjectMocks
     private UserController userController;

     @BeforeEach
     void setUp() {
         MockitoAnnotations.openMocks(this);
     }

     @Test
     void testSignUp_Success() {
         // Arrange
         UserDto userDto = new UserDto();
         userDto.setName("John Doe");
         userDto.setEmail("john.doe@example.com");
         userDto.setPassword("securePassword");

         SignUpUserResponseDto signUpResponse = new SignUpUserResponseDto();
         signUpResponse.setActive(true);
         signUpResponse.setId(UUID.fromString("099fe8aa-bef8-46ed-8685-af5f35cb3230"));

         when(iUserService.signUp(userDto)).thenReturn(signUpResponse);

         // Act
         ResponseEntity<SignUpUserResponseDto> response = userController.signUp(userDto);

         // Assert
         assertNotNull(response);
         assertEquals(HttpStatus.CREATED, response.getStatusCode());
         assertNotNull(response.getBody());
         assertEquals("099fe8aa-bef8-46ed-8685-af5f35cb3230", response.getBody().getId().toString());

         verify(iUserService, times(1)).signUp(userDto);
     }

     @Test
     void testSignUp_UserAlreadyExists() {
         // Arrange
         UserDto userDto = new UserDto();
         userDto.setEmail("existing@example.com");

         when(iUserService.signUp(userDto))
                 .thenThrow(new IllegalArgumentException("User already exists"));

         // Act & Assert
         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                 () -> userController.signUp(userDto));

         assertEquals("User already exists", exception.getMessage());
         verify(iUserService, times(1)).signUp(userDto);
     }

     @Test
     void testLogin_Success() {
         // Arrange
         LocalDateTime localDateTime = LocalDateTime.now();
         String token = "Bearer validToken";
         UUID uuid = UUID.randomUUID();
         LoginUserResponseDto loginResponse = new LoginUserResponseDto();
         loginResponse.setEmail("john.doe@example.com");
         loginResponse.setName("John Doe");
         loginResponse.setId(uuid);
         loginResponse.setActive(true);
         loginResponse.setCreated(localDateTime);
         loginResponse.setLastLogin(localDateTime);
         loginResponse.setToken(token);
         PhoneDto phone = new PhoneDto();
         phone.setId(123L);
         phone.setNumber(34567L);
         phone.setCityCode(1);
         phone.setCountryCode("+57");
         loginResponse.setPhones(List.of(phone));



         when(iUserService.login(anyString())).thenReturn(loginResponse);

         // Act
         ResponseEntity<LoginUserResponseDto> response = userController.login(token);

         // Assert
         assertNotNull(response);
         assertEquals(HttpStatus.OK, response.getStatusCode());
         assertNotNull(response.getBody());
         assertEquals("John Doe", response.getBody().getName());
         assertEquals("john.doe@example.com", response.getBody().getEmail());
         assertEquals(uuid, response.getBody().getId());
         assertEquals(localDateTime.toString(), response.getBody().getCreated().toString());
         assertEquals(localDateTime.toString(), response.getBody().getLastLogin().toString());
         assertTrue(response.getBody().isActive());
         assertEquals(123L, response.getBody().getPhones().get(0).getId());
         assertEquals(34567L, response.getBody().getPhones().get(0).getNumber());
         assertEquals(1, response.getBody().getPhones().get(0).getCityCode());
         assertEquals("+57", response.getBody().getPhones().get(0).getCountryCode());


         verify(iUserService, times(1)).login(anyString());
     }


}
