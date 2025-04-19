package init.upinmcse.backend.controller;

import init.upinmcse.backend.dto.*;
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

    @PostMapping("/register")
    public BaseResponse<Void> register(@Valid @RequestBody RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        authenticationService.register(request);
        return BaseResponse.<Void>builder()
                .message("Register successfully")
                .build();
    }

    @PostMapping("/verify")
    public BaseResponse<Void> verify(@Valid @RequestBody VerifyRequest request) {
        authenticationService.verifyEmail(request);
        return BaseResponse.<Void>builder()
                .message("Verify successfully")
                .build();
    }

    @PostMapping("/login")
    public BaseResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return BaseResponse.<JwtResponse>builder()
                .message("Login successfully")
                .result(authenticationService.login(request))
                .build();
    }

    @PostMapping("/forgot-password")
    public BaseResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return BaseResponse.<Void>builder()
                .message("Forgot password successfully")
                .build();
    }

    @PostMapping("/reset-password")
    public BaseResponse<Void> resetPassword(@Valid @RequestBody ResetPassword request) {
        authenticationService.resetPassword(request);
        return BaseResponse.<Void>builder()
                .message("Reset password successfully")
                .build();
    }
}
