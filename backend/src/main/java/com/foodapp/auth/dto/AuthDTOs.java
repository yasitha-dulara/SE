package com.foodapp.auth.dto;

import com.foodapp.auth.entity.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AuthDTOs {

    // ---- Register Request ----
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

        public RegisterRequest() {}
        public RegisterRequest(String name, String email, String password, String phoneNumber) {
            this.name = name; this.email = email; this.password = password; this.phoneNumber = phoneNumber;
        }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    // ---- Login Request ----
    public static class LoginRequest {
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;
        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}
        public LoginRequest(String email, String password) { this.email = email; this.password = password; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
    }

    // ---- Auth Response (JWT) ----
    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private String role;

        public AuthResponse() {}
        public AuthResponse(String token, String type, Long id, String name, String email, String role) {
            this.token = token; this.type = type; this.id = id;
            this.name = name; this.email = email; this.role = role;
        }
        public String getToken() { return token; }
        public String getType() { return type; }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public void setToken(String token) { this.token = token; }
        public void setType(String type) { this.type = type; }
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setRole(String role) { this.role = role; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String token; private String type = "Bearer"; private Long id;
            private String name; private String email; private String role;
            public Builder token(String token) { this.token = token; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder id(Long id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder email(String email) { this.email = email; return this; }
            public Builder role(String role) { this.role = role; return this; }
            public AuthResponse build() { return new AuthResponse(token, type, id, name, email, role); }
        }
    }

    // ---- User Profile Response ----
    public static class UserProfileResponse {
        private Long id; private String name; private String email;
        private String phoneNumber; private String role;
        private LocalDateTime createdAt; private List<AddressDTO> deliveryAddresses;

        public UserProfileResponse() {}
        public UserProfileResponse(Long id, String name, String email, String phoneNumber, String role,
                                   LocalDateTime createdAt, List<AddressDTO> deliveryAddresses) {
            this.id = id; this.name = name; this.email = email; this.phoneNumber = phoneNumber;
            this.role = role; this.createdAt = createdAt; this.deliveryAddresses = deliveryAddresses;
        }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getRole() { return role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public List<AddressDTO> getDeliveryAddresses() { return deliveryAddresses; }
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public void setRole(String role) { this.role = role; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public void setDeliveryAddresses(List<AddressDTO> deliveryAddresses) { this.deliveryAddresses = deliveryAddresses; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long id; private String name; private String email;
            private String phoneNumber; private String role;
            private LocalDateTime createdAt; private List<AddressDTO> deliveryAddresses;
            public Builder id(Long id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder email(String email) { this.email = email; return this; }
            public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
            public Builder role(String role) { this.role = role; return this; }
            public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
            public Builder deliveryAddresses(List<AddressDTO> deliveryAddresses) { this.deliveryAddresses = deliveryAddresses; return this; }
            public UserProfileResponse build() {
                return new UserProfileResponse(id, name, email, phoneNumber, role, createdAt, deliveryAddresses);
            }
        }
    }

    // ---- Update Profile Request ----
    public static class UpdateProfileRequest {
        private String name; private String phoneNumber;
        public UpdateProfileRequest() {}
        public UpdateProfileRequest(String name, String phoneNumber) { this.name = name; this.phoneNumber = phoneNumber; }
        public String getName() { return name; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setName(String name) { this.name = name; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    // ---- Change Password Request ----
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;
        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters")
        private String newPassword;

        public ChangePasswordRequest() {}
        public ChangePasswordRequest(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword; this.newPassword = newPassword;
        }
        public String getCurrentPassword() { return currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    // ---- Address DTO ----
    public static class AddressDTO {
        private Long id;
        @NotBlank(message = "Label is required") private String label;
        @NotBlank(message = "Street is required") private String street;
        private String city; private String postalCode; private boolean isDefault;

        public AddressDTO() {}
        public AddressDTO(Long id, String label, String street, String city, String postalCode, boolean isDefault) {
            this.id = id; this.label = label; this.street = street;
            this.city = city; this.postalCode = postalCode; this.isDefault = isDefault;
        }
        public Long getId() { return id; }
        public String getLabel() { return label; }
        public String getStreet() { return street; }
        public String getCity() { return city; }
        public String getPostalCode() { return postalCode; }
        public boolean isDefault() { return isDefault; }
        public void setId(Long id) { this.id = id; }
        public void setLabel(String label) { this.label = label; }
        public void setStreet(String street) { this.street = street; }
        public void setCity(String city) { this.city = city; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long id; private String label; private String street;
            private String city; private String postalCode; private boolean isDefault;
            public Builder id(Long id) { this.id = id; return this; }
            public Builder label(String label) { this.label = label; return this; }
            public Builder street(String street) { this.street = street; return this; }
            public Builder city(String city) { this.city = city; return this; }
            public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
            public Builder isDefault(boolean isDefault) { this.isDefault = isDefault; return this; }
            public AddressDTO build() { return new AddressDTO(id, label, street, city, postalCode, isDefault); }
        }
    }

    // ---- Message Response ----
    public static class MessageResponse {
        private String message;
        public MessageResponse() {}
        public MessageResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // ---- Payment Request ----
    public static class PaymentRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;
        @NotNull(message = "Payment method is required")
        private Payment.PaymentMethod method;
        private String orderReference; private String cardLastFour; private String notes;

        public PaymentRequest() {}
        public PaymentRequest(BigDecimal amount, Payment.PaymentMethod method,
                              String orderReference, String cardLastFour, String notes) {
            this.amount = amount; this.method = method; this.orderReference = orderReference;
            this.cardLastFour = cardLastFour; this.notes = notes;
        }
        public BigDecimal getAmount() { return amount; }
        public Payment.PaymentMethod getMethod() { return method; }
        public String getOrderReference() { return orderReference; }
        public String getCardLastFour() { return cardLastFour; }
        public String getNotes() { return notes; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public void setMethod(Payment.PaymentMethod method) { this.method = method; }
        public void setOrderReference(String orderReference) { this.orderReference = orderReference; }
        public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    // ---- Payment Response ----
    public static class PaymentResponse {
        private Long id; private Long userId; private String userName; private String userEmail;
        private BigDecimal amount; private String method; private String status;
        private String orderReference; private String cardLastFour; private String receiptNumber;
        private String notes; private LocalDateTime createdAt; private LocalDateTime updatedAt;

        public PaymentResponse() {}
        public PaymentResponse(Long id, Long userId, String userName, String userEmail,
                               BigDecimal amount, String method, String status,
                               String orderReference, String cardLastFour, String receiptNumber,
                               String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id; this.userId = userId; this.userName = userName; this.userEmail = userEmail;
            this.amount = amount; this.method = method; this.status = status;
            this.orderReference = orderReference; this.cardLastFour = cardLastFour;
            this.receiptNumber = receiptNumber; this.notes = notes;
            this.createdAt = createdAt; this.updatedAt = updatedAt;
        }
        public Long getId() { return id; }
        public Long getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserEmail() { return userEmail; }
        public BigDecimal getAmount() { return amount; }
        public String getMethod() { return method; }
        public String getStatus() { return status; }
        public String getOrderReference() { return orderReference; }
        public String getCardLastFour() { return cardLastFour; }
        public String getReceiptNumber() { return receiptNumber; }
        public String getNotes() { return notes; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setId(Long id) { this.id = id; }
        public void setUserId(Long userId) { this.userId = userId; }
        public void setUserName(String userName) { this.userName = userName; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public void setMethod(String method) { this.method = method; }
        public void setStatus(String status) { this.status = status; }
        public void setOrderReference(String orderReference) { this.orderReference = orderReference; }
        public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
        public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
        public void setNotes(String notes) { this.notes = notes; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long id; private Long userId; private String userName; private String userEmail;
            private BigDecimal amount; private String method; private String status;
            private String orderReference; private String cardLastFour; private String receiptNumber;
            private String notes; private LocalDateTime createdAt; private LocalDateTime updatedAt;
            public Builder id(Long id) { this.id = id; return this; }
            public Builder userId(Long userId) { this.userId = userId; return this; }
            public Builder userName(String userName) { this.userName = userName; return this; }
            public Builder userEmail(String userEmail) { this.userEmail = userEmail; return this; }
            public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
            public Builder method(String method) { this.method = method; return this; }
            public Builder status(String status) { this.status = status; return this; }
            public Builder orderReference(String orderReference) { this.orderReference = orderReference; return this; }
            public Builder cardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; return this; }
            public Builder receiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; return this; }
            public Builder notes(String notes) { this.notes = notes; return this; }
            public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
            public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
            public PaymentResponse build() {
                return new PaymentResponse(id, userId, userName, userEmail, amount, method, status,
                        orderReference, cardLastFour, receiptNumber, notes, createdAt, updatedAt);
            }
        }
    }

    // ---- Update Payment Status Request ----
    public static class UpdatePaymentStatusRequest {
        @NotNull(message = "Status is required")
        private Payment.PaymentStatus status;
        public UpdatePaymentStatusRequest() {}
        public UpdatePaymentStatusRequest(Payment.PaymentStatus status) { this.status = status; }
        public Payment.PaymentStatus getStatus() { return status; }
        public void setStatus(Payment.PaymentStatus status) { this.status = status; }
    }

    // ---- Admin User Summary ----
    public static class AdminUserSummary {
        private Long id; private String name; private String email;
        private String phoneNumber; private String role;
        private LocalDateTime createdAt; private int addressCount;

        public AdminUserSummary() {}
        public AdminUserSummary(Long id, String name, String email, String phoneNumber,
                                String role, LocalDateTime createdAt, int addressCount) {
            this.id = id; this.name = name; this.email = email; this.phoneNumber = phoneNumber;
            this.role = role; this.createdAt = createdAt; this.addressCount = addressCount;
        }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getRole() { return role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public int getAddressCount() { return addressCount; }
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setEmail(String email) { this.email = email; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public void setRole(String role) { this.role = role; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public void setAddressCount(int addressCount) { this.addressCount = addressCount; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long id; private String name; private String email;
            private String phoneNumber; private String role;
            private LocalDateTime createdAt; private int addressCount;
            public Builder id(Long id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder email(String email) { this.email = email; return this; }
            public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
            public Builder role(String role) { this.role = role; return this; }
            public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
            public Builder addressCount(int addressCount) { this.addressCount = addressCount; return this; }
            public AdminUserSummary build() {
                return new AdminUserSummary(id, name, email, phoneNumber, role, createdAt, addressCount);
            }
        }
    }
}
