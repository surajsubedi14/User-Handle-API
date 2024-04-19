package org.example.userhandleapi.Controller;

import lombok.RequiredArgsConstructor;
import org.example.coreapi.Entities.Doctor;
import org.example.coreapi.Services.DoctorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping ("/user-handle/doctor")
public class DoctorController {
    @Autowired
    DoctorServices doctorServices;
    @Autowired
    private PasswordEncoder passwordencoder;
    @PutMapping("/update-details/")
    public ResponseEntity<String> updateUser(@RequestParam String id, @RequestBody Doctor doctor) {

        try {
            doctorServices.updateDoctorDetails(Long.valueOf(id), doctor);
            return ResponseEntity.ok("Doctor Details Updated successfully");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }

    }


}
