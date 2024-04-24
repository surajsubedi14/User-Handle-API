package org.example.userhandleapi.DTO;

import lombok.Data;

@Data
public class ContactUsDTO {
    private String name;
    private String email;
    private String subject;
    private String message;
}
