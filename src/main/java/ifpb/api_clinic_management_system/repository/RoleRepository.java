package ifpb.api_clinic_management_system.repository;

import ifpb.api_clinic_management_system.model.entity.Role;
import ifpb.api_clinic_management_system.model.enumeration.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
