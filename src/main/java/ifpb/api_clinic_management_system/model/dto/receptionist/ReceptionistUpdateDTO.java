package ifpb.api_clinic_management_system.model.dto.receptionist;

import jakarta.validation.constraints.Size;

public record ReceptionistUpdateDTO(

        @Size(max = 100)
        String name,

        @Size(max = 20)
        String phone
) {}
