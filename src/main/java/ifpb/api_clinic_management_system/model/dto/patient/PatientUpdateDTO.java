package ifpb.api_clinic_management_system.model.dto.patient;

import ifpb.api_clinic_management_system.model.dto.AddressDTO;
import ifpb.api_clinic_management_system.model.enumeration.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientUpdateDTO(

        @Size(max = 100)
        String name,

        @NotNull
        LocalDate birthDate,

        @Size(max = 20)
        String phone,

        @Email
        @Size(max = 100)
        String email,

        Gender gender,

        AddressDTO address
) {}
