package init.upinmcse.backend.config.security;

public class Endpoints {

    public static final String[] PUBLIC_ENPOINT = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/verify",
            "/api/v1/auth/reset-password",
            "/api/v1/files/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

}