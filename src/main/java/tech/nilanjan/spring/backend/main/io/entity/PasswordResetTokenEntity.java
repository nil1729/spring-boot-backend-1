package tech.nilanjan.spring.backend.main.io.entity;

import javax.persistence.*;

@Entity(
        name = "PasswordResetToken"
)
@Table(
        name = "password_reset_tokens"
)
public class PasswordResetTokenEntity {
    @Id
    @SequenceGenerator(
            name = "password_reset_token_sequence",
            sequenceName = "password_reset_token_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "password_reset_token_sequence",
            strategy = GenerationType.AUTO
    )
    private Long id;

    @Column(name = "token", columnDefinition = "text")
    private String token;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            unique = true
    )
    private UserEntity userDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
