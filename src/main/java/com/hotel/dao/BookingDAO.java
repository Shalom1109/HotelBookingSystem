package com.hotel.dao;

import com.hotel.config.DBConnection;
import com.hotel.model.Booking;
import com.hotel.model.BookingDetails;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Feature 1: Check if room is available for specific date range (Date Overlap Check)
    public boolean isRoomAvailableForDates(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM bookings " +
                "WHERE room_number = ? AND check_in_date < ? AND check_out_date > ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomNumber);
            pstmt.setDate(2, Date.valueOf(checkOut));
            pstmt.setDate(3, Date.valueOf(checkIn));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0; // True if no overlapping bookings
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking date availability: " + e.getMessage());
        }
        return false;
    }

    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (guest_id, room_number, check_in_date, check_out_date, total_amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, booking.getGuestId());
            pstmt.setInt(2, booking.getRoomNumber());
            pstmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            pstmt.setDouble(5, booking.getTotalAmount());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Booking(
                            rs.getInt("booking_id"),
                            rs.getInt("guest_id"),
                            rs.getInt("room_number"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate(),
                            rs.getDouble("total_amount")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking: " + e.getMessage());
        }
        return null;
    }

    // Feature 3: Get detailed booking info by ID for receipt generation
    public BookingDetails getBookingDetailsById(int bookingId) {
        String sql = "SELECT b.booking_id, g.full_name, g.email, r.room_number, r.room_type, " +
                "b.check_in_date, b.check_out_date, b.total_amount " +
                "FROM bookings b " +
                "INNER JOIN guests g ON b.guest_id = g.guest_id " +
                "INNER JOIN rooms r ON b.room_number = r.room_number " +
                "WHERE b.booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new BookingDetails(
                            rs.getInt("booking_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getInt("room_number"),
                            rs.getString("room_type"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate(),
                            rs.getDouble("total_amount")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking details: " + e.getMessage());
        }
        return null;
    }

    public boolean cancelBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            return false;
        }
    }

    public List<BookingDetails> getAllBookingsWithDetails() {
        List<BookingDetails> list = new ArrayList<>();
        String sql = "SELECT b.booking_id, g.full_name, g.email, r.room_number, r.room_type, " +
                "b.check_in_date, b.check_out_date, b.total_amount " +
                "FROM bookings b " +
                "INNER JOIN guests g ON b.guest_id = g.guest_id " +
                "INNER JOIN rooms r ON b.room_number = r.room_number " +
                "ORDER BY b.booking_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new BookingDetails(
                        rs.getInt("booking_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getDouble("total_amount")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking details: " + e.getMessage());
        }
        return list;
    }

    // Feature 2: Search past/active bookings by guest email
    public List<BookingDetails> getBookingsByGuestEmail(String email) {
        List<BookingDetails> list = new ArrayList<>();
        String sql = "SELECT b.booking_id, g.full_name, g.email, r.room_number, r.room_type, " +
                "b.check_in_date, b.check_out_date, b.total_amount " +
                "FROM bookings b " +
                "INNER JOIN guests g ON b.guest_id = g.guest_id " +
                "INNER JOIN rooms r ON b.room_number = r.room_number " +
                "WHERE LOWER(g.email) = LOWER(?) " +
                "ORDER BY b.booking_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookingDetails(
                            rs.getInt("booking_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getInt("room_number"),
                            rs.getString("room_type"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate(),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching bookings by email: " + e.getMessage());
        }
        return list;
    }

    // Feature 4: Calculate total hotel earnings
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bookings";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating revenue: " + e.getMessage());
        }
        return 0.0;
    }
}