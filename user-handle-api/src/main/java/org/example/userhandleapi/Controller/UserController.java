package org.example.userhandleapi.Controller;

import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Entities.User;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Repositories.UserRepository;
import org.example.userhandleapi.DTO.AuthRequest;
import org.example.userhandleapi.DTO.ResetPasswordRequestDTO;
import org.example.userhandleapi.DTO.UpdatePasswordDTO;
import org.example.userhandleapi.DTO.otpStoreDTO;
import org.example.userhandleapi.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordencoder;

    @Autowired
    private HospitalRepository hospitalRepository;

    @PostMapping("/reset-password-otp")
    public String resetPassword(@RequestBody ResetPasswordRequestDTO req) {
        //System.out.println(req.getEmail());
        try {

            User user = userRepository.findByEmails(req.getEmail());
            Hospital hospital = hospitalRepository.findByEmail(req.getEmail());
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

                User user = userRepository.findByEmails(req.getEmail());
                Hospital hospital = hospitalRepository.findByEmail(req.getEmail());
                if(user != null)
                {
                    user.setPassword(passwordencoder.encode(req.getPassword()));
                    userRepository.save(user);

                }
                else {
                    hospital.setPassword(passwordencoder.encode(req.getPassword()));
                    hospitalRepository.save(hospital);
                }
                return "Password Updated Sucessfully";


            } else {
                return "Invalid OTP";

            }


        } catch (Exception e) {
            return e.getMessage();

        }


    }

}
