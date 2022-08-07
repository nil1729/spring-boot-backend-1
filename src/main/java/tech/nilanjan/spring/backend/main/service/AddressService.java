package tech.nilanjan.spring.backend.main.service;

import tech.nilanjan.spring.backend.main.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto>  getAddressList(String userEmail);

    AddressDto getAddressById(String userEmail, String addressId);
}
