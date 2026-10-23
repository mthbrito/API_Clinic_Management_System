package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.DoctorMapper;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorRequestDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorResponseDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorUpdateDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserSummaryDTO;
import ifpb.api_clinic_management_system.model.entity.Doctor;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private DoctorService doctorService;

    private DoctorRequestDTO doctorRequestDTO;
    private User user;

    @BeforeEach
    void setUp() {
        doctorRequestDTO = new DoctorRequestDTO("Dr. Test", "CRM-0001", "Cardiologia", "83999990000", 1L);
        user = User.builder()
                .id(1L)
                .email("dr@clinic.com")
                .password("encoded")
                .enabled(true)
                .build();
    }

    @Test
    void createDoctor_shouldSave_whenCrmAndUserAreAvailable() {
        Doctor doctor = Doctor.builder()
                .name(doctorRequestDTO.name())
                .crm(doctorRequestDTO.crm())
                .specialty(doctorRequestDTO.specialty())
                .phone(doctorRequestDTO.phone())
                .user(user)
                .build();
        Doctor savedDoctor = Doctor.builder()
                .id(10L)
                .name(doctorRequestDTO.name())
                .crm(doctorRequestDTO.crm())
                .specialty(doctorRequestDTO.specialty())
                .phone(doctorRequestDTO.phone())
                .user(user)
                .build();
        DoctorResponseDTO expectedResponse = new DoctorResponseDTO(10L, doctorRequestDTO.name(), doctorRequestDTO.crm(),
                doctorRequestDTO.specialty(), doctorRequestDTO.phone(), new UserSummaryDTO(1L, user.getEmail(), true));

        when(doctorRepository.existsByCrm(doctorRequestDTO.crm())).thenReturn(false);
        when(doctorRepository.existsByUserId(doctorRequestDTO.userId())).thenReturn(false);
        when(userService.fetchUserById(doctorRequestDTO.userId())).thenReturn(user);
        when(doctorMapper.toDoctor(doctorRequestDTO, user)).thenReturn(doctor);
        when(doctorRepository.save(doctor)).thenReturn(savedDoctor);
        when(doctorMapper.toDoctorResponseDTO(savedDoctor)).thenReturn(expectedResponse);

        DoctorResponseDTO result = doctorService.createDoctor(doctorRequestDTO);

        assertThat(result).isEqualTo(expectedResponse);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void createDoctor_shouldThrowBusinessException_whenCrmAlreadyExists() {
        when(doctorRepository.existsByCrm(doctorRequestDTO.crm())).thenReturn(true);

        assertThatThrownBy(() -> doctorService.createDoctor(doctorRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("CRM unavailable");

        verify(doctorRepository, never()).existsByUserId(any());
        verify(doctorRepository, never()).save(any());
        verifyNoInteractions(userService);
    }

    @Test
    void createDoctor_shouldThrowBusinessException_whenUserAlreadyLinkedToADoctor() {
        when(doctorRepository.existsByCrm(doctorRequestDTO.crm())).thenReturn(false);
        when(doctorRepository.existsByUserId(doctorRequestDTO.userId())).thenReturn(true);

        assertThatThrownBy(() -> doctorService.createDoctor(doctorRequestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User already linked to a doctor");

        verify(doctorRepository, never()).save(any());
        verifyNoInteractions(userService);
    }

    @Test
    void findDoctorById_shouldThrowEntityNotFoundException_whenDoctorDoesNotExist() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.findDoctorById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Doctor not found");
    }

    @Test
    void updateDoctor_shouldThrowBusinessException_whenNoFieldsProvided() {
        DoctorUpdateDTO emptyUpdate = new DoctorUpdateDTO(null, null, null);

        assertThatThrownBy(() -> doctorService.updateDoctor(1L, emptyUpdate))
                .isInstanceOf(BusinessException.class)
                .hasMessage("At least one field must be provided for update");

        verifyNoInteractions(doctorRepository);
    }

    @Test
    void updateDoctor_shouldApplyOnlyProvidedFields() {
        Doctor existing = Doctor.builder()
                .id(1L)
                .name("Old Name")
                .crm("CRM-OLD")
                .specialty("Old Specialty")
                .phone("0000")
                .user(user)
                .build();
        DoctorUpdateDTO update = new DoctorUpdateDTO("New Name", null, null);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(doctorMapper.toDoctorResponseDTO(existing)).thenReturn(
                new DoctorResponseDTO(1L, "New Name", "CRM-OLD", "Old Specialty", "0000",
                        new UserSummaryDTO(1L, user.getEmail(), true)));

        DoctorResponseDTO result = doctorService.updateDoctor(1L, update);

        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getSpecialty()).isEqualTo("Old Specialty"); // untouched
        assertThat(result.name()).isEqualTo("New Name");
    }

    @Test
    void disableDoctor_shouldDisableUnderlyingUser() {
        Doctor existing = Doctor.builder()
                .id(1L)
                .name("Dr. Test")
                .crm("CRM-0001")
                .specialty("Cardiologia")
                .user(user)
                .build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(doctorMapper.toDoctorResponseDTO(existing)).thenReturn(
                new DoctorResponseDTO(1L, "Dr. Test", "CRM-0001", "Cardiologia", null,
                        new UserSummaryDTO(1L, user.getEmail(), false)));

        DoctorResponseDTO result = doctorService.disableDoctor(1L);

        assertThat(user.isEnabled()).isFalse(); // disableDoctor disables the linked User, not the Doctor row
        assertThat(result.user()
                .enabled()).isFalse();
    }
}