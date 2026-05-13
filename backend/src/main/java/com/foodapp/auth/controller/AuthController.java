package com.foodapp.auth.controller;

import com.foodapp.auth.dto.AuthDTOs.*;
import com.foodapp.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // GET /api/auth/profile
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getProfile(userDetails.getUsername()));
    }

    // PUT /api/auth/profile
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateProfile(userDetails.getUsername(), request));
    }

    // PUT /api/auth/change-password
    @PutMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

    // POST /api/auth/addresses
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(authService.addAddress(userDetails.getUsername(), dto));
    }

    // DELETE /api/auth/addresses/{id}
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<MessageResponse> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        authService.deleteAddress(userDetails.getUsername(), id);
        return ResponseEntity.ok(new MessageResponse("Address deleted successfully"));
    }

    // DELETE /api/auth/account
    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        authService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.ok(new MessageResponse("Account deleted successfully"));
    }
}
