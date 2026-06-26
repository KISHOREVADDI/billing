package com.clothshop.billing.controller;

import com.clothshop.billing.dto.AuthRequest;
import com.clothshop.billing.dto.AuthResponse;
import com.clothshop.billing.model.Role;
import com.clothshop.billing.model.User;
import com.clothshop.billing.repository.UserRepository;
import com.clothshop.billing.security.JwtUtil;
import com.clothshop.billing.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Incorrect username or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest authRequest) {
        try {
            if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username is already taken!");
            }

            User user = new User();
            user.setUsername(authRequest.getUsername());
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            user.setRoles(Set.of(Role.ROLE_ADMIN));
            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }
}
