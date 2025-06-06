package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.constant.PredefinedRole;
import init.upinmcse.backend.dto.request.*;
import init.upinmcse.backend.dto.response.JwtResponse;
import init.upinmcse.backend.enums.GENDER;
import init.upinmcse.backend.enums.TYPE_TOKEN;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.Role;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.RoleRepository;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthService implements IAuthService {
    UserRepository userRepository;
    RoleRepository roleRepository;
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
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            if (user.isEnabled()) {
                throw new ErrorException(ErrorCode.USER_ALREADY_EXISTS);
            }
        }

        String code = generateCode();

        Role role = roleRepository.findByName(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new ErrorException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(role))
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
            log.info("Authentication: {}", authentication);
            if (authentication.isAuthenticated()) {
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
    public JwtResponse refreshToken(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractEmail(refreshToken, TYPE_TOKEN.REFRESH_TOKEN);
        if (email != null) {
            User user = userRepository.findByEmail(email).orElseThrow();
            if (!user.isEnabled()) {
                throw new ErrorException(ErrorCode.USER_NOT_ACTIVATED);
            }
            String accessToken = jwtService.generateToken(email, TYPE_TOKEN.ACCESS_TOKEN);
        }else{
            throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }
        return JwtResponse.builder()
                .accessToken(jwtService.generateToken(email, TYPE_TOKEN.ACCESS_TOKEN))
                .refreshToken(refreshToken)
                .build();

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
    public void resetPassword(ResetPasswordRequest request) {
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
