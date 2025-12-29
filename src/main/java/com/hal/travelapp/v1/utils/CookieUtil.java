package com.hal.travelapp.v1.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.time.Instant;

@Component
public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String EXPIRES_COOKIE = "expires_in";

    public void addAccessTokenCookies(HttpServletResponse response, String token, Instant expiresAt) {

        long expiresInSeconds = Duration.between(Instant.now(), expiresAt).getSeconds();
        if (expiresInSeconds < 0) {
            expiresInSeconds = 0;
        }

        // HttpOnly cookie - contains the JWT (browser cannot read it)
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)                     // JS cannot access
                .secure(true)                       // HTTPS only (set to false if testing on HTTP)
                .path("/")
                .partitioned(true)
                .sameSite("None")                  // needed for cross-origin cookies
                .maxAge(Duration.ofSeconds(expiresInSeconds))
                .build();

        // Readable cookie - frontend can access for expiry checks
        ResponseCookie expiresCookie = ResponseCookie.from(EXPIRES_COOKIE, String.valueOf(expiresInSeconds))
                .httpOnly(false)                    // frontend can read it
                .secure(true)
                .partitioned(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofSeconds(expiresInSeconds))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiresCookie.toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, ACCESS_TOKEN_COOKIE);
        clearCookie(response, EXPIRES_COOKIE);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .partitioned(true)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
