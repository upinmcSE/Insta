package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.common.BaseResponse;
import init.upinmcse.backend.dto.request.*;
import init.upinmcse.backend.dto.response.JwtResponse;
import init.upinmcse.backend.service.impl.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authenticationService;

    // UC-1: Register
    @PostMapping("/register")
    public BaseResponse<Void> register(@Valid @RequestBody RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        authenticationService.register(request);
        return BaseResponse.<Void>builder()
                .message("Register successfully")
                .build();
    }

    // UC-2: Verify email
    @PostMapping("/verify")
    public BaseResponse<Void> verify(@Valid @RequestBody VerifyRequest request) {
        authenticationService.verifyEmail(request);
        return BaseResponse.<Void>builder()
                .message("Verify successfully")
                .build();
    }

    // UC-3: Login
    @PostMapping("/login")
    public BaseResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return BaseResponse.<JwtResponse>builder()
                .message("Login successfully")
                .result(authenticationService.login(request))
                .build();
    }

    // UC-4: Forgot password
    @PostMapping("/forgot-password")
    public BaseResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return BaseResponse.<Void>builder()
                .message("Forgot password successfully")
                .build();
    }

    // UC-5: Reset password
    @PostMapping("/reset-password")
    public BaseResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return BaseResponse.<Void>builder()
                .message("Reset password successfully")
                .build();
    }

    @PostMapping("/refresh-token")
    public BaseResponse<JwtResponse> refreshToken(@Valid @RequestBody RefreshRequest request) {
        return BaseResponse.<JwtResponse>builder()
                .message("Refresh token successfully")
                .result(authenticationService.refreshToken(request))
                .build();
    }
}
