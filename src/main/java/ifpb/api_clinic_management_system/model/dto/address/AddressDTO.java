package ifpb.api_clinic_management_system.model.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDTO(

        @NotBlank
        String street,

        @NotBlank
        String city,

        @NotBlank
        String state,

        @NotBlank
        @Size(max = 10)
        String zipCode
) {}