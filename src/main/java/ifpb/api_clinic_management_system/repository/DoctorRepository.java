package ifpb.api_clinic_management_system.repository;

import ifpb.api_clinic_management_system.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByCrm(String crm);
    boolean existsByUserId(Long userId);
    List<Doctor> findBySpecialty(String specialty);
}
