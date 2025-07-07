package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.constant.PredefinedRole;
import init.upinmcse.backend.dto.request.*;
import init.upinmcse.backend.dto.response.JwtResponse;
import init.upinmcse.backend.dto.response.RegisterResponse;
import init.upinmcse.backend.enums.Status;
import init.upinmcse.backend.enums.TYPE_TOKEN;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.Role;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.cache.impl.TokenRedis;
import init.upinmcse.backend.repository.db.RoleRepository;
import init.upinmcse.backend.repository.db.UserRepository;
import init.upinmcse.backend.service.IAuthService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthService implements IAuthService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    MailService mailService;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    TokenRedis tokenRedis;


    private static String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(5);
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        boolean existedUser = userRepository.existsByEmail(request.getEmail());
        if (existedUser) {
            throw new ErrorException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Role role = roleRepository.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new ErrorException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(Set.of(role))
                .status(Status.ACTIVE)
                .build();
        user = userRepository.save(user);

        return RegisterResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public JwtResponse login(LoginRequest request) throws ParseException {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ErrorException(ErrorCode.NOT_FOUND_USER));

        var authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new ErrorException(ErrorCode.UNAUTHENTICATED);
        }
        var accessToken = jwtService.generateToken(user.getEmail(), TYPE_TOKEN.ACCESS_TOKEN);
        var refreshToken = jwtService.generateToken(user.getEmail(), TYPE_TOKEN.REFRESH_TOKEN);

        var refreshTokenId = jwtService.extractJwtId(refreshToken, TYPE_TOKEN.REFRESH_TOKEN);
        var expiration = jwtService.extractExpiration(refreshToken, TYPE_TOKEN.REFRESH_TOKEN);

        // Tính khoảng thời gian còn lại (từ hiện tại đến expiration) bằng milliseconds
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = expiration.getTime();
        long durationInMillis = expirationTimeMillis - currentTimeMillis;

        // Chuyển thành số giây
        long expirationInSeconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis);

        // Đảm bảo expirationInSeconds không âm
        if (expirationInSeconds <= 0) {
            throw new IllegalArgumentException("Token has already expired");
        }

        tokenRedis.set(user.getId(), refreshTokenId, expirationInSeconds);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenId)
                .build();
    }


    @Override
    public JwtResponse refreshToken(RefreshRequest request) {
        return null;
    }

    @Override
    public void verifyEmail(VerifyRequest request) {
        log.info("email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String code = generateCode();
        String hashedToken = passwordEncoder.encode(code);
        userRepository.save(user);
        try {
            mailService.sendEmail(
                    request.getEmail(),
                    "Xác thực tài khoản",
                    "Mã xác thực của bạn là: " + code
            );
        } catch (Exception e) {
            log.info("Error while sending email: {}", e.getMessage());
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
