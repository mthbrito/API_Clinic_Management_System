package ifpb.api_clinic_management_system.model.dto.doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DoctorRequestDTO(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 20)
        String crm,

        @NotBlank
        @Size(max = 50)
        String specialty,

        @Size(max = 20)
        String phone,

        @NotNull
        Long userId
) {}
