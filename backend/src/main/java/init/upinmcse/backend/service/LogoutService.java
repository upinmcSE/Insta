package init.upinmcse.backend.service;

import init.upinmcse.backend.enums.TYPE_TOKEN;
import init.upinmcse.backend.model.TokenRevoked;
import init.upinmcse.backend.repository.TokenRevokedRepository;
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

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        jwt = authHeader.substring(7);
        log.info("JWT token: {}", jwt);
        try {
            String id = jwtService.extractId(jwt, TYPE_TOKEN.ACCESS_TOKEN);
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
                        .expiryTime(jwtService.extractExpiration(jwt, TYPE_TOKEN.ACCESS_TOKEN))
                        .build();
                tokenRevokedRepository.save(storedToken);
            }
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_OK);

        }catch (Exception e){
            log.error("Error when logout: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
