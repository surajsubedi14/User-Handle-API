package org.example.userhandleapi.DTO;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String email;
    private String otp;
    private String password;
}
