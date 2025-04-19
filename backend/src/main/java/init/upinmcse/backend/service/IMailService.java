package init.upinmcse.backend.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface IMailService {
    String sendEmail(String recipients, String subject, String content)
            throws MessagingException, UnsupportedEncodingException;
}
