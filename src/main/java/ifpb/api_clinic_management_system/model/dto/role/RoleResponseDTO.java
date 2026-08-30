package ifpb.api_clinic_management_system.model.dto.role;

import ifpb.api_clinic_management_system.model.enumeration.RoleType;

public record RoleResponseDTO(
        Long id,
        RoleType name
) {}