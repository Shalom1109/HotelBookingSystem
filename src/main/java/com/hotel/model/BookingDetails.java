package com.hotel.model;

import java.time.LocalDate;

public class BookingDetails {
    private int bookingId;
    private String guestName;
    private String guestEmail;
    private int roomNumber;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalAmount;

    public BookingDetails(int bookingId, String guestName, String guestEmail, int roomNumber,
                          String roomType, LocalDate checkInDate, LocalDate checkOutDate, double totalAmount) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
    }

    public int getBookingId() { return bookingId; }
    public String getGuestName() { return guestName; }
    public String getGuestEmail() { return guestEmail; }
    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getTotalAmount() { return totalAmount; }

    @Override
    public String toString() {
        return String.format("Booking #%-4d | Guest: %-18s | Email: %-22s | Room #%-3d (%-8s) | %s to %s | Total: ₹%.2f",
                bookingId, guestName, guestEmail, roomNumber, roomType, checkInDate, checkOutDate, totalAmount);
    }
}