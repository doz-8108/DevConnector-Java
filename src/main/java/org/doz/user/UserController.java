package org.doz.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.antlr.v4.runtime.Token;
import org.doz.user.models.User;
import org.doz.user.projections.UserProjection;
import org.doz.user.utils.LoginRequestBody;
import org.doz.user.utils.RegisterRequestBody;
import org.doz.user.utils.UserException;
import org.doz.utils.R;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User already exists", content = @Content(schema = @Schema(implementation = R.class)))
    })
    public ResponseEntity<R<TokenData>> register(@RequestBody @Valid RegisterRequestBody user) throws NoSuchAlgorithmException {
        String token = userService.register(user);
        return ResponseEntity.ok(R.<TokenData>builder()
                                         .data(new TokenData(token))
                                         .build());
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    @ApiResponse(responseCode = "200", description = "logged in successfully")
    public ResponseEntity<R<TokenData>> login(@RequestBody @Valid LoginRequestBody user) {
        String token = userService.login(user);
        return ResponseEntity.ok(R.<TokenData>builder()
                                         .data(new TokenData(token))
                                         .build());
    }

    @DeleteMapping
    @Operation(summary = "Delete an user account")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    public ResponseEntity<R<String>> deleteAccount() {
        userService.deleteUser();
        return ResponseEntity.ok(R.<String>builder()
                                         .message("User deleted successfully")
                                         .build());
    }

    @ExceptionHandler({UserException.class, BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<R> handleUserExceptions(Exception e) {
        R resp = R.builder()
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest()
                .body(resp);
    }

    @Data
    @AllArgsConstructor
    static class TokenData {
        private String token;
    }
}
