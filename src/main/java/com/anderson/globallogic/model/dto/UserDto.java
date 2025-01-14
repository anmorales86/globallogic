package com.anderson.globallogic.model.dto;

import com.anderson.globallogic.validations.ValidPassword;
import lombok.*;

import javax.validation.constraints.Email;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String name;
    @Email(message = "Invalid email format")
    private String email;
    @ValidPassword
    private String password;
    private List<PhoneDto> phones;

}
