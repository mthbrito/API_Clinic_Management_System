package ifpb.api_clinic_management_system.repository;

import ifpb.api_clinic_management_system.model.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
    List<MedicalRecord> findByAppointment_Patient_Id(Long patientId);
}
