package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.*;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface IAuthService {
    void register(RegisterRequest request) throws MessagingException, UnsupportedEncodingException;
    JwtResponse login(LoginRequest request);
    void logout(String token);
    JwtResponse refreshToken(String token);
    void verifyEmail(VerifyRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPassword request);
}
