package ifpb.api_clinic_management_system.mapper;

import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentRequestDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentResponseDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentSummaryDTO;
import ifpb.api_clinic_management_system.model.entity.Appointment;
import ifpb.api_clinic_management_system.model.entity.Doctor;
import ifpb.api_clinic_management_system.model.entity.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;

    public Appointment toAppointment(AppointmentRequestDTO dto, Patient patient, Doctor doctor) {
        return Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .dateTime(dto.dateTime())
                .notes(dto.notes())
                .build();
    }

    public AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                patientMapper.toPatientSummaryDTO(appointment.getPatient()),
                doctorMapper.toDoctorSummaryDTO(appointment.getDoctor()),
                appointment.getDateTime(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt()
        );
    }

    public AppointmentSummaryDTO toAppointmentSummaryDTO(Appointment appointment) {
        return new AppointmentSummaryDTO(
                appointment.getId(),
                appointment.getDateTime(),
                appointment.getStatus(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getName()
        );
    }
}
