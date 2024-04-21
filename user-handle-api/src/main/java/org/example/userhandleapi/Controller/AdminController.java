package org.example.userhandleapi.Controller;
import lombok.RequiredArgsConstructor;

import org.example.coreapi.Entities.Department;
import org.example.coreapi.Entities.Doctor;
import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Repositories.DepartmentRepository;
import org.example.coreapi.Repositories.DoctorRepository;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Services.AdminService;
import org.example.coreapi.Services.DoctorServices;
import org.example.coreapi.Services.PatientService;
import org.example.userhandleapi.DTO.AuthResponseDto;
import org.example.userhandleapi.DTO.AuthStatus;
import org.example.userhandleapi.DTO.DepartmentDTO;
import org.example.userhandleapi.DTO.HospitalDetaillsUpdateDTO;
import org.example.userhandleapi.Helper.EmailHelper;
import org.example.userhandleapi.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private  final EmailService emailService;
    private final DoctorServices doctorServices;
    private final DoctorRepository doctorRepository;
    private final PatientService patientService;
    @Autowired
    private PasswordEncoder passwordencoder;

    private EmailHelper emailHelper;

    @PostMapping("/addDoctor/")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponseDto> addDoctor(@RequestParam String id, @RequestBody Doctor doctor) {
        Hospital hospital = hospitalRepository.getHospitalById(Long.valueOf(id));
        doctor.setActive(true);
        doctor.setHospital(hospital);
        try {

            String genPassword = EmailHelper.generateOTP(6);
            doctor.setPassword(passwordencoder.encode(genPassword));
            Doctor doctors = adminService.addDoctor(doctor);
            emailService.sendPasswordToDoctor(doctor.getEmail(),genPassword);
            var authResponseDto = new AuthResponseDto("", AuthStatus.USER_NOT_CREATED,"",0L);
            if (doctors != null) {
                authResponseDto = new AuthResponseDto("Doctor Added Successfully", AuthStatus.DOCTOR_ADDED_SUCCESSFULLY,doctor.getRole(),0L);

            } else {
                authResponseDto = new AuthResponseDto("Doctor Already Exists", AuthStatus.USER_NOT_CREATED,"",0L);
                System.out.println(authResponseDto.token());

            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        } catch (Exception e) {
            var authResponseDto = new AuthResponseDto(null, AuthStatus.USER_NOT_CREATED,"",0L);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }

    }

    @PutMapping("/updateHospitalDetails/")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponseDto> updateHospitalDetails(@RequestParam String id, @RequestBody HospitalDetaillsUpdateDTO hospitalDetaillsUpdateDTO) {

        try {
            Hospital hospital = hospitalRepository.getHospitalById(Long.parseLong(id)); //done
            hospital.setName(hospitalDetaillsUpdateDTO.getName());
            hospital.setEmail(hospitalDetaillsUpdateDTO.getEmail());
            hospital.setPhoneNumber(hospitalDetaillsUpdateDTO.getPhoneNumber());
            hospital.setAddress(hospitalDetaillsUpdateDTO.getAddress());
            hospital.setWebsite(hospitalDetaillsUpdateDTO.getWebsite());
            hospital.setDepartment(hospitalDetaillsUpdateDTO.getDepartments());
            hospitalRepository.save(hospital);

            var authResponseDto = new AuthResponseDto("Hospital Details Updated Successfully", AuthStatus.SUCCESS,"",0L);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        } catch (Exception e) {
            var authResponseDto = new AuthResponseDto("Hospital Details Not Updated" + " " + e, AuthStatus.UNSUCCESSFUL,"",0L);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }


    }


    @PutMapping("/doctor-status-update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponseDto> updateDoctorStatus(@RequestBody String id) {
        System.out.println(id);


        try {

          Doctor doctor = doctorServices.getDoctorByUserId(Long.valueOf(id));
          doctor.setActive(!(doctor.isActive()));
          adminService.addDoctor(doctor);
          String Status = "Deactivated";
          if(doctor.isActive())
          {
              Status = "activated";
          }
          emailService.sendStatusToDoctor(doctor.getEmail(),Status,doctor.getFirstName() +" "+ doctor.getLastName(),doctor.getHospital().getName());
            var authResponseDto = new AuthResponseDto("Doctor Status Updated Successfully", AuthStatus.SUCCESS,"",0L);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        } catch (Exception e) {
            var authResponseDto = new AuthResponseDto("Doctor Status Not Updated" + " " + e, AuthStatus.UNSUCCESSFUL,"",0L);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }


    }
}