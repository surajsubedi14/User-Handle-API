package org.example.userhandleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"org.example.coreapi", "org.example.userhandleapi"})
public class UserHandleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserHandleApiApplication.class, args);
    }

}
