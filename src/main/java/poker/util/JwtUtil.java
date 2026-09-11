package poker.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String DEFAULT_SECRET = "PokerOnlineDevelopmentSecretKey1234567890";
    private static final String SECRET_STRING = getSecret();
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 86400000; // 1 day in ms

    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long validateTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            return null; // Invalid or expired token
        }
    }

    private static String getSecret() {
        String configuredSecret = System.getenv("POKER_JWT_SECRET");
        return configuredSecret == null || configuredSecret.isBlank()
                ? DEFAULT_SECRET
                : configuredSecret;
    }
}
