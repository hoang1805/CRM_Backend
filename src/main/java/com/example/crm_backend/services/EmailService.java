package com.example.crm_backend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mail_sender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        mail_sender = mailSender;
    }

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mail_sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        helper.setFrom("your-email@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // Nội dung hỗ trợ HTML

        mail_sender.send(message);
    }
}
