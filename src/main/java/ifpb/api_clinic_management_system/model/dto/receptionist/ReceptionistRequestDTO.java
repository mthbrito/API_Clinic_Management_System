package ifpb.api_clinic_management_system.model.dto.receptionist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReceptionistRequestDTO(

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 20)
        String phone,

        @NotNull
        Long userId
) {}
