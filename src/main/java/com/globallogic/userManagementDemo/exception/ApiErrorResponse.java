package com.globallogic.userManagementDemo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private List<ErrorResponse> error;

    public ApiErrorResponse(LocalDateTime timestamp, Integer codigo, String detail) {
        this.error = Collections.singletonList(new ErrorResponse(timestamp, codigo, detail));
    }
}