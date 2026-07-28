package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.MedicalRecordMapper;
import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordRequestDTO;
import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordResponseDTO;
import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordUpdateDTO;
import ifpb.api_clinic_management_system.model.entity.Appointment;
import ifpb.api_clinic_management_system.model.entity.MedicalRecord;
import ifpb.api_clinic_management_system.model.enumeration.AppointmentStatus;
import ifpb.api_clinic_management_system.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;
    private final AppointmentService appointmentService;

    @Transactional
    public MedicalRecordResponseDTO createMedicalRecord(MedicalRecordRequestDTO dto) {
        ensureAppointmentIsAvailable(dto.appointmentId());
        Appointment appointment = appointmentService.fetchAppointmentById(dto.appointmentId());
        ensureAppointmentIsCompleted(appointment);
        MedicalRecord medicalRecord = medicalRecordMapper.toMedicalRecord(dto, appointment);
        log.info("Creating medical record for appointment id: {}", dto.appointmentId());
        return medicalRecordMapper.toMedicalRecordResponseDTO(medicalRecordRepository.save(medicalRecord));
    }

    public MedicalRecordResponseDTO findMedicalRecordById(Long id) {
        log.debug("Fetching medical record by id: {}", id);
        return medicalRecordMapper.toMedicalRecordResponseDTO(fetchMedicalRecordById(id));
    }

    public Page<MedicalRecordResponseDTO> findAllMedicalRecords(Pageable pageable) {
        log.debug("Fetching all medical records - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return medicalRecordRepository.findAll(pageable)
                .map(medicalRecordMapper::toMedicalRecordResponseDTO);
    }

    @Transactional
    public MedicalRecordResponseDTO updateMedicalRecord(Long id, MedicalRecordUpdateDTO dto) {
        ensureAtLeastOneFieldProvided(dto);
        MedicalRecord medicalRecord = fetchMedicalRecordById(id);
        applyUpdates(medicalRecord, dto);
        log.info("Updating medical record id: {}", id);
        return medicalRecordMapper.toMedicalRecordResponseDTO(medicalRecord);
    }

    @Transactional
    public void deleteMedicalRecord(Long id) {
        MedicalRecord medicalRecord = fetchMedicalRecordById(id);
        medicalRecordRepository.delete(medicalRecord);
        log.info("Deleting medical record id: {}", id);
    }

    protected MedicalRecord fetchMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Medical record not found for id: {}", id);
                    return new EntityNotFoundException("Medical record not found");
                });
    }

    private void applyUpdates(MedicalRecord medicalRecord, MedicalRecordUpdateDTO dto) {
        if (dto.diagnosis() != null) {
            medicalRecord.setDiagnosis(dto.diagnosis());
        }
        if (dto.prescription() != null) {
            medicalRecord.setPrescription(dto.prescription());
        }
        if (dto.notes() != null) {
            medicalRecord.setNotes(dto.notes());
        }
    }

    private void ensureAppointmentIsAvailable(Long appointmentId) {
        if (medicalRecordRepository.existsByAppointmentId(appointmentId)) {
            throw new BusinessException("Appointment already has a medical record");
        }
    }

    private void ensureAppointmentIsCompleted(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BusinessException("Medical record can only be created for a completed appointment");
        }
    }

    private void ensureAtLeastOneFieldProvided(MedicalRecordUpdateDTO dto) {
        if (dto.diagnosis() == null
                && dto.prescription() == null
                && dto.notes() == null) {
            throw new BusinessException("At least one field must be provided for update");
        }
    }
}