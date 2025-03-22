package com.example.focusflowbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "c3VwZXJzZWNyZXRrZXlmb3Jqd3Rwcm9qZWN0MTIzNDU2";

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Tạo token với email, userId và role
    public String generateToken(String email, Long userId, String role) {
        return Jwts.builder()
                .setSubject(email) // Subject vẫn là email
                .claim("userId", userId) // Thêm userId vào token
                .claim("role", role) // Thêm role vào token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Hết hạn sau 10 giờ
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Lấy userId từ token
    public Long extractUserId(String token) {
        token = token.replace("Bearer ", "").trim();
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            System.out.println("Error extracting userId from token: " + e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

    // Lấy role từ token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Lấy email từ token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Kiểm tra token có hợp lệ không
    public boolean validateToken(String token, String userEmail) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(userEmail) && !isTokenExpired(token));
    }

    // Kiểm tra token đã hết hạn chưa
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Lấy thời gian hết hạn từ token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Lấy tất cả claims từ token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Trích xuất một claim cụ thể từ token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            System.out.println("Error extracting claim from token: " + e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage());
        }
    }

}
