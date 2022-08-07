package tech.nilanjan.spring.backend.main.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.nilanjan.spring.backend.main.io.entity.AddressEntity;
import tech.nilanjan.spring.backend.main.io.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity> findAddressEntitiesByUserDetails(UserEntity userEntity);

    Optional<AddressEntity> findAddressEntityByUserDetailsAndAddressId(UserEntity userEntity, String addressId);
}
