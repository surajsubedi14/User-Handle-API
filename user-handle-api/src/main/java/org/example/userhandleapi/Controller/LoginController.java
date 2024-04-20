package org.example.userhandleapi.Controller;

import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Entities.User;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Repositories.UserRepository;
import org.example.coreapi.Services.HospitalServices;
import org.example.coreapi.Services.UserServices;
import org.example.userhandleapi.DTO.AuthResponseDto;
import org.example.userhandleapi.DTO.AuthRequest;
import org.example.userhandleapi.DTO.AuthStatus;
import org.example.userhandleapi.DTO.OTPLoginCread;
import org.example.userhandleapi.Service.UserInfoService;
import org.example.userhandleapi.config.JWT.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@RestController
@RequestMapping("/user-handle/auth")
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserServices userServices;

    @Autowired
    private HospitalServices hospitalServices;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> addUser(@RequestBody AuthRequest authRequest){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        Hospital hospital = hospitalServices.getHospitalByEmail(authRequest.getEmail());
        User user = userServices.getUserByEmail(authRequest.getEmail());
        if(authenticate.isAuthenticated() && hospital != null && Objects.equals(hospital.getRole(), authRequest.getRole()))
        {
            var jwtToken = jwtService.generateToken(authRequest.getEmail());


            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS,hospital.getRole(),hospital.getHospital_id());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        }
        else if(authenticate.isAuthenticated() && user != null && (Objects.equals(user.getRole(), authRequest.getRole())) ) {
            var jwtToken = jwtService.generateToken(authRequest.getEmail());
            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS,user.getRole(),user.getUser_id());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        }
         var authResponseDto = new AuthResponseDto("", AuthStatus.LOGIN_FAILED,"User Doesn't Exists",null);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponseDto);

    }

    @PostMapping("/login-using-otp")
    public ResponseEntity<AuthResponseDto> loginUsingOtp(@RequestBody OTPLoginCread otpLoginCread){
        User user = userServices.getUserByMobileNumber(otpLoginCread.getMobileNumber());

        if(Objects.equals(user.getRole(), otpLoginCread.getRole())) {
            var jwtToken = jwtService.generateToken(user.getEmail());
            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS,user.getRole(),user.getUser_id());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        }
        var authResponseDto = new AuthResponseDto("", AuthStatus.LOGIN_FAILED,"User Doesn't Exists",null);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authResponseDto);



    }

    @PostMapping("/logout")
    public void logout() {

        SecurityContextHolder.clearContext();
    }


}
