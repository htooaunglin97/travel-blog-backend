package com.hal.travelapp.v1.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ApiSuccess<T> extends BaseApiResponse
{

    private final T data;

    public ApiSuccess(HttpStatus status,
                      String code,
                      String message,
                      T data)
    {
        super(status, code, message);
        this.data = data;
    }
}
