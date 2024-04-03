package org.example.userhandleapi.Controller;

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
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        var jwtToken = jwtService.generateToken(authRequest.getEmail());
        System.out.println(jwtToken);
        if(Objects.equals(authRequest.getRole(), "ADMIN"))
        {
            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS,"ADMIN");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        }
        else {
            var authResponseDto = new AuthResponseDto(jwtToken, AuthStatus.LOGIN_SUCCESS,"PATIENT");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponseDto);
        }



    }
}
