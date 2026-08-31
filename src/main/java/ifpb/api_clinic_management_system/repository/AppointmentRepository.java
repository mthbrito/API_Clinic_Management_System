package ifpb.api_clinic_management_system.repository;

import ifpb.api_clinic_management_system.model.entity.Appointment;
import ifpb.api_clinic_management_system.model.enumeration.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    boolean existsByDoctorIdAndDateTime(Long doctorId, LocalDateTime dateTime);
    boolean existsByDoctorIdAndDateTimeAndIdNot(Long doctorId, LocalDateTime dateTime, Long id);
}
