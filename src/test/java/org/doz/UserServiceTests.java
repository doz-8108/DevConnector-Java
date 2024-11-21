package org.doz;

import org.doz.config.security.JwtService;
import org.doz.user.UserRepository;
import org.doz.user.UserService;
import org.doz.user.utils.LoginRequestBody;
import org.doz.user.utils.RegisterRequestBody;
import org.doz.user.utils.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@SpringBootTest
public class UserServiceTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Test
    @DisplayName("It should be able to register a new user")
    public void testUserRegistration() throws NoSuchAlgorithmException {
        Mockito.when(userRepository.findByEmail(Mockito.anyString(), Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.saveAndFlush(Mockito.any()))
                .thenReturn(null);

        userService.register(new RegisterRequestBody(
                "test@test.com",
                "test",
                "password"
        ));

        Mockito.verify(userRepository)
                .saveAndFlush(Mockito.assertArg(user -> {
                    Assertions.assertEquals(
                            "https://0.gravatar.com/avatar/f660ab912ec121d1b1e928a0bb4bc61b15f5ad44d5efdc4e1c92a25e99b8e44a",
                            user.getAvatar()
                    );
                }));

        Mockito.verify(jwtService, Mockito.times(1)).generateToken("test@test.com");
    }

    @Test
    @DisplayName("It should not allow duplicate user registration")
    public void testDuplicateUserRegistration() throws NoSuchAlgorithmException {
        Mockito.when(userRepository.findByEmail(Mockito.anyString(), Mockito.any()))
                .thenReturn(Optional.of(
                        new org.doz.user.models.User()
                ));

        Assertions.assertThrows(UserException.class, () -> {
            userService.register(new RegisterRequestBody(
                    "test@test.com",
                    "test",
                    "password"
            ));
        });
    }

    @Test
    @DisplayName("It should return error of invalid credentials when user enter wrong email or password")
    public void testLoginWithInvalidCredentials()  {
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        Assertions.assertThrows(BadCredentialsException.class, () -> {
            userService.login(new LoginRequestBody(
                    "test@test.com",
                    "password"
            ));
        });
    }
}
