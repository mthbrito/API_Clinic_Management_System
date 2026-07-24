package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.DoctorMapper;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorRequestDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorResponseDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorUpdateDTO;
import ifpb.api_clinic_management_system.model.entity.Doctor;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.repository.DoctorRepository;
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
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final UserService userService;

    @Transactional
    public DoctorResponseDTO createDoctor(DoctorRequestDTO dto) {
        ensureCrmIsAvailable(dto.crm());
        ensureUserIsAvailable(dto.userId());
        User user = userService.fetchUserById(dto.userId());
        Doctor doctor = doctorMapper.toDoctor(dto, user);
        log.info("Creating doctor with CRM: {}", dto.crm());
        return doctorMapper.toDoctorResponseDTO(doctorRepository.save(doctor));
    }

    public DoctorResponseDTO findDoctorById(Long id) {
        log.debug("Fetching doctor by id: {}", id);
        return doctorMapper.toDoctorResponseDTO(fetchDoctorById(id));
    }

    public Page<DoctorResponseDTO> findAllDoctors(Pageable pageable) {
        log.debug("Fetching all doctors - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return doctorRepository.findAll(pageable)
                .map(doctorMapper::toDoctorResponseDTO);
    }

    @Transactional
    public DoctorResponseDTO updateDoctor(Long id, DoctorUpdateDTO dto) {
        ensureAtLeastOneFieldProvided(dto);
        Doctor doctor = fetchDoctorById(id);
        applyUpdates(doctor, dto);
        log.info("Updating doctor id: {}", id);
        return doctorMapper.toDoctorResponseDTO(doctor);
    }

    @Transactional
    public DoctorResponseDTO disableDoctor(Long id) {
        Doctor doctor = fetchDoctorById(id);
        doctor.getUser().setEnabled(false);
        log.info("Disabling doctor id: {}", id);
        return doctorMapper.toDoctorResponseDTO(doctor);
    }

    protected Doctor fetchDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Doctor not found for id: {}", id);
                    return new EntityNotFoundException("Doctor not found");
                });
    }

    private void applyUpdates(Doctor doctor, DoctorUpdateDTO dto) {
        if (dto.name() != null) {
            doctor.setName(dto.name());
        }
        if (dto.specialty() != null) {
            doctor.setSpecialty(dto.specialty());
        }
        if (dto.phone() != null) {
            doctor.setPhone(dto.phone());
        }
    }

    private void ensureCrmIsAvailable(String crm) {
        if (doctorRepository.existsByCrm(crm)) {
            throw new BusinessException("CRM unavailable");
        }
    }

    private void ensureUserIsAvailable(Long userId) {
        if (doctorRepository.existsByUserId(userId)) {
            throw new BusinessException("User already linked to a doctor");
        }
    }

    private void ensureAtLeastOneFieldProvided(DoctorUpdateDTO dto) {
        if (dto.name() == null
                && dto.specialty() == null
                && dto.phone() == null) {
            throw new BusinessException("At least one field must be provided for update");
        }
    }
}