package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.patient.PatientRequestDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientResponseDTO;
import ifpb.api_clinic_management_system.model.dto.patient.PatientUpdateDTO;
import ifpb.api_clinic_management_system.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO dto) {
        PatientResponseDTO created = patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> findPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.findPatientById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponseDTO>> findAllPatients(Pageable pageable) {
        return ResponseEntity.ok(patientService.findAllPatients(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientUpdateDTO dto) {
        return ResponseEntity.ok(patientService.updatePatient(id, dto));
    }
}