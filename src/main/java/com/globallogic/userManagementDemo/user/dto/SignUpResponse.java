package com.globallogic.userManagementDemo.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {

    private UUID id;

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a", locale = "en", timezone = "America/Bogota")
    private LocalDateTime created;

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a", locale = "en", timezone = "America/Bogota")
    private LocalDateTime lastLogin;

    private String token;
    private Boolean isActive;
}