package com.hal.travelapp.v1.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BaseApiResponse
{
    private HttpStatus status;
    private String code;
    private String message;

    public BaseApiResponse(HttpStatus status,
                           String code,
                           String message)
    {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
