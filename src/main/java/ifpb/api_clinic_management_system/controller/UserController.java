package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.user.UserRequestDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserResponseDTO;
import ifpb.api_clinic_management_system.model.dto.user.UserUpdateDTO;
import ifpb.api_clinic_management_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<UserResponseDTO> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.disableUser(id));
    }
}