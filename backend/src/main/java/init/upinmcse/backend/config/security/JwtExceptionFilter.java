package init.upinmcse.backend.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.TokenExpiredException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@Nonnull  HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }catch (TokenExpiredException exception) {
            ErrorCode errorCode = exception.getErrorCode();
            BaseResponse<Object> apiResponse = BaseResponse.builder()
                    .code(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .build();

            response.setStatus(errorCode.getStatusCode().value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        }
    }
}
