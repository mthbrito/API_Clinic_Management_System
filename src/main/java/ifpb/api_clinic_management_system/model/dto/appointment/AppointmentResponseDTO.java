package ifpb.api_clinic_management_system.model.dto.appointment;

import ifpb.api_clinic_management_system.model.dto.doctor.DoctorSummaryDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientSummaryDTO;
import ifpb.api_clinic_management_system.model.enumeration.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long id,
        PatientSummaryDTO patient,
        DoctorSummaryDTO doctor,
        LocalDateTime dateTime,
        AppointmentStatus status,
        String notes,
        LocalDateTime createdAt
) {}
