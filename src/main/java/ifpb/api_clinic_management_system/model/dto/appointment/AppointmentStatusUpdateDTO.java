package ifpb.api_clinic_management_system.model.dto.appointment;

import ifpb.api_clinic_management_system.model.enumeration.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateDTO(

        @NotNull
        AppointmentStatus status
) {}
