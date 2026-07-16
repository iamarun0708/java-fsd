package com.cognizant.springlearn.controller;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @GetMapping("/authenticate")
    public ResponseEntity<Map<String, String>> authenticate(@RequestHeader("Authorization") String authHeader) {
        LOGGER.info("START: authenticate()");
        LOGGER.debug("authHeader: {}", authHeader);

        String user = getUser(authHeader);
        if (user == null || user.isEmpty()) {
            LOGGER.warn("Invalid Authorization header credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = generateJwt(user);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        LOGGER.info("END: authenticate() generated token successfully.");
        return ResponseEntity.ok(response);
    }

    private String getUser(String authHeader) {
        LOGGER.info("START: getUser()");
        try {
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                return null;
            }
            String encodedCredentials = authHeader.substring(6);
            byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
            String decodedString = new String(decodedBytes);
            LOGGER.debug("Decoded credentials: {}", decodedString);

            // Decoded format will be "username:password"
            int colonIndex = decodedString.indexOf(":");
            if (colonIndex != -1) {
                String username = decodedString.substring(0, colonIndex);
                String password = decodedString.substring(colonIndex + 1);

                // Authenticate hardcoded users
                if (("admin".equals(username) && "pwd".equals(password)) || 
                    ("user".equals(username) && "pwd".equals(password))) {
                    LOGGER.info("END: getUser() authenticated: {}", username);
                    return username;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error decoding Basic authorization header", e);
        }
        LOGGER.warn("END: getUser() authentication failed.");
        return null;
    }

    private String generateJwt(String user) {
        LOGGER.info("START: generateJwt() for user: {}", user);
        JwtBuilder builder = Jwts.builder();
        builder.setSubject(user);
        builder.setIssuedAt(new Date());
        // Set token expiry as 20 minutes from now (1200000 ms)
        builder.setExpiration(new Date((new Date()).getTime() + 1200000));
        builder.signWith(SignatureAlgorithm.HS256, "secretkey");
        String token = builder.compact();
        LOGGER.info("END: generateJwt()");
        return token;
    }
}
