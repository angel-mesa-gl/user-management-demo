package com.globallogic.userManagementDemo.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneResponse {
    private Long number;
    private Integer citycode;
    private String countrycode;
}