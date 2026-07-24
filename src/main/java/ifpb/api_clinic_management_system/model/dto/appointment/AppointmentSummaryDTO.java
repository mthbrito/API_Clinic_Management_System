package ifpb.api_clinic_management_system.model.dto.appointment;

import ifpb.api_clinic_management_system.model.enumeration.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentSummaryDTO(
        Long id,
        LocalDateTime dateTime,
        AppointmentStatus status,
        String doctorName,
        String patientName
) {}
