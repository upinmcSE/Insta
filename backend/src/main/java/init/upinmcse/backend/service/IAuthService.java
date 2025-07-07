package init.upinmcse.backend.service;

import init.upinmcse.backend.dto.request.*;
import init.upinmcse.backend.dto.response.JwtResponse;
import init.upinmcse.backend.dto.response.RegisterResponse;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public interface IAuthService {
    RegisterResponse register(RegisterRequest request) throws MessagingException, UnsupportedEncodingException;
    JwtResponse login(LoginRequest request) throws ParseException;
    JwtResponse refreshToken(RefreshRequest request);
    void verifyEmail(VerifyRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
