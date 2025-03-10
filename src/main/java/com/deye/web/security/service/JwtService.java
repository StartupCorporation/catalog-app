package com.deye.web.security.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.public.key}")
    private String publicKey;

    public Claims validateToken(String token) {
        try {
            log.info("Validating JWT");
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKeyObject = keyFactory.generatePublic(keySpec);

            Claims claims = Jwts.parser()
                    .setSigningKey(publicKeyObject)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("JWT is validated. Subject: {}", claims.getSubject());
            return claims;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JwtException("Error during validating JWT");
        }
    }
}
