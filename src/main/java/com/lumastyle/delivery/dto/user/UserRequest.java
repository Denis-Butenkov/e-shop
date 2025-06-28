package com.lumastyle.delivery.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "Name and Surname is required.")
    private String fullName;

    @NotBlank(message = "Email cannot be empty.")
    @Email(message = "Email must have a valid format.")
    private String email;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 8, message = "Password must contain at least 8 characters.")
    private String password;

}
