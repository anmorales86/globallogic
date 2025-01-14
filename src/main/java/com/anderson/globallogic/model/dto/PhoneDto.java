package com.anderson.globallogic.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDto {

    @JsonIgnore
    private Long id;
    private Long number;
    @JsonProperty("citycode")
    private int cityCode;
    @JsonProperty("countrycode")
    private String countryCode;

}
