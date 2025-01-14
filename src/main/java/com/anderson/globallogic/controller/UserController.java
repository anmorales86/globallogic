package com.anderson.globallogic.controller;

import com.anderson.globallogic.model.dto.LoginUserResponseDto;
import com.anderson.globallogic.model.dto.UserDto;
import com.anderson.globallogic.model.dto.SignUpUserResponseDto;
import com.anderson.globallogic.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private IUserService iUserService;

    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpUserResponseDto> signUp(@Valid @RequestBody final UserDto request) {
        return new ResponseEntity<>(this.iUserService.signUp(request), HttpStatus.CREATED);
    }

    @GetMapping("/login")
    public ResponseEntity<LoginUserResponseDto> login(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(this.iUserService.login(token), HttpStatus.OK);
    }

}
