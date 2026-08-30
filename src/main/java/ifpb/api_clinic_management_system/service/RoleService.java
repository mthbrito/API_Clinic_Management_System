package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.model.entity.Role;
import ifpb.api_clinic_management_system.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Set<Role> findAllRolesById(Set<Long> roleIds) {
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }
}