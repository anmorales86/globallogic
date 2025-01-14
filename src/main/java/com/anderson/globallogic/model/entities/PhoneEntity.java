package com.anderson.globallogic.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long number;
    @JsonProperty("citycode")
    private Integer cityCode;
    @JsonProperty("countrycode")
    private String countryCode;

    public PhoneEntity(Long number, Integer cityCode, String countryCode) {
        this.number = number;
        this.cityCode = cityCode;
        this.countryCode = countryCode;
    }
}
