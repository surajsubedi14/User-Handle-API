package org.example.userhandleapi.Service;

import org.example.coreapi.Entities.OTPStorage;
import org.example.coreapi.Repositories.OTPStorageRepository;
import org.example.userhandleapi.DTO.ContactUsDTO;
import org.example.userhandleapi.Helper.EmailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class EmailService {

    @Autowired
    private   JavaMailSender javaMailSender;

    @Autowired
    private OTPStorageRepository otpSrorageRepo;



    private static final int OTP_EXPIRATION_MINUTES = 5;


    public void sendEmail(String to) {
        String generatedOTP = EmailHelper.generateOTP(6);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("eChikitsa Login OTP");
        message.setText("Your one time password is : " + generatedOTP);
        Instant expirationTime = Instant.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        OTPStorage formate = new OTPStorage();
        formate.setEmail(to);
        formate.setGeneratedOTP(generatedOTP);
        formate.setExpirationTime(expirationTime);


        try {
            javaMailSender.send(message);
            OTPStorage otpData = otpSrorageRepo.findEmail(to);
            if (otpData == null) {
                otpSrorageRepo.save(formate);
            } else {
                otpSrorageRepo.update(to, expirationTime, generatedOTP);
            }
            System.out.println("Email sent successfully!");
        } catch (MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendPasswordToDoctor(String to,String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Echikitsa Login Password");
        message.setText("Your password for eChikitsa app is : " + password);
        try {
            javaMailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendStatusToDoctor(String to, String status, String doctorName, String hospitalName) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Status on eChikitsa Got update by your Hospital");
        if(Objects.equals(status, "activated"))
        {
            message.setText(EmailHelper.getEmailBodyForActive(status, doctorName, hospitalName));
        }
        else {
            message.setText(EmailHelper.getEmailBodyForDeactive(status, doctorName, hospitalName));
        }

        try {
            javaMailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendPromotionMailToDoctor(String to, String doctorName, String hospitalName) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Congratulations! Exciting News: Promotion Announcement");
        message.setText(EmailHelper.getEmailBodyForPromotion(doctorName, hospitalName));


        try {
            javaMailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }



    public boolean validateOTP(String to, String enteredOTP) {
        OTPStorage otpData = otpSrorageRepo.findEmail(to);
        if (otpData == null || otpData.getExpirationTime().isBefore(Instant.now())) {
            return false;
        }
        if (Objects.equals(otpData.getGeneratedOTP(), enteredOTP)) {
            return true;
        }
        return false;
    }

    public String sendContactUsMail(ContactUsDTO contactUsDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("eChikitsaapp@gmail.com");
        message.setSubject(contactUsDTO.getSubject() + " from "+contactUsDTO.getName());
        message.setText(contactUsDTO.getMessage());
        try {
            javaMailSender.send(message);
            return "Email sent successfully!";
        } catch (MailException e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}
