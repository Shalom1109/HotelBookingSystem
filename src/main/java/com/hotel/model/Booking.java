package com.hotel.model;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private int guestId;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    double totalAmount;

    public Booking() {}

    public Booking(int guestId, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double totalAmount) {
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
    }

    public Booking(int bookingId, int guestId, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double totalAmount) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    @Override
    public String toString() {
        return String.format("Booking #%-4d | Guest ID: %-3d | Room #%-4d | Check-In: %s | Check-Out: %s | Total: ₹%.2f",
                bookingId, guestId, roomNumber, checkInDate, checkOutDate, totalAmount);
    }
}