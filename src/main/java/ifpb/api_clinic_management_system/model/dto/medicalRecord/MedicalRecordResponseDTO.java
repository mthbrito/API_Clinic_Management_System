package ifpb.api_clinic_management_system.model.dto.medicalRecord;

import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentSummaryDTO;

import java.time.LocalDateTime;

public record MedicalRecordResponseDTO(
        Long id,
        AppointmentSummaryDTO appointment,
        String diagnosis,
        String prescription,
        String notes,
        LocalDateTime createdAt
) {}
