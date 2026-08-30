package ifpb.api_clinic_management_system.controller;

import ifpb.api_clinic_management_system.model.dto.auth.LoginRequestDTO;
import ifpb.api_clinic_management_system.model.dto.auth.LoginResponseDTO;
import ifpb.api_clinic_management_system.model.entity.Role;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.model.enumeration.RoleType;
import ifpb.api_clinic_management_system.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authentication = authenticationManager.authenticate(authToken);
        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(user);
        Set<RoleType> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000,
                user.getEmail(),
                roles
        ));
    }
}