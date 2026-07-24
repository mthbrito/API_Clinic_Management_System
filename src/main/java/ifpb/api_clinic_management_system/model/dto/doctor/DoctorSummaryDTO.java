package ifpb.api_clinic_management_system.model.dto.doctor;

public record DoctorSummaryDTO(
        Long id,
        String name,
        String crm,
        String specialty
) {}
