package init.upinmcse.backend.service.impl;

import init.upinmcse.backend.constant.PredefinedRole;
import init.upinmcse.backend.dto.common.UserInfo;
import init.upinmcse.backend.dto.request.*;
import init.upinmcse.backend.dto.response.JwtResponse;
import init.upinmcse.backend.dto.response.RegisterResponse;
import init.upinmcse.backend.constant.Status;
import init.upinmcse.backend.constant.TokenType;
import init.upinmcse.backend.exception.ErrorCode;
import init.upinmcse.backend.exception.ErrorException;
import init.upinmcse.backend.model.Role;
import init.upinmcse.backend.model.User;
import init.upinmcse.backend.repository.cache.impl.TokenRedis;
import init.upinmcse.backend.repository.db.RoleRepository;
import init.upinmcse.backend.repository.db.TokenRevokedRepository;
import init.upinmcse.backend.repository.db.UserRepository;
import init.upinmcse.backend.repository.http.OutboundIdentityClient;
import init.upinmcse.backend.repository.http.OutboundUserClient;
import init.upinmcse.backend.service.IAuthService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Date;
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
    TokenRevokedRepository tokenRevokedRepository;
    TokenRedis tokenRedis;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;

    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    private static String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(5);
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private void storeRefreshToken(String userId, String refreshToken) throws ParseException {
        Date expiration = jwtService.extractExpiration(refreshToken, TokenType.REFRESH_TOKEN);
        long expirationInSeconds = calculateExpirationInSeconds(expiration);
        tokenRedis.set(userId, refreshToken, expirationInSeconds);
    }

    private long calculateExpirationInSeconds(Date expiration) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = expiration.getTime();
        long durationInMillis = expirationTimeMillis - currentTimeMillis;
        long expirationInSeconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis);

        if (expirationInSeconds <= 0) {
            throw new IllegalArgumentException("Token has already expired");
        }
        return expirationInSeconds;
    }

    @Override
    public JwtResponse outboundAuthentication(String code) throws ParseException {
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        log.info("TOKEN RESPONSE {}", response);

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());

        log.info("User Info {}", userInfo);

        User existingUser = userRepository.findByEmail(userInfo.getEmail()).orElseGet(
                () -> userRepository.save(
                        User.builder()
                                .email(userInfo.getEmail())
                                .fullName(userInfo.getName())
                                .password("")
                                .avtUrl(userInfo.getPicture())
                                .roles(Set.of(
                                        roleRepository.findByName(PredefinedRole.USER_ROLE)
                                                .orElseThrow(() -> new ErrorException(ErrorCode.ROLE_NOT_FOUND))
                                ))
                                .status(Status.ACTIVE)
                                .build()
                )
        );

        var accessToken = jwtService.generateToken(existingUser.getId(), TokenType.ACCESS_TOKEN);
        var refreshToken = jwtService.generateToken(existingUser.getId(), TokenType.REFRESH_TOKEN);

        storeRefreshToken(existingUser.getId(), refreshToken);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .userInfo(UserInfo.builder()
                        .id(existingUser.getId())
                        .fullName(existingUser.getFullName())
                        .avatarUrl(existingUser.getAvtUrl())
                        .build())
                .build();
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

        var accessToken = jwtService.generateToken(user.getId(), TokenType.ACCESS_TOKEN);
        var refreshToken = jwtService.generateToken(user.getId(), TokenType.REFRESH_TOKEN);

        storeRefreshToken(user.getId(), refreshToken);

        var userInfo = UserInfo.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvtUrl())
                .build();

        return JwtResponse.builder()
                .accessToken(accessToken)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public JwtResponse refreshToken(RefreshRequest request) throws ParseException {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_USER));
        var tokenId = jwtService.extractJwtId(request.getToken(), TokenType.ACCESS_TOKEN);

        if (tokenId == null || tokenRevokedRepository.existsById(tokenId)) {
            throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }

        String refreshToken = tokenRedis.get(request.getUserId());
        if (refreshToken == null) {
            throw new ErrorException(ErrorCode.INVALID_TOKEN);
        }

        var token = jwtService.generateToken(user.getEmail(), TokenType.ACCESS_TOKEN);
        refreshToken = jwtService.generateToken(user.getEmail(), TokenType.REFRESH_TOKEN);

        storeRefreshToken(user.getId(), refreshToken);

        var userInfo = UserInfo.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvtUrl())
                .build();

        return JwtResponse.builder()
                .accessToken(token)
                .userInfo(userInfo)
                .build();
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