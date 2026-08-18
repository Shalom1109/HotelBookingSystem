package com.hotel.model;

public class Guest {
    private int guestId;
    private String fullName;
    private String email;
    private String phone;

    // Default Constructor
    public Guest() {}

    // Constructor without guestId (for adding new guests before SQL auto-generates ID)
    public Guest(String fullName, String email, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Constructor with guestId (for fetching existing guests from SQL)
    public Guest(int guestId, String fullName, String email, String phone) {
        this.guestId = guestId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("Guest ID: %-3d | Name: %-20s | Email: %-25s | Phone: %s",
                guestId, fullName, email, phone);
    }
}