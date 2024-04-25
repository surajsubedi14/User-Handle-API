package org.example.userhandleapi.Controller;

import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Services.HospitalServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user-handle/hospital")
public class HospitalController {
    @Autowired
    public HospitalServices hospitalServices;
    @Autowired
    private PasswordEncoder passwordencoder;

    @PostMapping(value = "/add-hospital")
    public ResponseEntity<?> RegisterHospital(@RequestBody Hospital hospital) {

        try {
            if (hospitalServices.existHospital(hospital.getEmail()) != null) {
                return ResponseEntity.ok("Hospital already exists");
            }
            hospital.setPassword(passwordencoder.encode(hospital.getPassword()));
            hospitalServices.addHospital(hospital);


            return ResponseEntity.ok("Hospital registered successfully");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("There is a server error");
        }
    }

}
