package ifpb.api_clinic_management_system.model.dto.user;

import ifpb.api_clinic_management_system.model.enumeration.RoleType;

import java.util.Set;

public record UserResponseDTO(
        Long id,
        String email,
        boolean enabled,
        Set<RoleType> roles
) {}
