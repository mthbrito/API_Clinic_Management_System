package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistRequestDTO;
import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistResponseDTO;
import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistUpdateDTO;
import ifpb.api_clinic_management_system.service.ReceptionistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receptionists")
@RequiredArgsConstructor
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReceptionistResponseDTO> createReceptionist(@Valid @RequestBody ReceptionistRequestDTO dto) {
        ReceptionistResponseDTO created = receptionistService.createReceptionist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceptionistResponseDTO> findReceptionistById(@PathVariable Long id) {
        return ResponseEntity.ok(receptionistService.findReceptionistById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ReceptionistResponseDTO>> findAllReceptionists(Pageable pageable) {
        return ResponseEntity.ok(receptionistService.findAllReceptionists(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReceptionistResponseDTO> updateReceptionist(@PathVariable Long id, @Valid @RequestBody ReceptionistUpdateDTO dto) {
        return ResponseEntity.ok(receptionistService.updateReceptionist(id, dto));
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReceptionistResponseDTO> disableReceptionist(@PathVariable Long id) {
        return ResponseEntity.ok(receptionistService.disableReceptionist(id));
    }
}