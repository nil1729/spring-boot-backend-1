package tech.nilanjan.spring.backend.main.ui.controller;

import com.google.common.base.Strings;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.nilanjan.spring.backend.main.exceptions.UserServiceException;
import tech.nilanjan.spring.backend.main.service.AddressService;
import tech.nilanjan.spring.backend.main.service.UserService;
import tech.nilanjan.spring.backend.main.shared.dto.AddressDto;
import tech.nilanjan.spring.backend.main.shared.dto.UserDto;
import tech.nilanjan.spring.backend.main.ui.model.request.UserRequestDetails;
import tech.nilanjan.spring.backend.main.ui.model.response.OperationStatusRest;
import tech.nilanjan.spring.backend.main.ui.model.response.UserAddressRest;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.ErrorMessages;
import tech.nilanjan.spring.backend.main.ui.model.response.UserRest;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationNames;
import tech.nilanjan.spring.backend.main.ui.model.response.constant.OperationStatus;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/v1/profile")
public class UserProfileController {
    private final UserService userService;
    private final AddressService addressService;

    @Autowired
    public UserProfileController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<EntityModel<UserRest>> getUserProfile(Authentication authResult) {
        String userEmail = authResult.getName();
        UserDto userDetails = userService.getUserByEmail(userEmail);

        UserRest returnValue = new ModelMapper().map(userDetails, UserRest.class);

        // Links
        Link selfLink = WebMvcLinkBuilder
                .linkTo(UserProfileController.class)
                .withSelfRel();
        Link addressesLink = WebMvcLinkBuilder
                .linkTo(
                        WebMvcLinkBuilder
                                .methodOn(UserProfileController.class)
                                .getUserAddressList(authResult)
                )
                .withRel("addresses");


        return ResponseEntity.ok().body(EntityModel.of(returnValue,
                Arrays.asList(selfLink, addressesLink)
        ));
    }

    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserRest> updateUserProfile(
            Authentication authResult,
            @RequestBody UserRequestDetails userDetails
    ) {
        String userEmail = authResult.getName();

        if(Strings.isNullOrEmpty(userDetails.getFirstName()) ||
                Strings.isNullOrEmpty(userDetails.getLastName())) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(userEmail, userDto);

        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(updatedUser, returnValue);

        return ResponseEntity.ok().body(returnValue);
    }

    @DeleteMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE
            }
    )
    public ResponseEntity<OperationStatusRest> deleteProfile(Authentication authResult) {
        OperationStatusRest operationStatusRest = new OperationStatusRest();
        operationStatusRest.setOperationName(OperationNames.DELETE.name());

        String userEmail = authResult.getName();
        userService.deleteUser(userEmail);

        operationStatusRest.setOperationStatus(OperationStatus.SUCCESS.name());
        return ResponseEntity.ok().body(operationStatusRest);
    }

    @GetMapping(
            path = "/addresses"
    )
    public ResponseEntity<CollectionModel<UserAddressRest>> getUserAddressList(Authentication authResult) {
        String userEmail = authResult.getName();

        List<AddressDto> addressDtoList = addressService.getAddressList(userEmail);

        ModelMapper modelMapper = new ModelMapper();
        List<UserAddressRest> returnValue
                = modelMapper.map(addressDtoList, new TypeToken<List<UserAddressRest>>() {}.getType());

        for (UserAddressRest addressRest: returnValue) {
            Link selfLink = WebMvcLinkBuilder
                    .linkTo(
                            WebMvcLinkBuilder
                                    .methodOn(UserProfileController.class)
                                    .getUserAddressById(authResult, addressRest.getAddressId())
                    )
                    .withSelfRel();

            addressRest.add(selfLink);
        }

        // Links
        Link profileLink = WebMvcLinkBuilder
                .linkTo(UserProfileController.class)
                .withRel("profile");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(
                        WebMvcLinkBuilder
                                .methodOn(UserProfileController.class)
                                .getUserAddressList(authResult)
                )
                .withSelfRel();

        return ResponseEntity.ok().body(
                CollectionModel.of(returnValue, Arrays.asList(profileLink, selfLink))
        );
    }

    @GetMapping(
            path = "/addresses/{addressId}"
    )
    public ResponseEntity<UserAddressRest> getUserAddressById(
            Authentication authResult,
            @PathVariable String addressId
    ){
        String userEmail = authResult.getName();

        AddressDto addressDto = addressService.getAddressById(userEmail, addressId);

        UserAddressRest returnValue = new ModelMapper().map(addressDto, UserAddressRest.class);

        // Links
        Link profileLink = WebMvcLinkBuilder
                .linkTo(UserProfileController.class)
                .withRel("profile");
        Link addressesLink = WebMvcLinkBuilder
                .linkTo(UserProfileController.class)
                .slash("addresses")
                .withRel("addresses");
        Link selfLink = WebMvcLinkBuilder
                .linkTo(UserProfileController.class)
                .slash("addresses")
                .slash(addressId)
                .withSelfRel();

        returnValue.add(profileLink).add(addressesLink).add(selfLink);

        return ResponseEntity.ok().body(returnValue);
    }
}
