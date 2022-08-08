package tech.nilanjan.spring.backend.main.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.io.entity.UserEntity;
import tech.nilanjan.spring.backend.main.shared.utils.EmailVerificationUtils;
import tech.nilanjan.spring.backend.main.shared.utils.RandomIdUtils;
import tech.nilanjan.spring.backend.main.repo.UserRepository;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.shared.dto.AddressDto;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RandomIdUtils randomIdUtils;
    private final EmailVerificationUtils emailVerificationUtils;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RandomIdUtils randomIdUtils,
            EmailVerificationUtils emailVerificationUtils
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.randomIdUtils = randomIdUtils;
        this.emailVerificationUtils = emailVerificationUtils;
    }

    @Override
    public UserDto createUser(UserDto user, HttpServletRequest request) {
        Optional<UserEntity> existingUser = userRepository.findUserEntityByEmail(user.getEmail());
        if(existingUser.isPresent()) {
            throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDto addressDto = user.getAddresses().get(i);
            addressDto.setUserDetails(user);
            addressDto.setAddressId(randomIdUtils.generateAddressId(30));
            user.getAddresses().set(i, addressDto);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String userId = randomIdUtils.generateUserId(30);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationStatus(false);
        userEntity.setUserId(userId);
        userEntity.setEmailVerificationToken(
                emailVerificationUtils.generateVerificationToken(userId, request)
        );

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto getUserByEmail(String userEmail) {
        Optional<UserEntity> userDetails = userRepository.findUserEntityByEmail(userEmail);

        if(userDetails.isPresent()) {
            UserDto returnValue = new UserDto();
            BeanUtils.copyProperties(userDetails.get(), returnValue);

            return returnValue;
        }

        return null;
    }

    @Override
    @Transactional
    public UserDto updateUser(String userEmail, UserDto userDto) {
        Optional<UserEntity> userDetails = userRepository.findUserEntityByEmail(userEmail);

        if(userDetails.isPresent()) {
            userDetails.get().setFirstName(userDto.getFirstName());
            userDetails.get().setLastName(userDto.getLastName());

            UserEntity updatedUser = userRepository.save(userDetails.get());

            UserDto returnValue = new UserDto();
            BeanUtils.copyProperties(updatedUser, returnValue);

            return returnValue;
        }

        return null;
    }

    @Override
    public void deleteUser(String userEmail) {
        Optional<UserEntity> userDetails = userRepository.findUserEntityByEmail(userEmail);
        userDetails.ifPresent(userRepository::delete);
    }

    @Override
    public List<UserDto> getUsersList(Integer page, Integer limit) {
        List<UserDto> returnValue = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageRequest);
        List<UserEntity> usersList = usersPage.getContent();

        for(UserEntity userEntity: usersList) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    @Transactional
    public Boolean verifyEmailAddress(String emailVerificationToken) {
        Optional<UserEntity> userEntity =
                userRepository.findUserEntityByEmailVerificationToken(emailVerificationToken);

        if(userEntity.isPresent() &&
                !emailVerificationUtils.checkIsTokenExpired(emailVerificationToken)) {

            userEntity.get().setEmailVerificationStatus(true);
            userEntity.get().setEmailVerificationToken(null);
            userRepository.save(userEntity.get());

            return true;
        }

        return false;
    }
}
