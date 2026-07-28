package ifpb.api_clinic_management_system.mapper;


import ifpb.api_clinic_management_system.model.dto.doctor.DoctorRequestDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorResponseDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorSummaryDTO;
import ifpb.api_clinic_management_system.model.entity.Doctor;
import ifpb.api_clinic_management_system.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorMapper {

    private final UserMapper userMapper;

    public Doctor toDoctor(DoctorRequestDTO dto, User user) {
        return Doctor.builder().
                name(dto.name()).
                crm(dto.crm()).
                specialty(dto.specialty()).
                phone(dto.phone()).
                user(user).
                build();
    }

    public DoctorResponseDTO toDoctorResponseDTO(Doctor doctor) {
        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getCrm(),
                doctor.getSpecialty(),
                doctor.getPhone(),
                userMapper.toUserSummaryDTO(doctor.getUser())
        );
    }

    public DoctorSummaryDTO toDoctorSummaryDTO(Doctor doctor) {
        return new DoctorSummaryDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getCrm(),
                doctor.getSpecialty()
        );
    }
}
