package com.example.focusflowbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Bỏ qua kiểm tra JWT cho đăng ký, đăng nhập và refresh token
        if (requestURI.startsWith("/api/register") || requestURI.startsWith("/api/login") || requestURI.startsWith("/api/refreshtoken") || requestURI.startsWith("/api/forgot-password") || requestURI.startsWith("/api/reset-password")) {
            chain.doFilter(request, response);
            return;
        }

        if (requestURI.startsWith("/v3/api-docs")
                || requestURI.startsWith("/swagger-ui")
                || requestURI.equals("/swagger-ui.html")
                || requestURI.startsWith("/actuator")
                || requestURI.startsWith("/api/payment/momo/notify")
                || requestURI.startsWith("/api/payment/momo/return")
                || requestURI.startsWith("/api/payment/vnpay/return")) {
            chain.doFilter(request, response); // Bỏ qua JWT cho Swagger, Actuator và các endpoint callback thanh toán
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
            System.out.println("Extracted Token: " + token);
            try {
                email = jwtUtil.extractEmail(token);
            } catch (Exception e) {
                System.out.println("Error extracting email from token: " + e.getMessage());
            }
        }

        if (token != null) {
            // Trước khi giải mã, kiểm tra lại token có đúng định dạng không
            try {
                Long currentUserId = jwtUtil.extractUserId(token);
                System.out.println("User ID from Token: " + currentUserId);
            } catch (Exception e) {
                System.out.println("Token error: " + e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }

}
