package tech.nilanjan.spring.backend.main.service.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.io.entity.AddressEntity;
import tech.nilanjan.spring.backend.main.io.entity.UserEntity;
import tech.nilanjan.spring.backend.main.repo.UserAddressRepository;
import tech.nilanjan.spring.backend.main.repo.UserRepository;
import tech.nilanjan.spring.backend.main.service.AddressService;
import tech.nilanjan.spring.backend.main.shared.dto.AddressDto;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    @Autowired
    public AddressServiceImpl(
            UserRepository userRepository,
            UserAddressRepository userAddressRepository
    ) {
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
    }

    @Override
    public List<AddressDto> getAddressList(String userEmail) {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByEmail(userEmail);
        if(userEntity.isEmpty()) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        List<AddressEntity> addressEntityList =
                userAddressRepository.findAddressEntitiesByUserDetails(userEntity.get());

        ModelMapper modelMapper = new ModelMapper();
        List<AddressDto> returnValue
                = modelMapper.map(addressEntityList, new TypeToken<List<AddressDto>>() {}.getType());

        return returnValue;
    }

    @Override
    public AddressDto getAddressById(String userEmail, String addressId) {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByEmail(userEmail);
        if(userEntity.isEmpty()) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        Optional<AddressEntity> addressEntity =
                userAddressRepository.findAddressEntityByUserDetailsAndAddressId(userEntity.get(), addressId);

        if(addressEntity.isEmpty())
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        AddressDto returnValue = new ModelMapper().map(addressEntity.get(), AddressDto.class);

        return returnValue;
    }
}
