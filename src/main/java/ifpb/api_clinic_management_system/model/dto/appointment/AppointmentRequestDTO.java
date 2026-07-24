package ifpb.api_clinic_management_system.model.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(

        @NotNull
        Long patientId,

        @NotNull
        Long doctorId,

        @NotNull
        @Future
        LocalDateTime dateTime,

        @Size(max = 500)
        String notes
) {}
