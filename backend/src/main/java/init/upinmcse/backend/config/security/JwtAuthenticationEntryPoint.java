package init.upinmcse.backend.config.security;

import java.io.IOException;
import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        if (authException instanceof OAuth2AuthenticationException oauth2Exception) {
            OAuth2Error error = oauth2Exception.getError();
            String errorCodeStr = error.getErrorCode();

            // Map the OAuth2Error code to your ErrorCode enum
            if (String.valueOf(ErrorCode.TOKEN_EXPIRED.getCode()).equals(errorCodeStr)) {
                errorCode = ErrorCode.TOKEN_EXPIRED;
            } else if (String.valueOf(ErrorCode.INVALID_TOKEN.getCode()).equals(errorCodeStr)) {
                errorCode = ErrorCode.INVALID_TOKEN;
            }
        }

        log.warn("Authentication failed [{}]: {} {}", errorCode, request.getMethod(), request.getRequestURI());
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