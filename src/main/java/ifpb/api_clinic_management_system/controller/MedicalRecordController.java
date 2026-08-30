package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordRequestDTO;
import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordResponseDTO;
import ifpb.api_clinic_management_system.model.dto.medicalRecord.MedicalRecordUpdateDTO;
import ifpb.api_clinic_management_system.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(@Valid @RequestBody MedicalRecordRequestDTO dto) {
        MedicalRecordResponseDTO created = medicalRecordService.createMedicalRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> findMedicalRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.findMedicalRecordById(id));
    }

    @GetMapping
    public ResponseEntity<Page<MedicalRecordResponseDTO>> findAllMedicalRecords(Pageable pageable) {
        return ResponseEntity.ok(medicalRecordService.findAllMedicalRecords(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> updateMedicalRecords(@PathVariable Long id,
                                                           @Valid @RequestBody MedicalRecordUpdateDTO dto) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}