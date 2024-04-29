package org.example.userhandleapi.Controller;
import lombok.RequiredArgsConstructor;

import org.example.coreapi.Entities.*;
import org.example.coreapi.Repositories.DepartmentRepository;
import org.example.coreapi.Repositories.DoctorRepository;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Services.AdminService;
import org.example.coreapi.Services.DoctorServices;
import org.example.coreapi.Services.HospitalServices;
import org.example.coreapi.Services.UserServices;
import org.example.userhandleapi.DTO.*;
import org.example.userhandleapi.Helper.EmailHelper;
import org.example.userhandleapi.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/user-handle/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private  final EmailService emailService;
    private final DoctorServices doctorServices;
    private final HospitalServices hospitalServices;
    private final UserServices userServices;
    private final PasswordEncoder passwordencoder;



    @PostMapping("/addDoctor/")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponseDto> addDoctor(@RequestParam String id, @RequestBody Doctor doctor) {

        Optional<Hospital> isHospital = hospitalServices.getSpecificHospitalDetails(Integer.parseInt(id));
        User isDoctor = userServices.userExists(doctor.getPhoneNumber(),doctor.getEmail());
        if(isDoctor != null)
        {
            if(Objects.equals(isDoctor.getEmail(), doctor.getEmail()))
            {
                var authResponseDto = new AuthResponseDto("Email already exists", AuthStatus.EMAIL_ALREADY_EXISTS,"",-1L);
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(authResponseDto);
            }
            else{
                var authResponseDto = new AuthResponseDto("Phone Number already exists", AuthStatus.PHONE_NUMBER_ALREADY_EXISTS,"",-1L);
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(authResponseDto);
            }

        }
        if(isHospital.isPresent())
        {
            Hospital hospital = isHospital.get();
            doctor.setActive(true);
            Feedback feedback = new Feedback();
            doctor.setFeedback(feedback);
            doctor.setHospital(hospital);
            try {

                String genPassword = EmailHelper.generateOTP(6);
                doctor.setPassword(passwordencoder.encode(genPassword));
                Doctor doctors = adminService.addDoctor(doctor);
                emailService.sendPasswordToDoctor(doctor.getEmail(),genPassword);
                var authResponseDto = new AuthResponseDto("Doctor Added Successfully", AuthStatus.DOCTOR_ADDED_SUCCESSFULLY,doctor.getRole(),doctor.getUser_id());
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(authResponseDto);
            } catch (Exception e) {
                var authResponseDto = new AuthResponseDto("Something Went Wrong", AuthStatus.SOMETHING_WENT_WRONG,"",-1L);

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(authResponseDto);
            }
        }
        else {
            var authResponseDto = new AuthResponseDto("Hospital Doesn't Exists", AuthStatus.HOSPITAL_NOT_PRESENT,"",-1L);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }


    }

    @PutMapping("/updateHospitalDetails/")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponseDto> updateHospitalDetails(@RequestParam String id, @RequestBody HospitalDetaillsUpdateDTO hospitalDetaillsUpdateDTO) {

        try {
            Optional<Hospital> isHospital = hospitalServices.getSpecificHospitalDetails(Integer.parseInt(id));
            if(isHospital.isPresent())
            {
                Hospital hospital = isHospital.get();
                hospital.setName(hospitalDetaillsUpdateDTO.getHospital_name());
                hospital.setEmail(hospitalDetaillsUpdateDTO.getEmail());
                hospital.setPhoneNumber(hospitalDetaillsUpdateDTO.getPhoneNumber());
                hospital.setAddress(hospitalDetaillsUpdateDTO.getAddress());
                hospital.setWebsite(hospitalDetaillsUpdateDTO.getWebsite());
                hospital.setDepartment(hospitalDetaillsUpdateDTO.getDepartments());
                hospitalServices.addHospital(hospital);
                var authResponseDto = new AuthResponseDto("Hospital Details Updated Successfully", AuthStatus.SUCCESS,"",200L);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(authResponseDto);

            }
            else {
                var authResponseDto = new AuthResponseDto("Hospital Details Not Updated", AuthStatus.UNSUCCESSFUL,"",-1L);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(authResponseDto);

            }

        } catch (Exception e) {
            var authResponseDto = new AuthResponseDto("Hospital Details Not Updated" + " " + e, AuthStatus.UNSUCCESSFUL,"",-1L);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(authResponseDto);
        }


    }


    @PutMapping("/doctor-status-update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponseDto> updateDoctorStatus(@RequestBody String id) {
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

    @PutMapping("/promote-doctor")

    public ResponseEntity<AuthResponseDto> promoteDoctor(@RequestBody String id) {
        try {

            Doctor doctor = doctorServices.getDoctorByUserId(Long.valueOf(id));
            doctor.setSeniorityLevel("senior");
            adminService.addDoctor(doctor);

            emailService.sendPromotionMailToDoctor(doctor.getEmail(),doctor.getFirstName() +" "+ doctor.getLastName(),doctor.getHospital().getName());
            var authResponseDto = new AuthResponseDto("Doctor Promoted Successfully", AuthStatus.SUCCESS,"",0L);
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