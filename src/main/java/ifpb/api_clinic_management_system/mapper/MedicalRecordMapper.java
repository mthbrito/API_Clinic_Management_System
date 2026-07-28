package ifpb.api_clinic_management_system.mapper;

import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordRequestDTO;
import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordResponseDTO;
import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordSummaryDTO;
import ifpb.api_clinic_management_system.model.entity.Appointment;
import ifpb.api_clinic_management_system.model.entity.MedicalRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalRecordMapper {

    private final AppointmentMapper appointmentMapper;

    public MedicalRecord toMedicalRecord(MedicalRecordRequestDTO dto, Appointment appointment) {
        return MedicalRecord.builder()
                .appointment(appointment)
                .diagnosis(dto.diagnosis())
                .prescription(dto.prescription())
                .notes(dto.notes())
                .build();
    }

    public MedicalRecordResponseDTO toMedicalRecordResponseDTO(MedicalRecord medicalRecord) {
        return new MedicalRecordResponseDTO(
                medicalRecord.getId(),
                appointmentMapper.toAppointmentSummaryDTO(medicalRecord.getAppointment()),
                medicalRecord.getDiagnosis(),
                medicalRecord.getPrescription(),
                medicalRecord.getNotes(),
                medicalRecord.getCreatedAt()
        );
    }

    public MedicalRecordSummaryDTO toMedicalRecordSummaryDTO(MedicalRecord medicalRecord) {
        return new MedicalRecordSummaryDTO(
                medicalRecord.getId(),
                medicalRecord.getAppointment().getId(),
                medicalRecord.getDiagnosis(),
                medicalRecord.getCreatedAt()
        );
    }
}