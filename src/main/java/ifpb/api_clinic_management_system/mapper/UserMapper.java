package ifpb.api_clinic_management_system.mapper;

import ifpb.api_clinic_management_system.model.dto.user.UserRequestDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserResponseDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserSummaryDTO;
import ifpb.api_clinic_management_system.model.entity.Role;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.model.enumeration.RoleType;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toUser(UserRequestDTO dto, Set<Role> roles, String encodedPassword) {
        return User.builder()
                .email(dto.email())
                .enabled(true)
                .roles(roles)
                .password(encodedPassword)
                .build();
    }

    public UserResponseDTO toUserResponseDTO(User user) {
        Set<RoleType> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.isEnabled(),
                roles
        );
    }

    public UserSummaryDTO toUserSummaryDTO(User user) {
        return new UserSummaryDTO(
                user.getId(),
                user.getEmail(),
                user.isEnabled()
        );
    }
}
