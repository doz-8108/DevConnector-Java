package org.doz.user.utils;

import lombok.*;
import jakarta.validation.constraints.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestBody {
    @NotNull(message = "Email is required")
    @NotBlank(message = "Please provide a valid email")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Please provide a valid password")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
