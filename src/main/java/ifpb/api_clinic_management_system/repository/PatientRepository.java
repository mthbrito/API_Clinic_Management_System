package ifpb.api_clinic_management_system.repository;

import ifpb.api_clinic_management_system.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
