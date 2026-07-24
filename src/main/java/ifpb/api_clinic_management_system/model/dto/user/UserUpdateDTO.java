package ifpb.api_clinic_management_system.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserUpdateDTO(

        @Email
        @Size(max = 50)
        String email,

        @Size(min = 8, max = 255)
        String newPassword,

        Boolean enabled,

        Set<Long> roleIds
) {}
