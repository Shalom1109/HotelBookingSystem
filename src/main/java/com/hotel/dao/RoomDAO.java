package com.hotel.dao;

import com.hotel.config.DBConnection;
import com.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rooms.add(new Room(
                        rs.getInt("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night"),
                        rs.getBoolean("is_available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available rooms: " + e.getMessage());
        }
        return rooms;
    }

    // Feature 2: Search rooms with filters (type & budget)
    public List<Room> filterRooms(String roomType, double maxPrice) {
        List<Room> rooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM rooms WHERE is_available = TRUE");

        if (roomType != null && !roomType.isBlank()) {
            sql.append(" AND LOWER(room_type) = LOWER(?)");
        }
        if (maxPrice > 0) {
            sql.append(" AND price_per_night <= ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (roomType != null && !roomType.isBlank()) {
                pstmt.setString(paramIndex++, roomType.trim());
            }
            if (maxPrice > 0) {
                pstmt.setDouble(paramIndex, maxPrice);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(new Room(
                            rs.getInt("room_number"),
                            rs.getString("room_type"),
                            rs.getDouble("price_per_night"),
                            rs.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error filtering rooms: " + e.getMessage());
        }
        return rooms;
    }

    public Room getRoomByNumber(int roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                            rs.getInt("room_number"),
                            rs.getString("room_type"),
                            rs.getDouble("price_per_night"),
                            rs.getBoolean("is_available")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching room details: " + e.getMessage());
        }
        return null;
    }

    public boolean updateRoomStatus(int roomNumber, boolean isAvailable) {
        String sql = "UPDATE rooms SET is_available = ? WHERE room_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, roomNumber);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room status: " + e.getMessage());
            return false;
        }
    }

    // Feature 4: Admin update room night price
    public boolean updateRoomPrice(int roomNumber, double newPrice) {
        String sql = "UPDATE rooms SET price_per_night = ? WHERE room_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, roomNumber);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room price: " + e.getMessage());
            return false;
        }
    }

    // Feature 4: Admin add new room
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, is_available) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setBoolean(4, room.isAvailable());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            return false;
        }
    }
}