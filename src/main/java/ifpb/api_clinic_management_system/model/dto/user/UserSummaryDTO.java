package ifpb.api_clinic_management_system.model.dto.user;

public record UserSummaryDTO(
        Long id,
        String email,
        boolean enabled
) {}
