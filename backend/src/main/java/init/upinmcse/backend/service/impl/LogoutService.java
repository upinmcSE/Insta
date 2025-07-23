package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.constant.TokenType;
import init.upinmcse.backend.model.TokenRevoked;
import init.upinmcse.backend.repository.cache.impl.TokenRedis;
import init.upinmcse.backend.repository.db.TokenRevokedRepository;
import init.upinmcse.backend.service.IJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LogoutService implements LogoutHandler {
    TokenRevokedRepository tokenRevokedRepository;
    IJwtService jwtService;
    TokenRedis tokenRedis;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String userId = request.getHeader("userId");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        jwt = authHeader.substring(7);
        log.info("JWT token: {}", jwt);
        try {
            String id = jwtService.extractJwtId(jwt, TokenType.ACCESS_TOKEN);
            log.info("Extracted ID: {}", id);
            if( id == null) {
                log.error("Id is null, cannot revoke token");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            TokenRevoked storedToken = tokenRevokedRepository.findById(id).orElse(null);
            if (storedToken == null) {
                storedToken = TokenRevoked.builder()
                        .id(id)
                        .expiryTime(jwtService.extractExpiration(jwt, TokenType.ACCESS_TOKEN))
                        .build();
                tokenRevokedRepository.save(storedToken);
            }

            tokenRedis.delete(userId);

            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_OK);

        }catch (Exception e){
            log.error("Error when logout: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
