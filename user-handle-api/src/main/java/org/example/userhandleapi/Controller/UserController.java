package org.example.userhandleapi.Controller;

import org.example.coreapi.Entities.Doctor;
import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Entities.Patient;
import org.example.coreapi.Entities.User;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Repositories.UserRepository;
import org.example.coreapi.Services.DoctorServices;
import org.example.coreapi.Services.HospitalServices;
import org.example.coreapi.Services.PatientService;
import org.example.coreapi.Services.UserServices;
import org.example.userhandleapi.DTO.*;
import org.example.userhandleapi.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping("/user-handle/auth")
public class UserController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordencoder;
    @Autowired
    private UserServices userServices;
    @Autowired
    private HospitalServices hospitalServices;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorServices doctorServices;

    @PostMapping("/reset-password-otp")
    public String resetPassword(@RequestBody ResetPasswordRequestDTO req) {

        try {

            User user = userServices.getUserByEmail(req.getEmail());
            Hospital hospital = hospitalServices.getHospitalByEmail(req.getEmail());
            if (user != null) {
                emailService.sendEmail(req.getEmail());
                return "OTP Sent Successfully";

            }
            else if(hospital != null){
                emailService.sendEmail(req.getEmail());
                return "OTP Sent Successfully";

            }
            return "User does not exists";

        } catch (Exception e) {
            return e.getMessage();

        }

    }

    @PostMapping("/reset-password")
    public String updatePassword(@RequestBody UpdatePasswordDTO req) {
        System.out.println(req.getOtp());
        try {
            if (emailService.validateOTP(req.getEmail(), req.getOtp())) {


                User user = userServices.getUserByEmail(req.getEmail());
                Hospital hospital = hospitalServices.getHospitalByEmail(req.getEmail());
                if(user != null)
                {
                    user.setPassword(passwordencoder.encode(req.getPassword()));

                    if(Objects.equals(user.getRole(), "PATIENT"))
                    {
                        Patient patient = (Patient) user;
                        patientService.updatePasswordPatient(patient);
                    }
                    else {
                        Doctor doctor = (Doctor) user;
                        doctorServices.updatePasswordDoctor(doctor);
                    }


                }
                else {
                    hospital.setPassword(passwordencoder.encode(req.getPassword()));

                    hospitalServices.addHospital(hospital);
                }
                return "Password Updated Sucessfully";


            } else {
                return "Invalid OTP";

            }


        } catch (Exception e) {
            return e.getMessage();

        }


    }

    @PostMapping("/contact-us")
    public String contact(@RequestBody ContactUsDTO contactUsDTO) {
        try {

            return emailService.sendContactUsMail(contactUsDTO);

        } catch (Exception e) {
            return e.getMessage();

        }


    }

}
