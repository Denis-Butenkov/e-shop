package com.lumastyle.delivery.controller;

import com.lumastyle.delivery.dto.UserRequest;
import com.lumastyle.delivery.dto.UserResponse;
import com.lumastyle.delivery.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody UserRequest request) {
        log.info("Received request to register a new user: {}", request.getEmail());
        return service.registerUser(request);
    }
}
