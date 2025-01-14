package com.anderson.globallogic.service;

import com.anderson.globallogic.model.dto.LoginUserResponseDto;
import com.anderson.globallogic.model.dto.UserDto;
import com.anderson.globallogic.model.dto.SignUpUserResponseDto;

public interface IUserService {

    SignUpUserResponseDto signUp(UserDto request);
    LoginUserResponseDto login(String token);

}
