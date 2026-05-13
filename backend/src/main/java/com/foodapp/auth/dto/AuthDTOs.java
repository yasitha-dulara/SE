package com.foodapp.auth.dto;

import com.foodapp.auth.entity.Payment;
import com.foodapp.auth.entity.User;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AuthDTOs {

    // ---- Register Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String phoneNumber;
    }

    // ---- Login Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    // ---- Auth Response (JWT) ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private String role;
    }

    // ---- User Profile Response ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileResponse {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
        private String role;
        private LocalDateTime createdAt;
        private List<AddressDTO> deliveryAddresses;
    }

    // ---- Update Profile Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String name;
        private String phoneNumber;
    }

    // ---- Change Password Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        private String newPassword;
    }

    // ---- Address DTO ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private Long id;
        @NotBlank(message = "Label is required")
        private String label;
        @NotBlank(message = "Street is required")
        private String street;
        private String city;
        private String postalCode;
        private boolean isDefault;
    }

    // ---- Message Response ----
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageResponse {
        private String message;
    }

    // ---- Payment Request ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;

        @NotNull(message = "Payment method is required")
        private Payment.PaymentMethod method;

        private String orderReference;
        private String cardLastFour;
        private String notes;
    }

    // ---- Payment Response ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private Long id;
        private Long userId;
        private String userName;
        private String userEmail;
        private BigDecimal amount;
        private String method;
        private String status;
        private String orderReference;
        private String cardLastFour;
        private String receiptNumber;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // ---- Update Payment Status Request (admin) ----
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePaymentStatusRequest {
        @NotNull(message = "Status is required")
        private Payment.PaymentStatus status;
    }

    // ---- Admin User Summary ----
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUserSummary {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
        private String role;
        private LocalDateTime createdAt;
        private int addressCount;
    }
}
