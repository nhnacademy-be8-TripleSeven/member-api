package com.example.msamemberapi.common.interceptor;

import com.example.msamemberapi.common.annotations.secure.SecureKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthHeaderInterceptor implements HandlerInterceptor {

    @SecureKey("secret.keys.auth.value")
    private String authValue;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader = " + authHeader);
        System.out.println("authValue = " + authValue);
        if (authHeader == null || !authHeader.equals(authValue)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Authorization header is missing or invalid.");
            return false;
        }

        return true;
    }
}
