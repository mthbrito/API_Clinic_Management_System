package ifpb.api_clinic_management_system.model.dto.doctor;

import jakarta.validation.constraints.Size;

public record DoctorUpdateDTO(

        @Size(max = 100)
        String name,

        @Size(max = 50)
        String specialty,

        @Size(max = 20)
        String phone
) {}