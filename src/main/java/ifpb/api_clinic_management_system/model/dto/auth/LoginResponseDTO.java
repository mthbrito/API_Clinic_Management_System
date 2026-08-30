package ifpb.api_clinic_management_system.model.dto.auth;

import ifpb.api_clinic_management_system.model.enumeration.RoleType;

import java.util.Set;

public record LoginResponseDTO(
        String token,
        String tokenType,
        long expiresIn,
        String email,
        Set<RoleType> roles
) {}