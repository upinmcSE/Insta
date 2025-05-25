package init.upinmcse.backend.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.enums.TYPE_TOKEN;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.repository.TokenRevokedRepository;
import init.upinmcse.backend.service.impl.JwtService;
import init.upinmcse.backend.service.impl.UserSecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "JwtFilter")
public class JwtFilter extends OncePerRequestFilter {

    JwtService jwtService;
    UserSecurityService userSecurityService;
    TokenRevokedRepository tokenRevokedRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                username = jwtService.extractEmail(token, TYPE_TOKEN.ACCESS_TOKEN);
                log.info("Extracted username: {}", username);
            } catch (Exception e) {
                log.error("Error extracting username from token", e);
                handleTokenError(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userSecurityService.loadUserByUsername(username);

                // Check if token is revoked (logout case)
                String tokenId = null;
                try {
                    tokenId = jwtService.extractId(token, TYPE_TOKEN.ACCESS_TOKEN);
                } catch (Exception e) {
                    log.error("Error extracting token ID", e);
                    handleTokenError(response, ErrorCode.INVALID_TOKEN);
                    return;
                }

                if (tokenId != null && tokenRevokedRepository.existsById(tokenId)) {
                    log.info("Token revoked for user {}", username);
                    handleTokenError(response, ErrorCode.TOKEN_REVOKED);
                    return;
                }

                // Validate token (expiration case)
                if (!jwtService.validateToken(token, TYPE_TOKEN.ACCESS_TOKEN, userDetails)) {
                    log.info("Token expired for user {}", username);
                    handleTokenError(response, ErrorCode.TOKEN_EXPIRED);
                    return;
                }

                // If token is valid, set authentication
                log.info("Token valid. Roles: {}", userDetails.getAuthorities());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (Exception e) {
                log.error("Error during token validation", e);
                handleTokenError(response, ErrorCode.INVALID_TOKEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleTokenError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        BaseResponse<?> apiResponse = BaseResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}