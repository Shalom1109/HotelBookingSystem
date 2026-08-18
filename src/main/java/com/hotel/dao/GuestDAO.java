package com.hotel.dao;

import com.hotel.config.DBConnection;
import com.hotel.model.Guest;

import java.sql.*;

public class GuestDAO {

    // Add new guest and return auto-generated guest_id
    public int addGuest(Guest guest) {
        String sql = "INSERT INTO guests (full_name, email, phone) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, guest.getFullName());
            pstmt.setString(2, guest.getEmail());
            pstmt.setString(3, guest.getPhone());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Returns auto-generated guest_id
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding guest: " + e.getMessage());
        }
        return -1;
    }
}