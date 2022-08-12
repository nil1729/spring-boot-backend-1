package tech.nilanjan.spring.backend.main.ui.model.response;

public class LoginRest {
    private String accessToken;

    public LoginRest(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
