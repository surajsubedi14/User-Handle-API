package org.example.userhandleapi.DTO;

import lombok.Data;

@Data
public class OTPVerificationRequest {
    private String verificationId;
    private String otp;
}