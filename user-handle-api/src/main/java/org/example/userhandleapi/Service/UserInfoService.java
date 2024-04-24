package org.example.userhandleapi.Service;

import org.example.coreapi.Entities.Hospital;
import org.example.coreapi.Entities.User;
import org.example.coreapi.Repositories.HospitalRepository;
import org.example.coreapi.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {
    @Autowired
    private UserRepository userInfoRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userInfo = Optional.ofNullable(userInfoRepository.findByEmails(username));
        if (userInfo.isPresent()) {

            return userInfo.map(UserInfoDetails::new).orElseThrow(()-> new UsernameNotFoundException("User not found"+username));
        }
        Optional<org.example.coreapi.Entities.Hospital> hosInfo = Optional.ofNullable(hospitalRepository.findByEmail(username));
        Hospital hospital = hosInfo.get();
        User user = new User();
        user.setEmail(hospital.getEmail());
        user.setPassword(hospital.getPassword());
        user.setRole(hospital.getRole());
        Optional<User> userInfo2 = Optional.of(user);

        return userInfo2.map(UserInfoDetails::new)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"+username));

    }





}