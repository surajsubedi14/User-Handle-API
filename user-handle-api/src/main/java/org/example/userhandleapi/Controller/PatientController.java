package org.example.userhandleapi.Controller;

import lombok.RequiredArgsConstructor;
import org.example.coreapi.Entities.Patient;
import org.example.coreapi.Repositories.PatientRepository;
import org.example.coreapi.Services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping ("/patient")
public class PatientController {

    @Autowired
    private  PatientService patientService;

    @Autowired
    private PasswordEncoder passwordencoder;
    @PostMapping("/registerPatient")
    public ResponseEntity<String> registerPatient(@RequestBody Patient patient){
        patient.setPassword(passwordencoder.encode(patient.getPassword()));
        try {
            if (patientService.existPatient(patient.getEmail()) != null) {
                return ResponseEntity.ok("Patient already exists");
            }
            // Save the new user
            patientService.registerPatient(patient);


            return ResponseEntity.ok("Patient registered successfully");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }



    }


    @PostMapping("/update-details/")
    public ResponseEntity<String> updateUser(@RequestParam String id, @RequestBody Patient patient) {

        try {
            // update patient details
            patientService.updateUserDetails(Long.valueOf(id), patient);
            return ResponseEntity.ok("Patient Details Updated successfully");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }

    }
}
