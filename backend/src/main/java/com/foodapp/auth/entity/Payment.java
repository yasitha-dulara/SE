package com.foodapp.auth.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method = PaymentMethod.CASH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "order_reference")
    private String orderReference;

    @Column(name = "card_last_four")
    private String cardLastFour;

    @Column(name = "receipt_number", unique = true)
    private String receiptNumber;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum PaymentMethod {
        CASH, CARD, ONLINE
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, CANCELLED, REFUNDED
    }

    public Payment() {}

    public Payment(Long id, User user, BigDecimal amount, PaymentMethod method, PaymentStatus status,
                   String orderReference, String cardLastFour, String receiptNumber, String notes,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.orderReference = orderReference;
        this.cardLastFour = cardLastFour;
        this.receiptNumber = receiptNumber;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public String getOrderReference() { return orderReference; }
    public String getCardLastFour() { return cardLastFour; }
    public String getReceiptNumber() { return receiptNumber; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }
    public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private BigDecimal amount;
        private PaymentMethod method = PaymentMethod.CASH;
        private PaymentStatus status = PaymentStatus.PENDING;
        private String orderReference;
        private String cardLastFour;
        private String receiptNumber;
        private String notes;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder method(PaymentMethod method) { this.method = method; return this; }
        public Builder status(PaymentStatus status) { this.status = status; return this; }
        public Builder orderReference(String orderReference) { this.orderReference = orderReference; return this; }
        public Builder cardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; return this; }
        public Builder receiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder createdAt(LocalDateTime t) { this.createdAt = t; return this; }
        public Builder updatedAt(LocalDateTime t) { this.updatedAt = t; return this; }

        public Payment build() {
            return new Payment(id, user, amount, method, status, orderReference,
                    cardLastFour, receiptNumber, notes, createdAt, updatedAt);
        }
    }
}
