package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentRequestDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentResponseDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentStatusUpdateDTO;
import ifpb.api_clinic_management_system.model.dto.appointment.AppointmentUpdateDTO;
import ifpb.api_clinic_management_system.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentRequestDTO dto) {
        AppointmentResponseDTO created = appointmentService.createAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> findAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findAppointmentById(id));
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> findAllAppointments(Pageable pageable) {
        return ResponseEntity.ok(appointmentService.findAllAppointments(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponseDTO> updateAppointmentStatus(@PathVariable Long id,
                                                               @Valid @RequestBody AppointmentStatusUpdateDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, dto));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }
}