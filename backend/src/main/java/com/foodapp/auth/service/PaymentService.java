package com.foodapp.auth.service;

import com.foodapp.auth.dto.AuthDTOs.*;
import com.foodapp.auth.entity.Payment;
import com.foodapp.auth.entity.User;
import com.foodapp.auth.repository.PaymentRepository;
import com.foodapp.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private UserRepository userRepository;

    // ---- Submit Payment ----
    public PaymentResponse submitPayment(String email, PaymentRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String receipt = generateReceiptNumber();

        Payment payment = Payment.builder()
                .user(user)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(Payment.PaymentStatus.PENDING)
                .orderReference(request.getOrderReference())
                .cardLastFour(request.getCardLastFour())
                .receiptNumber(receipt)
                .notes(request.getNotes())
                .build();

        // Auto-complete CASH payments; CARD/ONLINE stay PENDING until confirmed
        if (request.getMethod() == Payment.PaymentMethod.CASH) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
        }

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    // ---- Get My Payments ----
    public List<PaymentResponse> getMyPayments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ---- Get Single Payment ----
    public PaymentResponse getPayment(String email, Long paymentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        // Allow if owner or admin
        if (!payment.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }
        return toResponse(payment);
    }

    // ---- Cancel Payment (customer) ----
    public PaymentResponse cancelPayment(String email, Long paymentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        if (!payment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("Only PENDING payments can be cancelled");
        }
        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
        return toResponse(payment);
    }

    // ---- Admin: Get All Payments ----
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ---- Admin: Update Payment Status ----
    public PaymentResponse updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(request.getStatus());
        paymentRepository.save(payment);
        return toResponse(payment);
    }

    // ---- Admin: Delete Payment History ----
    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        paymentRepository.delete(payment);
    }

    // ---- Helpers ----
    private String generateReceiptNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "RCP-" + datePart + "-" + randomPart;
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .userName(p.getUser().getName())
                .userEmail(p.getUser().getEmail())
                .amount(p.getAmount())
                .method(p.getMethod().name())
                .status(p.getStatus().name())
                .orderReference(p.getOrderReference())
                .cardLastFour(p.getCardLastFour())
                .receiptNumber(p.getReceiptNumber())
                .notes(p.getNotes())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
