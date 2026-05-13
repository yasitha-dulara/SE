package com.foodapp.auth.service;

import com.foodapp.auth.dto.AuthDTOs.*;
import com.foodapp.auth.entity.DeliveryAddress;
import com.foodapp.auth.entity.User;
import com.foodapp.auth.repository.DeliveryAddressRepository;
import com.foodapp.auth.repository.UserRepository;
import com.foodapp.auth.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private DeliveryAddressRepository addressRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private AuthenticationManager authenticationManager;

    // ---- Register ----
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.CUSTOMER)
                .build();

        userRepository.save(user);
        String token = jwtUtils.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ---- Login ----
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ---- Get Profile ----
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AddressDTO> addresses = addressRepository.findByUserId(user.getId())
                .stream()
                .map(this::toAddressDTO)
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .deliveryAddresses(addresses)
                .build();
    }

    // ---- Update Profile ----
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        userRepository.save(user);
        return getProfile(email);
    }

    // ---- Change Password ----
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ---- Add Address ----
    public AddressDTO addAddress(String email, AddressDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DeliveryAddress address = DeliveryAddress.builder()
                .user(user)
                .label(dto.getLabel())
                .street(dto.getStreet())
                .city(dto.getCity())
                .postalCode(dto.getPostalCode())
                .isDefault(dto.isDefault())
                .build();

        addressRepository.save(address);
        return toAddressDTO(address);
    }

    // ---- Delete Address ----
    public void deleteAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DeliveryAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this address");
        }
        addressRepository.delete(address);
    }

    // ---- Delete Account ----
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    // ---- Helper ----
    private AddressDTO toAddressDTO(DeliveryAddress a) {
        return AddressDTO.builder()
                .id(a.getId())
                .label(a.getLabel())
                .street(a.getStreet())
                .city(a.getCity())
                .postalCode(a.getPostalCode())
                .isDefault(a.isDefault())
                .build();
    }
}
