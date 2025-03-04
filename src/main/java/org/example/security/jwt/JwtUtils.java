package org.example.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.security.AppUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String JwtSecret;

    @Value("${app.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    public String generateJwtToken(AppUserDetails userDetails) {
        return generateTokenFromUserName(userDetails.getUsername());
    }

    public String generateTokenFromUserName(String userName) {
        SecretKey key = getKey(JwtSecret);
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + tokenExpiration.toMillis()))
                .signWith(key)
                .compact();
    }

    public String getUserName(String token) {
        SecretKey key = getKey(JwtSecret);
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    private SecretKey getKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public boolean validate(String authToken){
        SecretKey key = getKey(JwtSecret);
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex){
            log.error("invalid signature: {}",ex.getMessage());
        } catch (MalformedJwtException ex){
            log.error("invalid token: {}",ex.getMessage());
        } catch (ExpiredJwtException ex){
            log.error("token expired: {}",ex.getMessage());
        } catch (UnsupportedJwtException ex){
            log.error("token is unsupported: {}",ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Claim string is empty: {}",ex.getMessage());
        }
        return false;
    }
}
