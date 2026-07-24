package ifpb.api_clinic_management_system.model.dto.medicalRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequestDTO(

        @NotNull
        Long appointmentId,

        @NotBlank
        @Size(max = 1000)
        String diagnosis,

        @Size(max = 1000)
        String prescription,

        @Size(max = 1000)
        String notes
) {}
