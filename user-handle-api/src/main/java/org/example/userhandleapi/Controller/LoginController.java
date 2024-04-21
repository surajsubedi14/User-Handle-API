package org.example.userhandleapi.Controller;

import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Entities.User;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Repositories.UserRepository;
import org.example.userhandleapi.DTO.AuthResponseDto;
import org.example.userhandleapi.DTO.AuthRequest;
import org.example.userhandleapi.DTO.AuthStatus;
import org.example.userhandleapi.Service.UserInfoService;
import org.example.userhandleapi.config.JWT.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
@CrossOrigin
@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> addUser(@RequestBody AuthRequest authRequest){


        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            Hospital hospital = hospitalRepository.findByEmail(authRequest.getEmail());
            User user = userRepository.findByEmails(authRequest.getEmail());

            if (authenticate.isAuthenticated()) {
                if (hospital != null && Objects.equals(hospital.getRole(), authRequest.getRole())) {
                    var jwtToken = jwtService.generateToken(authRequest.getEmail());
                    var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS, hospital.getRole(), hospital.getHospital_id());
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(authResponseDto);
                } else if (user != null && Objects.equals(user.getRole(), authRequest.getRole())) {
                    var jwtToken = jwtService.generateToken(authRequest.getEmail());
                    var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS, user.getRole(), user.getUser_id());
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(authResponseDto);
                } else {
                    var authResponseDto = new AuthResponseDto("", AuthStatus.LOGIN_FAILED, "User Doesn't Exists", null);
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(authResponseDto);
                }
            } else {
                var authResponseDto = new AuthResponseDto("", AuthStatus.LOGIN_FAILED, "Authentication Failed", null);
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(authResponseDto);
            }
        } catch (AuthenticationException e) {
            var authResponseDto = new AuthResponseDto("", AuthStatus.LOGIN_FAILED, "Authentication Exception: " + e.getMessage(), null);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(authResponseDto);
        } catch (Exception e) {
            var authResponseDto = new AuthResponseDto("", AuthStatus.LOGIN_FAILED, "Exception: " + e.getMessage(), null);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(authResponseDto);
        }
}

        @PostMapping("/logout")
    public void logout() {
        // Invalidate session
        SecurityContextHolder.clearContext();
    }


}
