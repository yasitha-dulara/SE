package com.foodapp.auth.controller;

import com.foodapp.auth.dto.AuthDTOs.*;
import com.foodapp.auth.service.AdminService;
import com.foodapp.auth.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private AdminService adminService;
    @Autowired private PaymentService paymentService;

    // ---- User Management ----

    // GET /api/admin/users
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserSummary>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // GET /api/admin/users/{id}
    @GetMapping("/users/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    // PUT /api/admin/users/{id}/role
    @PutMapping("/users/{id}/role")
    public ResponseEntity<AdminUserSummary> changeRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.changeUserRole(id, body.get("role")));
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        adminService.deleteUserById(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    // POST /api/admin/users/create-admin
    @PostMapping("/users/create-admin")
    public ResponseEntity<AdminUserSummary> createAdmin(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    // ---- Payment Management ----

    // GET /api/admin/payments
    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // PUT /api/admin/payments/{id}/status
    @PutMapping("/payments/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody UpdatePaymentStatusRequest request) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, request));
    }

    // DELETE /api/admin/payments/{id}
    @DeleteMapping("/payments/{id}")
    public ResponseEntity<MessageResponse> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok(new MessageResponse("Payment record deleted"));
    }
}
