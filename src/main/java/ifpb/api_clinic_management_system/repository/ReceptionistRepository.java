package ifpb.api_clinic_management_system.repository;

import ifpb.api_clinic_management_system.model.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
    boolean existsByUserId(Long userId);
}
