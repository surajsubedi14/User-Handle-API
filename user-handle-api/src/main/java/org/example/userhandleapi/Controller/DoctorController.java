package org.example.userhandleapi.Controller;

import lombok.RequiredArgsConstructor;
import org.example.coreapi.Entities.Doctor;
import org.example.coreapi.Entities.User;
import org.example.coreapi.Services.AdminService;
import org.example.coreapi.Services.DoctorServices;
import org.example.coreapi.Services.UserServices;
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
    private AdminService adminService;

    @PutMapping("/update-details/")
    public ResponseEntity<String> updateUser(@RequestParam String id, @RequestBody Doctor doctor) {

        try {
            doctorServices.updateDoctorDetails(Long.valueOf(id), doctor);
            return ResponseEntity.ok("Doctor Details Updated successfully");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }

    }



    @PutMapping("/change-availability/")
    public String changeAvailabilityStatus(@RequestParam String id) {
        Doctor doctor = (Doctor) doctorServices.getDoctorByUserId(Long.valueOf(id));
        doctor.setAvailability(false);
        adminService.addDoctor(doctor);
        return "Status changed successfully";

    }


}
