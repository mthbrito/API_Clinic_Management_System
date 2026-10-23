package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.UserMapper;
import ifpb.api_clinic_management_system.model.dto.user.UserRequestDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserResponseDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserUpdateDTO;
import ifpb.api_clinic_management_system.model.entity.Role;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.model.enumeration.RoleType;
import ifpb.api_clinic_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private Role doctorRole;
    private User user;

    private static Set<Role> anySet() {
        return any();
    }

    @BeforeEach
    void setUp() {
        userRequestDTO = new UserRequestDTO("new.doctor@clinic.com", "SenhaForte123", Set.of(2L));
        doctorRole = new Role(2L, RoleType.ROLE_DOCTOR);
        user = User.builder()
                .id(1L)
                .email(userRequestDTO.email())
                .password("encoded-password")
                .enabled(true)
                .roles(Set.of(doctorRole))
                .build();
    }

    @Test
    void createUser_shouldEncodePasswordAndSave_whenEmailIsAvailableAndRolesExist() {
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, userRequestDTO.email(), true, Set.of(RoleType.ROLE_DOCTOR));

        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(false);
        when(roleService.findAllRolesById(userRequestDTO.roleIds())).thenReturn(Set.of(doctorRole));
        when(passwordEncoder.encode(userRequestDTO.password())).thenReturn("encoded-password");
        when(userMapper.toUser(eq(userRequestDTO), anySet(), eq("encoded-password"))).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDTO(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertThat(result).isEqualTo(expectedResponse);
        verify(passwordEncoder).encode(userRequestDTO.password());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowBusinessException_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(userRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email unavailable");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, roleService, userMapper);
    }

    @Test
    void createUser_shouldThrowEntityNotFoundException_whenSomeRoleIdsDoNotExist() {
        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(false);
        when(roleService.findAllRolesById(userRequestDTO.roleIds())).thenReturn(Set.of());

        assertThatThrownBy(() -> userService.createUser(userRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("One or more roles not found");

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void findUserById_shouldReturnMappedUser_whenUserExists() {
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, user.getEmail(), true, Set.of(RoleType.ROLE_DOCTOR));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.findUserById(1L);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void findUserById_shouldThrowEntityNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void updateUser_shouldThrowBusinessException_whenNoFieldsProvided() {
        UserUpdateDTO emptyUpdate = new UserUpdateDTO(null, null, null, null);

        assertThatThrownBy(() -> userService.updateUser(1L, emptyUpdate))
                .isInstanceOf(BusinessException.class)
                .hasMessage("At least one field must be provided for update");

        verifyNoInteractions(userRepository);
    }

    @Test
    void updateUser_shouldValidateNewEmailAvailability_whenEmailIsChanged() {
        UserUpdateDTO update = new UserUpdateDTO("taken@clinic.com", null, null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("taken@clinic.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, update))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email unavailable");

        assertThat(user.getEmail()).isNotEqualTo("taken@clinic.com"); // never applied
    }

    @Test
    void updateUser_shouldApplyOnlyProvidedFields() {
        UserUpdateDTO update = new UserUpdateDTO(null, "NovaSenha123", null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NovaSenha123")).thenReturn("new-encoded-password");
        when(userMapper.toUserResponseDTO(user)).thenReturn(
                new UserResponseDTO(1L, user.getEmail(), true, Set.of(RoleType.ROLE_DOCTOR)));

        userService.updateUser(1L, update);

        assertThat(user.getPassword()).isEqualTo("new-encoded-password");
        assertThat(user.getEmail()).isEqualTo(userRequestDTO.email()); // untouched
    }

    @Test
    void disableUser_shouldSetEnabledFalse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(
                new UserResponseDTO(1L, user.getEmail(), false, Set.of(RoleType.ROLE_DOCTOR)));

        UserResponseDTO result = userService.disableUser(1L);

        assertThat(user.isEnabled()).isFalse();
        assertThat(result.enabled()).isFalse();
    }
}
