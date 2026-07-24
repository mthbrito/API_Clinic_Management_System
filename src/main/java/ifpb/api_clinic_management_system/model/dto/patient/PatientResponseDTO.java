package ifpb.api_clinic_management_system.model.dto.patient;

import ifpb.api_clinic_management_system.model.dto.address.AddressDTO;
import ifpb.api_clinic_management_system.model.enumeration.Gender;

import java.time.LocalDate;

public record PatientResponseDTO(
        Long id,
        String name,
        String cpf,
        LocalDate birthDate,
        String phone,
        String email,
        Gender gender,
        AddressDTO address
) {}
