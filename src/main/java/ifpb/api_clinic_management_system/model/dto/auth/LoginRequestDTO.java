package ifpb.api_clinic_management_system.model.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank
        String email,

        @NotBlank
        String password
) {}
