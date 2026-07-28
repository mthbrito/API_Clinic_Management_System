package ifpb.api_clinic_management_system.mapper;

import ifpb.api_clinic_management_system.model.dto.patient.PatientRequestDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientResponseDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientSummaryDTO;
import ifpb.api_clinic_management_system.model.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientMapper {

    private final AddressMapper addressMapper;

    public Patient toPatient(PatientRequestDTO dto) {
        return Patient.builder()
                .name(dto.name())
                .cpf(dto.cpf())
                .birthDate(dto.birthDate())
                .phone(dto.phone())
                .email(dto.email())
                .gender(dto.gender())
                .address(addressMapper.toAddress(dto.address()))
                .build();
    }

    public PatientResponseDTO toPatientResponseDTO(Patient patient) {
        return new PatientResponseDTO(
                patient.getId(),
                patient.getName(),
                patient.getCpf(),
                patient.getBirthDate(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getGender(),
                addressMapper.toAddressDTO(patient.getAddress())
        );
    }

    public PatientSummaryDTO toPatientSummaryDTO(Patient patient) {
        return new PatientSummaryDTO(
                patient.getId(),
                patient.getName(),
                patient.getCpf(),
                patient.getBirthDate(),
                patient.getGender()
        );
    }
}