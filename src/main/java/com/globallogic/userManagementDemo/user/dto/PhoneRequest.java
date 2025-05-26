package com.globallogic.userManagementDemo.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequest {

    @NotNull(message = "Phone number is required")
    private Long number;

    @NotNull(message = "City code is required")
    private Integer citycode;

    @NotBlank(message = "Country code is required")
    private String countrycode;
}