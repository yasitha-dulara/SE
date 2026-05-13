package com.foodapp.auth.controller;

import com.foodapp.auth.dto.AuthDTOs.*;
import com.foodapp.auth.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // POST /api/payments — submit a new payment
    @PostMapping
    public ResponseEntity<PaymentResponse> submitPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.submitPayment(userDetails.getUsername(), request));
    }

    // GET /api/payments — get my payment history
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getMyPayments(userDetails.getUsername()));
    }

    // GET /api/payments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(userDetails.getUsername(), id));
    }

    // PUT /api/payments/{id}/cancel — cancel pending payment (customer)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.cancelPayment(userDetails.getUsername(), id));
    }
}
