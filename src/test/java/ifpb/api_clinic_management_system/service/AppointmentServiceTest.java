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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .build();

        doctor = Doctor.builder()
                .id(2L)
                .name("Dr. Test")
                .crm("CRM-0001")
                .specialty("Cardiologia")
                .build();

        dateTime = LocalDateTime.of(2027, 1, 10, 10, 0);
    }

    @Test
    void createAppointment_shouldSave_whenDoctorIsAvailable() {

        AppointmentRequestDTO request = new AppointmentRequestDTO(patient.getId(), doctor.getId(), dateTime, "First consultation");

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .notes(request.notes())
                .build();

        Appointment savedAppointment = Appointment.builder()
                .id(10L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.notes())
                .build();

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(10L, null, null, dateTime, AppointmentStatus.SCHEDULED, "First consultation", null);

        when(patientService.fetchPatientById(patient.getId())).thenReturn(patient);
        when(doctorService.fetchDoctorById(doctor.getId())).thenReturn(doctor);
        when(appointmentRepository.existsByDoctorIdAndDateTime(doctor.getId(), dateTime)).thenReturn(false);
        when(appointmentMapper.toAppointment(request, patient, doctor)).thenReturn(appointment);
        when(appointmentRepository.save(appointment)).thenReturn(savedAppointment);
        when(appointmentMapper.toAppointmentResponseDTO(savedAppointment)).thenReturn(expectedResponse);

        AppointmentResponseDTO result = appointmentService.createAppointment(request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);

        verify(patientService).fetchPatientById(patient.getId());
        verify(doctorService).fetchDoctorById(doctor.getId());
        verify(appointmentRepository).existsByDoctorIdAndDateTime(doctor.getId(), dateTime);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void createAppointment_shouldThrowBusinessException_whenDoctorIsAlreadyBooked() {

        AppointmentRequestDTO request = new AppointmentRequestDTO(patient.getId(), doctor.getId(), dateTime, "First consultation");

        when(patientService.fetchPatientById(patient.getId())).thenReturn(patient);
        when(doctorService.fetchDoctorById(doctor.getId())).thenReturn(doctor);
        when(appointmentRepository.existsByDoctorIdAndDateTime(doctor.getId(), dateTime)).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.createAppointment(request)).isInstanceOf(BusinessException.class)
                .hasMessage("Doctor already has an appointment scheduled at this time");

        verify(appointmentRepository, never()).save(any());
        verify(appointmentMapper, never()).toAppointment(any(), any(), any());
    }

    @Test
    void findAppointmentById_shouldReturnAppointment_whenAppointmentExists() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.SCHEDULED)
                .notes("Notes")
                .build();

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(1L, null, null, dateTime, AppointmentStatus.SCHEDULED, "Notes", null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toAppointmentResponseDTO(appointment)).thenReturn(expectedResponse);

        AppointmentResponseDTO result = appointmentService.findAppointmentById(1L);

        assertThat(result).isEqualTo(expectedResponse);

        verify(appointmentRepository).findById(1L);
        verify(appointmentMapper).toAppointmentResponseDTO(appointment);
    }

    @Test
    void findAppointmentById_shouldThrowEntityNotFoundException_whenAppointmentDoesNotExist() {

        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findAppointmentById(99L)).isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Appointment not found");

        verify(appointmentMapper, never()).toAppointmentResponseDTO(any());
    }

    @Test
    void updateAppointment_shouldThrowBusinessException_whenNoFieldsProvided() {

        AppointmentUpdateDTO update = new AppointmentUpdateDTO(null, null);

        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, update)).isInstanceOf(BusinessException.class)
                .hasMessage("At least one field must be provided for update");

        verifyNoInteractions(appointmentRepository);
        verifyNoInteractions(appointmentMapper);
    }

    @Test
    void updateAppointment_shouldUpdateDateTime_whenDateTimeIsProvided() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.SCHEDULED)
                .notes("Old notes")
                .build();

        LocalDateTime newDateTime = LocalDateTime.of(2027, 1, 10, 11, 0);

        AppointmentUpdateDTO update = new AppointmentUpdateDTO(newDateTime, null);

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(1L, null, null, newDateTime, AppointmentStatus.SCHEDULED, "Old notes", null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctorIdAndDateTimeAndIdNot(doctor.getId(), newDateTime, appointment.getId())).thenReturn(false);
        when(appointmentMapper.toAppointmentResponseDTO(appointment)).thenReturn(expectedResponse);

        AppointmentResponseDTO result = appointmentService.updateAppointment(1L, update);

        assertThat(appointment.getDateTime()).isEqualTo(newDateTime);
        assertThat(appointment.getNotes()).isEqualTo("Old notes");
        assertThat(result).isEqualTo(expectedResponse);

        verify(appointmentRepository).existsByDoctorIdAndDateTimeAndIdNot(doctor.getId(), newDateTime, appointment.getId());
    }

    @Test
    void updateAppointment_shouldUpdateNotes_whenNotesAreProvided() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.SCHEDULED)
                .notes("Old notes")
                .build();

        AppointmentUpdateDTO update = new AppointmentUpdateDTO(null, "New notes");

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(1L, null, null, dateTime, AppointmentStatus.SCHEDULED, "New notes", null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toAppointmentResponseDTO(appointment)).thenReturn(expectedResponse);

        AppointmentResponseDTO result = appointmentService.updateAppointment(1L, update);

        assertThat(appointment.getNotes()).isEqualTo("New notes");
        assertThat(appointment.getDateTime()).isEqualTo(dateTime);
        assertThat(result).isEqualTo(expectedResponse);

        verify(appointmentRepository, never()).existsByDoctorIdAndDateTimeAndIdNot(any(), any(), any());
    }

    @Test
    void updateAppointment_shouldThrowBusinessException_whenAppointmentIsCancelled() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.CANCELLED)
                .build();

        AppointmentUpdateDTO update = new AppointmentUpdateDTO(null, "Trying to update");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, update)).isInstanceOf(BusinessException.class)
                .hasMessage("Appointment can no longer be modified");

        verify(appointmentMapper, never()).toAppointmentResponseDTO(any());
    }

    @Test
    void updateAppointment_shouldThrowBusinessException_whenAppointmentIsCompleted() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.COMPLETED)
                .build();

        AppointmentUpdateDTO update = new AppointmentUpdateDTO(null, "Trying to update");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, update)).isInstanceOf(BusinessException.class)
                .hasMessage("Appointment can no longer be modified");
    }

    @Test
    void updateAppointment_shouldThrowBusinessException_whenNewDateTimeIsAlreadyBooked() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        LocalDateTime newDateTime = LocalDateTime.of(2027, 1, 10, 11, 0);

        AppointmentUpdateDTO update = new AppointmentUpdateDTO(newDateTime, null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.existsByDoctorIdAndDateTimeAndIdNot(doctor.getId(), newDateTime, appointment.getId())).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.updateAppointment(1L, update)).isInstanceOf(BusinessException.class)
                .hasMessage("Doctor already has an appointment scheduled at this time");
        assertThat(appointment.getDateTime()).isEqualTo(dateTime);

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void updateAppointmentStatus_shouldChangeStatus_whenTransitionIsValid() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        AppointmentStatusUpdateDTO update = new AppointmentStatusUpdateDTO(AppointmentStatus.COMPLETED);

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(1L, null, null, dateTime, AppointmentStatus.COMPLETED, null, null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toAppointmentResponseDTO(appointment)).thenReturn(expectedResponse);

        AppointmentResponseDTO result = appointmentService.updateAppointmentStatus(1L, update);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void updateAppointmentStatus_shouldThrowBusinessException_whenAppointmentIsCancelled() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .status(AppointmentStatus.CANCELLED)
                .build();

        AppointmentStatusUpdateDTO update = new AppointmentStatusUpdateDTO(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateAppointmentStatus(1L, update)).isInstanceOf(BusinessException.class)
                .hasMessage("Appointment status can no longer be changed");
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    void updateAppointmentStatus_shouldThrowBusinessException_whenAppointmentIsCompleted() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .status(AppointmentStatus.COMPLETED)
                .build();

        AppointmentStatusUpdateDTO update = new AppointmentStatusUpdateDTO(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateAppointmentStatus(1L, update)).isInstanceOf(BusinessException.class)
                .hasMessage("Appointment status can no longer be changed");
    }

    @Test
    void updateAppointmentStatus_shouldThrowBusinessException_whenTargetStatusIsTheSame() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        AppointmentStatusUpdateDTO update = new AppointmentStatusUpdateDTO(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateAppointmentStatus(1L, update)).isInstanceOf(BusinessException.class)
                .hasMessage("Appointment is already in status: SCHEDULED");
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void cancelAppointment_shouldCancel_whenAppointmentIsModifiable() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .dateTime(dateTime)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        AppointmentResponseDTO expectedResponse = new AppointmentResponseDTO(1L, null, null, dateTime, AppointmentStatus.CANCELLED, null, null);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toAppointmentResponseDTO(appointment)).thenReturn(expectedResponse);

        AppointmentResponseDTO result = appointmentService.cancelAppointment(1L);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void cancelAppointment_shouldThrowBusinessException_whenAppointmentIsAlreadyCancelled() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .status(AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(1L)).isInstanceOf(BusinessException.class)
                .hasMessage("Appointment can no longer be modified");
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    void cancelAppointment_shouldThrowBusinessException_whenAppointmentIsCompleted() {

        Appointment appointment = Appointment.builder()
                .id(1L)
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(1L)).isInstanceOf(BusinessException.class)
                .hasMessage("Appointment can no longer be modified");
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void findAllAppointments_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Appointment appointment = Appointment.builder()
                .id(1L)
                .status(AppointmentStatus.SCHEDULED)
                .build();
        Page<Appointment> page = new PageImpl<>(List.of(appointment));
        AppointmentResponseDTO responseDTO = new AppointmentResponseDTO(1L, null, null, dateTime, AppointmentStatus.SCHEDULED, null, null);

        when(appointmentRepository.findAll(pageable)).thenReturn(page);
        when(appointmentMapper.toAppointmentResponseDTO(appointment)).thenReturn(responseDTO);

        Page<AppointmentResponseDTO> result = appointmentService.findAllAppointments(pageable);

        assertThat(result.getContent()).containsExactly(responseDTO);
    }

    @Test
    void createAppointment_shouldPropagateException_andNeverCheckAvailability_whenPatientNotFound() {
        AppointmentRequestDTO request = new AppointmentRequestDTO(99L, doctor.getId(), dateTime, "Notes");

        when(patientService.fetchPatientById(99L))
                .thenThrow(new EntityNotFoundException("Patient not found"));

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(doctorService, appointmentRepository, appointmentMapper);
    }
}