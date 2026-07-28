package ifpb.api_clinic_management_system.mapper;

import ifpb.api_clinic_management_system.model.dto.address.AddressDTO;
import ifpb.api_clinic_management_system.model.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toAddress(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        return Address.builder()
                .street(dto.street())
                .city(dto.city())
                .state(dto.state())
                .zipCode(dto.zipCode())
                .build();
    }

    public AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }
}