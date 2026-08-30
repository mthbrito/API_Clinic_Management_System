package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.AddressMapper;
import ifpb.api_clinic_management_system.mapper.PatientMapper;
import ifpb.api_clinic_management_system.model.dto.patient.PatientRequestDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientResponseDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientUpdateDTO;
import ifpb.api_clinic_management_system.model.entity.Patient;
import ifpb.api_clinic_management_system.repository.PatientRepository;
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
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AddressMapper addressMapper;

    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO dto) {
        ensureCpfIsAvailable(dto.cpf());
        Patient patient = patientMapper.toPatient(dto);
        log.info("Creating patient with CPF: {}", dto.cpf());
        return patientMapper.toPatientResponseDTO(patientRepository.save(patient));
    }

    public PatientResponseDTO findPatientById(Long id) {
        log.debug("Fetching patient by id: {}", id);
        return patientMapper.toPatientResponseDTO(fetchPatientById(id));
    }

    public Page<PatientResponseDTO> findAllPatients(Pageable pageable) {
        log.debug("Fetching all patients - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return patientRepository.findAll(pageable)
                .map(patientMapper::toPatientResponseDTO);
    }

    @Transactional
    public PatientResponseDTO updatePatient(Long id, PatientUpdateDTO dto) {
        ensureAtLeastOneFieldProvided(dto);
        Patient patient = fetchPatientById(id);
        applyUpdates(patient, dto);
        log.info("Updating patient id: {}", id);
        return patientMapper.toPatientResponseDTO(patient);
    }

    protected Patient fetchPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient not found for id: {}", id);
                    return new EntityNotFoundException("Patient not found");
                });
    }

    private void applyUpdates(Patient patient, PatientUpdateDTO dto) {
        if (dto.name() != null) {
            patient.setName(dto.name());
        }
        if (dto.birthDate() != null) {
            patient.setBirthDate(dto.birthDate());
        }
        if (dto.phone() != null) {
            patient.setPhone(dto.phone());
        }
        if (dto.email() != null) {
            patient.setEmail(dto.email());
        }
        if (dto.gender() != null) {
            patient.setGender(dto.gender());
        }
        if (dto.address() != null) {
            patient.setAddress(addressMapper.toAddress(dto.address()));
        }
    }

    private void ensureCpfIsAvailable(String cpf) {
        if (patientRepository.existsByCpf(cpf)) {
            throw new BusinessException("CPF unavailable");
        }
    }

    private void ensureAtLeastOneFieldProvided(PatientUpdateDTO dto) {
        if (dto.name() == null
                && dto.phone() == null
                && dto.email() == null
                && dto.gender() == null
                && dto.address() == null) {
            throw new BusinessException("At least one field must be provided for update");
        }
    }
}