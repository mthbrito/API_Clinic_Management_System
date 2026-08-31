package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.UserMapper;
import ifpb.api_clinic_management_system.model.dto.user.UserRequestDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserResponseDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserUpdateDTO;
import ifpb.api_clinic_management_system.model.entity.Role;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        ensureEmailIsAvailable(dto.email(), null);
        Set<Role> roles = fetchRoles(dto.roleIds());
        String encodedPassword = encodePassword(dto.password());
        User user = userMapper.toUser(dto, roles, encodedPassword);
        log.info("Creating user with email: {}", dto.email());
        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO findUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        return userMapper.toUserResponseDTO(fetchUserById(id));
    }

    public Page<UserResponseDTO> findAllUsers(Pageable pageable) {
        log.debug("Fetching all users - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponseDTO);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        ensureAtLeastOneFieldProvided(dto);
        User user = fetchUserById(id);
        applyUpdates(user, dto);
        log.info("Updating user id: {}", id);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO disableUser(Long id) {
        User user = fetchUserById(id);
        user.setEnabled(false);
        log.info("Disabling user id: {}", id);
        return userMapper.toUserResponseDTO(user);
    }

    protected User fetchUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for id: {}", id);
                    return new EntityNotFoundException("User not found");
                });
    }

    private void applyUpdates(User user, UserUpdateDTO dto) {
        if (dto.email() != null) {
            ensureEmailIsAvailable(dto.email(), user.getId());
            user.setEmail(dto.email());
        }
        if (dto.newPassword() != null) {
            user.setPassword(encodePassword(dto.newPassword()));
        }
        if (dto.enabled() != null) {
            user.setEnabled(dto.enabled());
        }
        if (dto.roleIds() != null) {
            user.setRoles(fetchRoles(dto.roleIds()));
        }
    }

    private Set<Role> fetchRoles(Set<Long> roleIds) {
        Set<Role> roles = new HashSet<>(roleService.findAllRolesById(roleIds));
        if (roles.size() != roleIds.size()) {
            log.warn("Role lookup mismatch: requested={}, found={}", roleIds.size(), roles.size());
            throw new EntityNotFoundException("One or more roles not found");
        }
        return roles;
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private void ensureEmailIsAvailable(String email, Long excludeId) {
        boolean exists = excludeId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, excludeId);
        if (exists) {
            throw new BusinessException("Email unavailable");
        }
    }

    private void ensureAtLeastOneFieldProvided(UserUpdateDTO dto) {
        if (dto.email() == null
                && dto.newPassword() == null
                && dto.enabled() == null
                && dto.roleIds() == null) {
            throw new BusinessException("At least one field must be provided for update");
        }
    }
}