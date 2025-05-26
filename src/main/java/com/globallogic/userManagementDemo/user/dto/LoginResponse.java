package com.globallogic.userManagementDemo.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat; // NEW import

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private UUID id;

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a", locale = "en", timezone = "America/Bogota") // ADDED
    private LocalDateTime created;

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a", locale = "en", timezone = "America/Bogota") // ADDED
    private LocalDateTime lastLogin;

    private String token;
    private Boolean isActive;
    private String name;
    private String email;
    private String password;
    private List<PhoneResponse> phones;
}