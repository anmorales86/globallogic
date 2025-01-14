package com.anderson.globallogic.service.impl;

import com.anderson.globallogic.model.dto.LoginUserResponseDto;
import com.anderson.globallogic.model.dto.UserDto;
import com.anderson.globallogic.model.dto.SignUpUserResponseDto;
import com.anderson.globallogic.model.entities.PhoneEntity;
import com.anderson.globallogic.model.entities.UserEntity;
import com.anderson.globallogic.repositories.IUserRepository;
import com.anderson.globallogic.service.IJwtService;
import com.anderson.globallogic.service.IUserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository iUserRepository;
    private final IJwtService iJwtService;

    public UserServiceImpl(IUserRepository iUserRepository, IJwtService iJwtService) {
        this.iUserRepository = iUserRepository;
        this.iJwtService = iJwtService;
    }

    public SignUpUserResponseDto signUp(UserDto request) {
        if (iUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "User with email already exists");
        }
        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setActive(true);
        user.setToken(iJwtService.generateToken(request.getEmail()));
        user.setEmail(request.getEmail());
        user.setPassword(new String(Base64.encodeBase64(request.getPassword().getBytes())));
        user.setCreated(LocalDateTime.now());
        if (Objects.nonNull(request.getPhones()) && !request.getPhones().isEmpty()) {
            user.setPhones(request.getPhones().stream().map(data->new PhoneEntity(data.getNumber(), data.getCityCode(), data.getCountryCode())
            ).collect(Collectors.toList()));
        }
        iUserRepository.save(user);
        return new ObjectMapper().registerModule(new JavaTimeModule())
                .configure(MapperFeature.USE_ANNOTATIONS, true)
                .configure(DeserializationFeature. FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL).convertValue(user, SignUpUserResponseDto.class);
    }

    @Override
        public LoginUserResponseDto login(String token) {
        UserEntity userEntity = iJwtService.getInfoUserFromToken(token);
        userEntity.setToken(iJwtService.generateToken(userEntity.getEmail()));
        userEntity.setLastLogin(userEntity.getCreated());
        userEntity.setCreated(LocalDateTime.now());
        iUserRepository.save(userEntity);
        userEntity.setPassword(new String(Base64.decodeBase64(userEntity.getPassword().getBytes())));
        return new ObjectMapper().registerModule(new JavaTimeModule())
                .configure(MapperFeature.USE_ANNOTATIONS, true)
                .configure(DeserializationFeature. FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL).convertValue(userEntity, LoginUserResponseDto.class);
    }


}
