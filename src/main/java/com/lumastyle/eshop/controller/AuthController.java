package com.lumastyle.eshop.controller;

import com.lumastyle.eshop.dto.auth.AuthRequest;
import com.lumastyle.eshop.dto.auth.AuthResponse;
import com.lumastyle.eshop.service.impl.AppUserDetailsService;
import com.lumastyle.eshop.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication operations")
public class AuthController {

    private final AuthenticationManager authManager;
    private final AppUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "Authenticate user and generate JWT",
            description = "Validates user credentials and returns a JSON Web Token for subsequent requests."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content)
    })
    @PostMapping("/login")
    public AuthResponse login(@Valid
                              @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                      description = "User credentials (email + password)",
                                      required = true,
                                      content = @Content(schema = @Schema(implementation = AuthRequest.class)))
                              @RequestBody AuthRequest request) {
        log.info("Received login request for user: {}", request.getEmail());
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        return new AuthResponse(request.getEmail(), jwtToken);
    }
}
