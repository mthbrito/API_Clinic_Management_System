package ifpb.api_clinic_management_system.mapper;

import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistRequestDTO;
import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistResponseDTO;
import ifpb.api_clinic_management_system.model.entity.Receptionist;
import ifpb.api_clinic_management_system.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReceptionistMapper {

    private final UserMapper userMapper;

    public ReceptionistResponseDTO toReceptionistResponseDTO(Receptionist receptionist) {
        return new ReceptionistResponseDTO(
                receptionist.getId(),
                receptionist.getName(),
                receptionist.getPhone(),
                userMapper.toUserSummaryDTO(receptionist.getUser())
        );
    }

    public Receptionist toReceptionist(ReceptionistRequestDTO dto, User user) {
        return Receptionist.builder().
                name(dto.name()).
                phone(dto.phone()).
                user(user).
                build();
    }
}
