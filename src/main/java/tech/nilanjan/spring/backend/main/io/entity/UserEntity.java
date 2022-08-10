package tech.nilanjan.spring.backend.main.io.entity;

import javax.persistence.*;
import java.util.List;

@Entity(name = "User")
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = {"email"}
                ),
                @UniqueConstraint(
                        name = "user_user_id_unique",
                        columnNames = {"user_id"}
                )
        }
)
public class UserEntity {
    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "student_sequence"
    )
    private Long id;
    @Column(
            length = 30,
            nullable = false,
            name = "user_id",
            updatable = false
    )
    private String userId;
    @Column(
            length = 50,
            nullable = false,
            name = "first_name"
    )
    private String firstName;
    @Column(
            length = 50,
            nullable = false,
            name = "last_name"
    )
    private String lastName;

    @Column(
            length = 100,
            nullable = false,
            name = "email"
    )
    private String email;

    @Column(
            nullable = false,
            name = "password"
    )
    private String password;

    @Column(name = "email_verification_token", columnDefinition = "text")
    private String emailVerificationToken;

    @Column(
            nullable = false,
            name = "email_verification_status"
    )
    private Boolean emailVerificationStatus = false;

    @OneToMany(
            mappedBy = "userDetails",
            cascade = {CascadeType.ALL}
    )
    private List<AddressEntity> addresses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public Boolean getEmailVerificationStatus() {
        return emailVerificationStatus;
    }

    public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
        this.emailVerificationStatus = emailVerificationStatus;
    }

    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }
}
