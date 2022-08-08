package tech.nilanjan.spring.backend.main.security.constant;

import org.springframework.http.HttpMethod;

public enum PublicRoutes {
    AUTH_ROUTE("/v1/auth/**", HttpMethod.POST),
    EMAIL_VERIFICATION_ROUTE("/v1/email-verification", HttpMethod.GET);

    private final String route;
    private final HttpMethod httpMethod;

    PublicRoutes(String route, HttpMethod httpMethod) {
        this.route = route;
        this.httpMethod = httpMethod;
    }

    public String getRoute() {
        return route;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
