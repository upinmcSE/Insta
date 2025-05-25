package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.request.*;
import init.upinmcse.backend.dto.response.JwtResponse;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface IAuthService {
    void register(RegisterRequest request) throws MessagingException, UnsupportedEncodingException;
    JwtResponse login(LoginRequest request);
    JwtResponse refreshToken(RefreshRequest request);
    void verifyEmail(VerifyRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
