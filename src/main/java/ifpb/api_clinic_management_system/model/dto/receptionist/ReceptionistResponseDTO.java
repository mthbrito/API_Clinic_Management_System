package ifpb.api_clinic_management_system.model.dto.receptionist;

import ifpb.api_clinic_management_system.model.dto.user.UserSummaryDTO;

public record ReceptionistResponseDTO(
        Long id,
        String name,
        String phone,
        UserSummaryDTO user
) {}
