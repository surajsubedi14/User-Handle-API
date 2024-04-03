package org.example.userhandleapi.Controller;
import lombok.RequiredArgsConstructor;

import org.example.coreapi.Entities.Department;
import org.example.coreapi.Entities.Doctor;
import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Services.AdminService;
import org.example.userhandleapi.DTO.AuthResponseDto;
import org.example.userhandleapi.DTO.AuthStatus;
import org.example.userhandleapi.DTO.HospitalDetaillsUpdateDTO;
import org.example.userhandleapi.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final HospitalRepository hospitalRepository;
    private  final EmailService emailService;
    @Autowired
    private PasswordEncoder passwordencoder;

    @PostMapping("/addDoctor/")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponseDto> addDoctor(@RequestParam String id, @RequestBody Doctor doctor) {
        Hospital hospital = hospitalRepository.getHospitalById(Long.valueOf(id));
        //System.out.println(hospital.getRole());
        doctor.setHospital(hospital);
        try {

            String genPassword = EmailService.generateOTP(6);
            doctor.setPassword(passwordencoder.encode(genPassword));
//            doctor.setPassword(genPassword);




            Doctor doctors = adminService.addDoctor(doctor);

            emailService.sendPasswordToDoctor(doctor.getEmail(),genPassword);



            var authResponseDto = new AuthResponseDto("", AuthStatus.USER_NOT_CREATED,"");
            ;
            if (doctors != null) {
                authResponseDto = new AuthResponseDto("Doctor Added Successfully", AuthStatus.DOCTOR_ADDED_SUCCESSFULLY,doctor.getRole());

            } else {
                authResponseDto = new AuthResponseDto("Doctor Already Exists", AuthStatus.USER_NOT_CREATED,"");

            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        } catch (Exception e) {
            var authResponseDto = new AuthResponseDto(null, AuthStatus.USER_NOT_CREATED,"");

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }

    }

    @PostMapping("/updateHospitalDetails/")
    public ResponseEntity<AuthResponseDto> updateHospitalDetails(@RequestParam String id, @RequestBody HospitalDetaillsUpdateDTO hospitalDetaillsUpdateDTO) {

        try {

            Hospital hospital = hospitalRepository.getHospitalById(Long.valueOf(id));
            Set<Department> dept = hospital.getDepartment();
            for (Department department : hospitalDetaillsUpdateDTO.getDepartments()) {
                Department newDartment = new Department();
                newDartment.setDepartment_id(Long.valueOf(department.getDepartment_id()));
                newDartment.setDepartment_name(department.getDepartment_name());
                dept.add(newDartment);
            }
            hospital.setName(hospitalDetaillsUpdateDTO.getName());
            hospital.setEmail(hospitalDetaillsUpdateDTO.getEmail());
            hospital.setPhoneNumber(hospitalDetaillsUpdateDTO.getPhoneNumber());
            hospital.setAddress(hospitalDetaillsUpdateDTO.getAddress());
            hospital.setWebsite(hospitalDetaillsUpdateDTO.getWebsite());
            hospital.setDepartment(dept);
            hospitalRepository.save(hospital);

            var authResponseDto = new AuthResponseDto("Hospital Details Updated Successfully", AuthStatus.SUCCESS,"");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        } catch (Exception e) {
            var authResponseDto = new AuthResponseDto("Hospital Details Not Updated" + " " + e, AuthStatus.UNSUCCESSFUL,"");

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }


    }
}