package org.example.userhandleapi.Service;

import org.example.coreapi.Entities.OTPStorage;
import org.example.coreapi.Repositories.OTPStorageRepository;
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
        String generatedOTP = generateOTP(6);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Echikitsa Login OTP");
        message.setText("Your one time password is : " + generatedOTP);
        Instant expirationTime = Instant.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        OTPStorage formate = new OTPStorage();
        formate.setEmail(to);
        formate.setGeneratedOTP(generatedOTP);
        formate.setExpirationTime(expirationTime);


        try {
            javaMailSender.send(message);
            OTPStorage otpData = otpSrorageRepo.findEmail(to);
            //System.out.println(otpData.getEmail());
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
        //String generatedOTP = generateOTP(6);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Echikitsa Login Password");
        message.setText("Your password for eChikitsa app is : " + password);
       // Instant expirationTime = Instant.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

//        OTPStorage formate = new OTPStorage();
//        formate.setEmail(to);
//        formate.setGeneratedOTP(generatedOTP);
//        formate.setExpirationTime(expirationTime);


        try {
            javaMailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MailException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private static final String OTP_CHARS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(OTP_CHARS.length());
            char otpChar = OTP_CHARS.charAt(randomIndex);
            otp.append(otpChar);
        }
        return otp.toString();
    }

    // Validate the OTP entered by the user
    public boolean validateOTP(String to, String enteredOTP) {
        OTPStorage otpData = otpSrorageRepo.findEmail(to);
        // System.out.println(otpData.getGeneratedOTP());
        if (otpData == null || otpData.getExpirationTime().isBefore(Instant.now())) {
            return false; // No OTP found for the user
        }
        if (Objects.equals(otpData.getGeneratedOTP(), enteredOTP)) {
            return true; // OTP has expired
        }
        return false;
    }
}
