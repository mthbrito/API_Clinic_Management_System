package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.AppointmentMapper;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentRequestDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentResponseDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentStatusUpdateDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentUpdateDTO;
import ifpb.api_clinic_management_system.model.entity.Appointment;
import ifpb.api_clinic_management_system.model.entity.Doctor;
import ifpb.api_clinic_management_system.model.entity.Patient;
import ifpb.api_clinic_management_system.model.enumeration.AppointmentStatus;
import ifpb.api_clinic_management_system.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO dto) {
        Patient patient = patientService.fetchPatientById(dto.patientId());
        Doctor doctor = doctorService.fetchDoctorById(dto.doctorId());
        ensureDoctorIsAvailable(doctor.getId(), dto.dateTime(), null);
        Appointment appointment = appointmentMapper.toAppointment(dto, patient, doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        log.info("Creating appointment for patient id: {} with doctor id: {}", dto.patientId(), dto.doctorId());
        return appointmentMapper.toAppointmentResponseDTO(appointmentRepository.save(appointment));
    }

    public AppointmentResponseDTO findAppointmentById(Long id) {
        log.debug("Fetching appointment by id: {}", id);
        return appointmentMapper.toAppointmentResponseDTO(fetchAppointmentById(id));
    }

    public Page<AppointmentResponseDTO> findAllAppointments(Pageable pageable) {
        log.debug("Fetching all appointments - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return appointmentRepository.findAll(pageable)
                .map(appointmentMapper::toAppointmentResponseDTO);
    }

    @Transactional
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentUpdateDTO dto) {
        ensureAtLeastOneFieldProvided(dto);
        Appointment appointment = fetchAppointmentById(id);
        ensureAppointmentIsModifiable(appointment);
        applyUpdates(appointment, dto);
        log.info("Updating appointment id: {}", id);
        return appointmentMapper.toAppointmentResponseDTO(appointment);
    }

    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(Long id, AppointmentStatusUpdateDTO dto) {
        Appointment appointment = fetchAppointmentById(id);
        ensureValidStatusTransition(appointment.getStatus(), dto.status());
        appointment.setStatus(dto.status());
        log.info("Updating appointment id: {} to status: {}", id, dto.status());
        return appointmentMapper.toAppointmentResponseDTO(appointment);
    }

    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appointment = fetchAppointmentById(id);
        ensureAppointmentIsModifiable(appointment);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        log.info("Cancelling appointment id: {}", id);
        return appointmentMapper.toAppointmentResponseDTO(appointment);
    }

    protected Appointment fetchAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Appointment not found for id: {}", id);
                    return new EntityNotFoundException("Appointment not found");
                });
    }

    private void applyUpdates(Appointment appointment, AppointmentUpdateDTO dto) {
        if (dto.dateTime() != null) {
            ensureDoctorIsAvailable(appointment.getDoctor().getId(), dto.dateTime(), appointment.getId());
            appointment.setDateTime(dto.dateTime());
        }
        if (dto.notes() != null) {
            appointment.setNotes(dto.notes());
        }
    }

    private void ensureDoctorIsAvailable(Long doctorId, LocalDateTime dateTime, Long excludeId) {
        boolean exists = excludeId == null
                ? appointmentRepository.existsByDoctorIdAndDateTime(doctorId, dateTime)
                : appointmentRepository.existsByDoctorIdAndDateTimeAndIdNot(doctorId, dateTime, excludeId);
        if (exists) {
            throw new BusinessException("Doctor already has an appointment scheduled at this time");
        }
    }

    private void ensureAppointmentIsModifiable(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Appointment can no longer be modified");
        }
    }

    private void ensureValidStatusTransition(AppointmentStatus current, AppointmentStatus target) {
        if (current == AppointmentStatus.CANCELLED || current == AppointmentStatus.COMPLETED) {
            throw new BusinessException("Appointment status can no longer be changed");
        }
        if (current == target) {
            throw new BusinessException("Appointment is already in status: " + target);
        }
    }

    private void ensureAtLeastOneFieldProvided(AppointmentUpdateDTO dto) {
        if (dto.dateTime() == null
                && dto.notes() == null) {
            throw new BusinessException("At least one field must be provided for update");
        }
    }
}