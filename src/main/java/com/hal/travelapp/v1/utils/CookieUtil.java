package com.hal.travelapp.v1.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String EXPIRES_COOKIE = "expires_in";

    public void addAccessTokenCookies(HttpServletResponse response, String token, Instant expiresAt) {

        long expiresInSeconds = Duration.between(Instant.now(), expiresAt).getSeconds();
        if (expiresInSeconds < 0) {
            expiresInSeconds = 0; // token already expired or almost expired
        }


        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE, token);
        accessTokenCookie.setHttpOnly(true);             // cannot be accessed by JS
        accessTokenCookie.setPath("/");
        accessTokenCookie.setSecure(true);               // only https
        accessTokenCookie.setMaxAge((int) expiresInSeconds);

        Cookie expiresCookie = new Cookie(EXPIRES_COOKIE, String.valueOf(expiresInSeconds));
        expiresCookie.setHttpOnly(false);                // frontend can read this
        expiresCookie.setPath("/");
        expiresCookie.setSecure(true);
        expiresCookie.setMaxAge((int) expiresInSeconds);

        response.addCookie(accessTokenCookie);
        response.addCookie(expiresCookie);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, ACCESS_TOKEN_COOKIE);
        clearCookie(response, EXPIRES_COOKIE);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
