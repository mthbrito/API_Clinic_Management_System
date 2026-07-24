package ifpb.api_clinic_management_system.model.dto.doctor;

import ifpb.api_clinic_management_system.model.dto.user.UserSummaryDTO;

public record DoctorResponseDTO(
        Long id,
        String name,
        String crm,
        String specialty,
        String phone,
        UserSummaryDTO user
) {}