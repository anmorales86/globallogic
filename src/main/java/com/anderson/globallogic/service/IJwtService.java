package com.anderson.globallogic.service;

import com.anderson.globallogic.model.entities.UserEntity;

public interface IJwtService {

    String generateToken(String email);
    boolean validateToken(String token);
    UserEntity getInfoUserFromToken(String token);

}
