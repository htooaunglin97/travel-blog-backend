package com.hal.travelapp.v1.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Getter
public class ApiError  extends BaseApiResponse{

    private final List<String> errors;

    public ApiError(HttpStatus status,
                    String code,
                    String message,
                    List<String> errors) {
        super(status, code, message);
        this.errors = errors;
    }

    public ApiError(HttpStatus status,
                    String code,
                    String message,
                    String error) {
        super(status, code, message);
        this.errors = Collections.singletonList(error);
    }
}
