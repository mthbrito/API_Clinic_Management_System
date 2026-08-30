package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.doctor.DoctorRequestDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorResponseDTO;
import ifpb.api_clinic_management_system.model.dto.doctor.DoctorUpdateDTO;
import ifpb.api_clinic_management_system.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> createDoctor(@Valid @RequestBody DoctorRequestDTO dto) {
        DoctorResponseDTO created = doctorService.createDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> findDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.findDoctorById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponseDTO>> findAllDoctors(Pageable pageable) {
        return ResponseEntity.ok(doctorService.findAllDoctors(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorUpdateDTO dto) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> disableDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.disableDoctor(id));
    }
}