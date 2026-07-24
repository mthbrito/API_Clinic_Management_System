package ifpb.api_clinic_management_system.model.dto.patient;

import ifpb.api_clinic_management_system.model.enumeration.Gender;

import java.time.LocalDate;

public record PatientSummaryDTO(
        Long id,
        String name,
        String cpf,
        LocalDate birthDate,
        Gender gender
) {}
