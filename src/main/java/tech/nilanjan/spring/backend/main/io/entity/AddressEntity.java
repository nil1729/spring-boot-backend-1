package tech.nilanjan.spring.backend.main.io.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "UserAddress")
@Table(
        name = "user_addresses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_address_id_unique",
                        columnNames = {"address_id"}
                )
        }
)
public class AddressEntity implements Serializable {
    @Id
    @SequenceGenerator(
            name = "user_address_sequence",
            sequenceName = "user_address_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "user_address_sequence"
    )
    private Long id;

    @Column(
            nullable = false,
            updatable = false,
            length = 30,
            name = "address_id"
    )
    private String addressId;

    @Column(
            nullable = false,
            length = 15
    )
    private String country;

    @Column(
            nullable = false,
            length = 15
    )
    private String state;

    @Column(
            nullable = false,
            length = 15
    )
    private String city;

    @Column(
            nullable = false,
            length = 100,
            name = "street_name"
    )
    private String streetName;

    @Column(
            nullable = false,
            length = 6,
            name = "postal_code"
    )
    private String postalCode;

    @Column(
            nullable = false,
            length = 10
    )
    private String type;

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private UserEntity userDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
