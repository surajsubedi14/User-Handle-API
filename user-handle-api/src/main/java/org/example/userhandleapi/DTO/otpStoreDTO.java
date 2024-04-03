package org.example.userhandleapi.DTO;

import lombok.Data;

@Data
public class otpStoreDTO {
    private String email;
    private String generatedOTP;
}