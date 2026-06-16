package com.haircare.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@haircare.com");
            message.setTo(email);
            message.setSubject("Password Reset OTP - Hair Care E-Commerce");
            message.setText(buildOtpEmailBody(otp));

            mailSender.send(message);
            logger.info("OTP email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    private String buildOtpEmailBody(String otp) {
        return "Dear User,\n\n" +
                "Your One-Time Password (OTP) for password reset is: " + otp + "\n\n" +
                "This OTP will expire in 5 minutes.\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Hair Care E-Commerce Team";
    }
}
