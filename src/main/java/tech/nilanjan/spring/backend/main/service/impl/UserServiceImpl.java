package tech.nilanjan.spring.backend.main.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.io.entity.PasswordResetTokenEntity;
import tech.nilanjan.spring.backend.main.io.entity.UserEntity;
import tech.nilanjan.spring.backend.main.repo.PasswordResetTokenRepository;
import tech.nilanjan.spring.backend.main.shared.utils.EmailSenderUtils;
import tech.nilanjan.spring.backend.main.shared.utils.EmailVerificationUtils;
import tech.nilanjan.spring.backend.main.shared.utils.RandomIdUtils;
import tech.nilanjan.spring.backend.main.repo.UserRepository;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.shared.dto.AddressDto;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;
import tech.nilanjan.spring.backend.main.shared.utils.ResetPasswordUtils;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RandomIdUtils randomIdUtils;
    private final EmailVerificationUtils emailVerificationUtils;
    private final ResetPasswordUtils resetPasswordUtils;
    private final EmailSenderUtils emailSenderUtils;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            RandomIdUtils randomIdUtils,
            EmailVerificationUtils emailVerificationUtils,
            ResetPasswordUtils resetPasswordUtils,
            EmailSenderUtils emailSenderUtils
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.randomIdUtils = randomIdUtils;
        this.emailVerificationUtils = emailVerificationUtils;
        this.resetPasswordUtils = resetPasswordUtils;
        this.emailSenderUtils = emailSenderUtils;
    }

    @Override
    public UserDto createUser(UserDto user, HttpServletRequest request) {
        Optional<UserEntity> existingUser = userRepository.findUserEntityByEmail(user.getEmail());
        if (existingUser.isPresent()) {
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
        String emailVerificationToken = emailVerificationUtils.generateVerificationToken(userId, request);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationStatus(false);
        userEntity.setUserId(userId);
        userEntity.setEmailVerificationToken(emailVerificationToken);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        emailSenderUtils.sendVerificationEmail(returnValue.getEmail(), emailVerificationToken);

        return returnValue;
    }

    @Override
    public UserDto getUserByEmail(String userEmail) {
        Optional<UserEntity> userDetails = userRepository.findUserEntityByEmail(userEmail);

        if (userDetails.isPresent()) {
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

        if (userDetails.isPresent()) {
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

        for (UserEntity userEntity : usersList) {
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

        if (userEntity.isPresent() &&
                !emailVerificationUtils.checkIsTokenExpired(emailVerificationToken)) {

            userEntity.get().setEmailVerificationStatus(true);
            userEntity.get().setEmailVerificationToken(null);
            userRepository.save(userEntity.get());

            return true;
        }

        return false;
    }

    @Override
    public Boolean resetPasswordRequest(String email, HttpServletRequest request) {
        Optional<UserEntity> userEntity = userRepository.findUserEntityByEmail(email);

        if (userEntity.isPresent()) {
            Optional<PasswordResetTokenEntity> existingPasswordResetTokenEntity
                    = passwordResetTokenRepository.findPasswordResetTokenEntitiesByUserDetails(userEntity.get());

            String passwordResetToken = resetPasswordUtils.generatePasswordResetToken(userEntity.get().getUserId(), request);

            if (existingPasswordResetTokenEntity.isPresent()) {
                existingPasswordResetTokenEntity.get().setToken(passwordResetToken);

                passwordResetTokenRepository.save(existingPasswordResetTokenEntity.get());
            } else {
                PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
                passwordResetTokenEntity.setToken(passwordResetToken);
                passwordResetTokenEntity.setUserDetails(userEntity.get());

                passwordResetTokenRepository.save(passwordResetTokenEntity);
            }

            emailSenderUtils.sendResetPasswordEmail(email, passwordResetToken);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Boolean resetPassword(String password, String token) {
        Optional<PasswordResetTokenEntity> passwordResetTokenEntity =
                passwordResetTokenRepository.findPasswordResetTokenEntitiesByToken(token);

        if(passwordResetTokenEntity.isPresent() &&
                !resetPasswordUtils.checkIsTokenExpired(passwordResetTokenEntity.get().getToken())) {

            UserEntity userEntity = passwordResetTokenEntity.get().getUserDetails();
            userEntity.setPassword(passwordEncoder.encode(password));
            userRepository.save(userEntity);

            passwordResetTokenRepository.delete(passwordResetTokenEntity.get());

            return true;
        }

        return false;
    }
}
