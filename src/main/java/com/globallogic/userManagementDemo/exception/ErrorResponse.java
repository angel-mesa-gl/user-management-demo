package com.globallogic.userManagementDemo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a", locale = "en", timezone = "America/Bogota")
    private LocalDateTime timestamp;
    private Integer codigo;
    private String detail;
}