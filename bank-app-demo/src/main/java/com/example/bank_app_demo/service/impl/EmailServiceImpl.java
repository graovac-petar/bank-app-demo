package com.example.bank_app_demo.service.impl;

import com.example.bank_app_demo.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class EmailServiceImpl implements  EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            System.out.println("Email sent successfully");
        }
        catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try{
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(senderEmail);
            helper.setTo(emailDetails.getRecipient());
            helper.setText(emailDetails.getMessageBody());
            helper.setSubject(emailDetails.getSubject());

            FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
            helper.addAttachment("BankStatement.pdf", file);
            javaMailSender.send(mimeMessage);

            log.info("Email sent with attachment successfully");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
