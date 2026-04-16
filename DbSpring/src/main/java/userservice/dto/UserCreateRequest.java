package userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "Name must not be blank.")
        @Size(max = 100, message = "Name must contain at most 100 characters.")
        String name,

        @NotBlank(message = "Email must not be blank.")
        @Email(message = "Email has invalid format.")
        @Size(max = 150, message = "Email must contain at most 150 characters.")
        String email,

        @Min(value = 1, message = "Age must be between 1 and 130.")
        @Max(value = 130, message = "Age must be between 1 and 130.")
        int age
) {
}
