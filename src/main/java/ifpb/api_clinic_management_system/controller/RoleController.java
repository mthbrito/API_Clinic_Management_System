package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.role.RoleResponseDTO;
import ifpb.api_clinic_management_system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> findAll() {
        List<RoleResponseDTO> roles = roleService.findAllRoles().stream()
                .map(role -> new RoleResponseDTO(role.getId(), role.getName()))
                .toList();
        return ResponseEntity.ok(roles);
    }
}