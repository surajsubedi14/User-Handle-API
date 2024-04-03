package org.example.userhandleapi.Controller;

import org.example.userhandleapi.DTO.emailDto;
import org.example.userhandleapi.DTO.otpStoreDTO;
import org.example.userhandleapi.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody emailDto email) {
        //System.out.println(email.getEmail());
        emailService.sendEmail(email.getEmail());
        return "Email sent successfully!";
    }

    @PostMapping("/valOtp")
    public boolean valOtp(@RequestBody otpStoreDTO otpStoreDTO) {
        //emailService.sendEmail(otpStoreDTO.getEmail());
        //System.out.println(otpStoreDTO.getGeneratedOTP());
        return emailService.validateOTP(otpStoreDTO.getEmail(),otpStoreDTO.getGeneratedOTP());
    }
}