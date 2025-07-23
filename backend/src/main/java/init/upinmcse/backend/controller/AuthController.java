package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.request.*;
import init.upinmcse.backend.dto.response.JwtResponse;
import init.upinmcse.backend.dto.response.RegisterResponse;
import init.upinmcse.backend.service.IAuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j(topic = "AuthController")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    IAuthService authenticationService;

    @PostMapping("/outbound/authentication")
    public BaseResponse<JwtResponse> outboundAuthentication(@RequestParam("code") String code) throws ParseException {
        return BaseResponse.<JwtResponse>builder()
                .message("Outbound authentication successful")
                .result(authenticationService.outboundAuthentication(code))
                .build();
    }

    // UC-1: Register
    @PostMapping("/register")
    public BaseResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        return BaseResponse.<RegisterResponse>builder()
                .message("Register successfully")
                .result(authenticationService.register(request))
                .build();
    }

    // UC-2: Login
    @PostMapping("/login")
    public BaseResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request) throws ParseException {
        return BaseResponse.<JwtResponse>builder()
                .message("Login successfully")
                .result(authenticationService.login(request))
                .build();
    }

    // UC-3: Forgot password
    @PostMapping("/forgot-password")
    public BaseResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return BaseResponse.<Void>builder()
                .message("Forgot password successfully")
                .build();
    }

    // UC-3.1: Verify email
    @PostMapping("/verify")
    public BaseResponse<Void> verify(@Valid @RequestBody VerifyRequest request) {
        authenticationService.verifyEmail(request);
        return BaseResponse.<Void>builder()
                .message("Verify successfully")
                .build();
    }

    // UC-3.2: Reset password
    @PostMapping("/reset-password")
    public BaseResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return BaseResponse.<Void>builder()
                .message("Reset password successfully")
                .build();
    }

    // UC-4: Refresh token
    @PostMapping("/refresh-token")
    public BaseResponse<JwtResponse> refreshToken(@Valid @RequestBody RefreshRequest request) throws ParseException {
        return BaseResponse.<JwtResponse>builder()
                .message("Refresh token successfully")
                .result(authenticationService.refreshToken(request))
                .build();
    }
}
