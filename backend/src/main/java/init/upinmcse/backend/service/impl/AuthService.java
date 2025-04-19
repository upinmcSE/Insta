package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.dto.*;
import init.upinmcse.backend.enums.GENDER;
import init.upinmcse.backend.enums.RoleType;
import init.upinmcse.backend.enums.TYPE_TOKEN;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.UserRepository;
import init.upinmcse.backend.service.IAuthService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthService implements IAuthService {
    UserRepository userRepository;
    MailService mailService;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtService jwtService;



    private static String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(5);
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    @Override
    public void register(RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        boolean existedUser = userRepository.existsByEmail(request.getEmail());
        if (existedUser) {
            log.error("Kiểm tra xem tài khoản đã kích hoạt chưa");
        }

        String code = generateCode();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.USER)
                .enabled(false)
                .gender(GENDER.OTHER)
                .verifyCode(passwordEncoder.encode(code))
                .verifyExpired(LocalDateTime.now().plusMinutes(4))
                .build();
        userRepository.save(user);

        mailService.sendEmail(
                request.getEmail(),
                "Xác thực tài khoản",
                "Mã xác thực của bạn là: " + code
        );
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        try{
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

            if (!user.isEnabled()) {
                throw new ErrorException(ErrorCode.USER_NOT_ACTIVATED);
            }

            // Thực hiện xác thực với username và password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                // Nếu xác thực thành công, lưu thông tin vào SecurityContext
                String accessToken = jwtService.generateToken(request.getEmail(), TYPE_TOKEN.ACCESS_TOKEN);
                String refreshToken = jwtService.generateToken(request.getEmail(), TYPE_TOKEN.REFRESH_TOKEN);
                return JwtResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }

        } catch (AuthenticationException e) {
            throw new ErrorException(ErrorCode.UNAUTHENTICATED);
        }
        throw new ErrorException(ErrorCode.UNAUTHENTICATED);
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public JwtResponse refreshToken(String token) {
        return null;
    }

    @Override
    public void verifyEmail(VerifyRequest request) {
        log.info("email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        if(passwordEncoder.matches(request.getCode(), user.getVerifyCode())){
            if(user.getVerifyExpired().isBefore(LocalDateTime.now())){
                throw new ErrorException(ErrorCode.ACTIVATION_CODE_NOT_MATCH);
            }

            user.setEnabled(true);
            user.setVerifyCode(null);
            user.setVerifyExpired(null);
            userRepository.save(user);
        } else {
            throw new ErrorException(ErrorCode.ACTIVATION_CODE_NOT_MATCH);
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        String code = generateCode();
        String hashedToken = passwordEncoder.encode(code);
        user.setVerifyCode(hashedToken);
        user.setVerifyExpired(LocalDateTime.now().plusMinutes(4));
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
    public void resetPassword(ResetPassword request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (passwordEncoder.matches(request.getCode(), user.getVerifyCode())
                && !user.getVerifyExpired().isBefore(LocalDateTime.now())) {
            user.setVerifyCode(null);
            user.setVerifyExpired(null);
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        } else if (passwordEncoder.matches(request.getCode(), user.getVerifyCode())
                && user.getVerifyExpired().isBefore(LocalDateTime.now())) {
            throw new ErrorException(ErrorCode.ACTIVATION_CODE_NOT_MATCH);
        } else {
            throw new ErrorException(ErrorCode.ACTIVATION_CODE_NOT_MATCH);
        }
    }
}
