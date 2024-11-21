package org.doz;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.doz.config.security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtServiceTests {
    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("It should be able to identify an invalid token")
    public void testInvalidToken() {
        Assertions.assertThrows(SignatureException.class, () -> jwtService.isTokenValid(
                "eyJhbGciOiJIUzI1NiJ9.ey12JzdWIiOiJtbTY2Nzc4OTBAZ21haWwuY29tIiwiaWF0IjoxNzI2MjQzMjMyLCJleHAiOjE3MjYzMjk2MzJ9.CXuVRr6IgmXNR27kQFW6zsReOQ2FTznlEydftFG-CIk"));
    }

    @Test
    @DisplayName("It should be able to identify an expired token")
    public void testExpiredToken() {
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtbTY2Nzc4OTBAZ21haWwuY29tIiwiaWF0IjoxNzI2MzA2OTI1LCJleHAiOjE3MjYzMDY5MjZ9.NpwXCKQzi4cSgOOCOIlXtrxax42ekJNeocaiWCy0AVo"));
    }

    @Test
    @DisplayName("It should be able to generate valid token")
    public void testGenerateValidToken() {
        String testEmail = "test@test.com";
        String token = jwtService.generateToken(testEmail);
        Assertions.assertEquals(testEmail, jwtService.extractSubject(token));
        Assertions.assertTrue(jwtService.isTokenValid(token));
    }
}
