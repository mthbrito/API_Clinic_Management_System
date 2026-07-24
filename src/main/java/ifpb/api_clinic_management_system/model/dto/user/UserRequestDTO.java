package ifpb.api_clinic_management_system.model.dto.user;

import jakarta.validation.constraints.*;

import java.util.Set;

public record UserRequestDTO(

        @NotBlank
        @Email
        @Size(max = 50)
        String email,

        @NotBlank
        @Size(min = 8, max = 255)
        String password,

        @NotNull
        @NotEmpty
        Set<Long> roleIds
) {}
