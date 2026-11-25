package com.mscv.customer.domain.models;

public class Address {
    private Long id;
    private String state;
    private String city;
    private String fullAddress;

    public Address() {
    }

    public Address(Long id, String state, String city, String fullAddress) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.fullAddress = fullAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
