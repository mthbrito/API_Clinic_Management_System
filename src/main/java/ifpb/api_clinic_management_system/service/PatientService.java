package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.mapper.PatientMapper;
import ifpb.api_clinic_management_system.model.dto.patient.PatientRequestDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientResponseDTO;
import ifpb.api_clinic_management_system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO dto) {
        ensureCpfIsAvailable(dto.cpf());

    }

    private void ensureCpfIsAvailable(String cpf) {
        if (patientRepository.existsByCpf(cpf)) {
            throw new BusinessException("CPF unavailable");
        }
    }
}
