package org.example.userhandleapi.DTO;

import lombok.Data;
import org.example.coreapi.Entities.Department;

import java.util.List;
import java.util.Set;

@Data
public class HospitalDetaillsUpdateDTO {
    private String hospital_name;
    private String email;
    private String phoneNumber;
    private String address;
    private String website;
    private List<Department> departments;



}