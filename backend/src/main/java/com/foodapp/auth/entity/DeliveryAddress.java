package com.foodapp.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_addresses")
public class DeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String street;

    private String city;
    private String postalCode;
    private boolean isDefault = false;

    public DeliveryAddress() {}

    public DeliveryAddress(Long id, User user, String label, String street,
                           String city, String postalCode, boolean isDefault) {
        this.id = id;
        this.user = user;
        this.label = label;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getLabel() { return label; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }
    public boolean isDefault() { return isDefault; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setLabel(String label) { this.label = label; }
    public void setStreet(String street) { this.street = street; }
    public void setCity(String city) { this.city = city; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User user;
        private String label;
        private String street;
        private String city;
        private String postalCode;
        private boolean isDefault = false;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder label(String label) { this.label = label; return this; }
        public Builder street(String street) { this.street = street; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder isDefault(boolean isDefault) { this.isDefault = isDefault; return this; }

        public DeliveryAddress build() {
            return new DeliveryAddress(id, user, label, street, city, postalCode, isDefault);
        }
    }
}
