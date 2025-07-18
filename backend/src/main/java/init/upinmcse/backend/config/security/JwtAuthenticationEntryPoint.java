package init.upinmcse.backend.config.security;

import java.io.IOException;
import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        if (authException.getCause() instanceof TokenExpiredException) {
            errorCode = ErrorCode.TOKEN_EXPIRED;
            log.warn("Token expired: {}", authException.getMessage());
        } else {
            log.error("Authentication failed: {}", authException.getMessage());
        }

        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        BaseResponse<?> baseResponse = BaseResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
        response.flushBuffer();
    }
}