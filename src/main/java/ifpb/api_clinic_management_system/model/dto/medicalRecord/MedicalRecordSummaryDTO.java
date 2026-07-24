package ifpb.api_clinic_management_system.model.dto.medicalRecord;

import java.time.LocalDateTime;

public record MedicalRecordSummaryDTO(
        Long id,
        Long appointmentId,
        String diagnosis,
        LocalDateTime createdAt
) {}
