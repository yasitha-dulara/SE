package com.foodapp.auth.service;

import com.foodapp.auth.dto.AuthDTOs.*;
import com.foodapp.auth.entity.User;
import com.foodapp.auth.repository.DeliveryAddressRepository;
import com.foodapp.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired private UserRepository userRepository;
    @Autowired private DeliveryAddressRepository addressRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // ---- List All Users ----
    public List<AdminUserSummary> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // ---- Get Single User ----
    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<AddressDTO> addresses = addressRepository.findByUserId(user.getId())
                .stream()
                .map(a -> AddressDTO.builder()
                        .id(a.getId()).label(a.getLabel()).street(a.getStreet())
                        .city(a.getCity()).postalCode(a.getPostalCode()).isDefault(a.isDefault())
                        .build())
                .collect(Collectors.toList());
        return UserProfileResponse.builder()
                .id(user.getId()).name(user.getName()).email(user.getEmail())
                .phoneNumber(user.getPhoneNumber()).role(user.getRole().name())
                .createdAt(user.getCreatedAt()).deliveryAddresses(addresses)
                .build();
    }

    // ---- Change User Role ----
    public AdminUserSummary changeUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Use CUSTOMER or ADMIN");
        }
        userRepository.save(user);
        return toSummary(user);
    }

    // ---- Admin Delete Account ----
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // ---- Create Admin Account ----
    public AdminUserSummary createAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }
        User admin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(admin);
        return toSummary(admin);
    }

    private AdminUserSummary toSummary(User user) {
        int addrCount = addressRepository.findByUserId(user.getId()).size();
        return AdminUserSummary.builder()
                .id(user.getId()).name(user.getName()).email(user.getEmail())
                .phoneNumber(user.getPhoneNumber()).role(user.getRole().name())
                .createdAt(user.getCreatedAt()).addressCount(addrCount)
                .build();
    }
}
