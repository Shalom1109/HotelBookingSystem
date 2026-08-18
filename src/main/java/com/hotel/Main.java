package com.hotel;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.model.Booking;
import com.hotel.model.BookingDetails;
import com.hotel.model.Guest;
import com.hotel.model.Room;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final RoomDAO roomDAO = new RoomDAO();
    private static final GuestDAO guestDAO = new GuestDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String ADMIN_PASSWORD = "admin";

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("    WELCOME TO HOTEL BOOKING SYSTEM     ");
        System.out.println("=========================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Search & Filter Rooms (Type / Budget)");
            System.out.println("3. Book a Room");
            System.out.println("4. Search Guest Booking History");
            System.out.println("5. View All Active Bookings");
            System.out.println("6. Checkout & Print Tax Invoice / Cancel Booking");
            System.out.println("7. Admin Panel (Manage Rooms & Revenue)");
            System.out.println("8. Exit");
            System.out.print("Choose an option (1-8): ");

            int choice = readIntInput();

            switch (choice) {
                case 1:
                    viewAvailableRooms();
                    break;
                case 2:
                    filterRoomsMenu();
                    break;
                case 3:
                    bookRoom();
                    break;
                case 4:
                    searchGuestHistory();
                    break;
                case 5:
                    viewAllBookings();
                    break;
                case 6:
                    checkoutOrCancelBooking();
                    break;
                case 7:
                    adminPanelMenu();
                    break;
                case 8:
                    System.out.println("Thank you for using Hotel Booking System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("❌ Invalid choice. Please enter a number between 1 and 8.");
            }
        }
    }

    private static void viewAvailableRooms() {
        System.out.println("\n--- AVAILABLE ROOMS ---");
        List<Room> availableRooms = roomDAO.getAvailableRooms();

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available at the moment.");
        } else {
            for (Room room : availableRooms) {
                System.out.println(room);
            }
        }
    }

    // Feature 2: Filter rooms by type and max budget
    private static void filterRoomsMenu() {
        System.out.println("\n--- SEARCH & FILTER ROOMS ---");
        System.out.print("Enter Room Type (Single/Double/Suite or press Enter to skip): ");
        String roomType = scanner.nextLine().trim();

        System.out.print("Enter Maximum Price per night (or 0 to skip): ");
        double maxPrice = readDoubleInput();

        List<Room> filtered = roomDAO.filterRooms(roomType, maxPrice);
        if (filtered.isEmpty()) {
            System.out.println("❌ No rooms match your search criteria.");
        } else {
            System.out.println("\n--- FILTERED RESULTS ---");
            for (Room r : filtered) {
                System.out.println(r);
            }
        }
    }

    // Feature 1: Book room with Date Overlap Check
    private static void bookRoom() {
        System.out.println("\n--- ROOM BOOKING PROCESS ---");

        List<Room> rooms = roomDAO.getAvailableRooms();
        if (rooms.isEmpty()) {
            System.out.println("Sorry, no rooms are available right now.");
            return;
        }

        System.out.println("Available Rooms:");
        for (Room room : rooms) {
            System.out.println(room);
        }

        System.out.print("\nEnter Room Number to book: ");
        int roomNumber = readIntInput();

        Room selectedRoom = rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .findFirst()
                .orElse(null);

        if (selectedRoom == null) {
            System.out.println("❌ Invalid room number or room is not available.");
            return;
        }

        LocalDate checkIn = readDateInput("Enter Check-In Date (YYYY-MM-DD): ");
        if (checkIn == null) return;

        LocalDate checkOut = readDateInput("Enter Check-Out Date (YYYY-MM-DD): ");
        if (checkOut == null) return;

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            System.out.println("❌ Check-Out date must be after Check-In date.");
            return;
        }

        // Feature 1 Check: Verify if dates overlap with existing bookings
        boolean isAvailableForDates = bookingDAO.isRoomAvailableForDates(roomNumber, checkIn, checkOut);
        if (!isAvailableForDates) {
            System.out.println("❌ Room #" + roomNumber + " is already booked for the selected date range!");
            return;
        }

        System.out.print("Enter Guest Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Guest Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Guest Phone Number: ");
        String phone = scanner.nextLine();

        Guest guest = new Guest(name, email, phone);
        double totalAmount = nights * selectedRoom.getPricePerNight();

        int guestId = guestDAO.addGuest(guest);
        if (guestId != -1) {
            Booking booking = new Booking(guestId, roomNumber, checkIn, checkOut, totalAmount);
            boolean isBooked = bookingDAO.createBooking(booking);

            if (isBooked) {
                roomDAO.updateRoomStatus(roomNumber, false);
                System.out.println("\n🎉 BOOKING SUCCESSFUL!");
                System.out.printf("Total Nights: %d | Total Amount (Excl. Tax): ₹%.2f\n", nights, totalAmount);
            } else {
                System.out.println("❌ Booking failed. Please try again.");
            }
        } else {
            System.out.println("❌ Guest registration failed.");
        }
    }

    // Feature 2: Search guest history
    private static void searchGuestHistory() {
        System.out.println("\n--- SEARCH GUEST HISTORY ---");
        System.out.print("Enter Guest Email: ");
        String email = scanner.nextLine().trim();

        List<BookingDetails> history = bookingDAO.getBookingsByGuestEmail(email);
        if (history.isEmpty()) {
            System.out.println("No booking history found for: " + email);
        } else {
            System.out.println("\n--- BOOKING HISTORY FOR " + email.toUpperCase() + " ---");
            for (BookingDetails bd : history) {
                System.out.println(bd);
            }
        }
    }

    private static void viewAllBookings() {
        System.out.println("\n--- ACTIVE BOOKINGS ---");
        List<BookingDetails> list = bookingDAO.getAllBookingsWithDetails();

        if (list.isEmpty()) {
            System.out.println("No active bookings found.");
        } else {
            for (BookingDetails details : list) {
                System.out.println(details);
            }
        }
    }

    // Feature 3: Checkout, Tax Calculation (18% GST), and Invoice Export
    private static void checkoutOrCancelBooking() {
        System.out.println("\n--- CHECKOUT / CANCEL BOOKING ---");
        System.out.print("Enter Booking ID: ");
        int bookingId = readIntInput();

        BookingDetails details = bookingDAO.getBookingDetailsById(bookingId);
        if (details == null) {
            System.out.println("❌ Booking ID not found.");
            return;
        }

        int roomNumber = details.getRoomNumber();
        boolean isCancelled = bookingDAO.cancelBooking(bookingId);

        if (isCancelled) {
            roomDAO.updateRoomStatus(roomNumber, true);
            System.out.println("✅ Checkout/Cancellation completed successfully!");
            System.out.println("Room #" + roomNumber + " is now available for new bookings.");

            System.out.print("Would you like to print/export the invoice receipt? (y/n): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (ans.equals("y") || ans.equals("yes")) {
                printAndExportReceipt(details);
            }
        } else {
            System.out.println("❌ Failed to process checkout. Please try again.");
        }
    }

    // Feature 3: Invoice generator
    private static void printAndExportReceipt(BookingDetails details) {
        double baseAmount = details.getTotalAmount();
        double gstTax = baseAmount * 0.18; // 18% GST
        double grandTotal = baseAmount + gstTax;

        String invoiceText = String.format("""
            ====================================================
                            HOTEL TAX INVOICE                   
            ====================================================
            Booking ID    : #%d
            Guest Name    : %s
            Guest Email   : %s
            Room Number   : #%d (%s)
            Check-In Date : %s
            Check-Out Date: %s
            ----------------------------------------------------
            Room Charge   : ₹%.2f
            GST (18%%)     : ₹%.2f
            ----------------------------------------------------
            GRAND TOTAL   : ₹%.2f
            ====================================================
            Thank you for staying with us!
            """,
                details.getBookingId(), details.getGuestName(), details.getGuestEmail(),
                details.getRoomNumber(), details.getRoomType(), details.getCheckInDate(),
                details.getCheckOutDate(), baseAmount, gstTax, grandTotal);

        System.out.println("\n" + invoiceText);

        // Export to TXT file
        String fileName = "Invoice_Booking_" + details.getBookingId() + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.print(invoiceText);
            System.out.println("📄 Invoice exported successfully to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to write invoice file: " + e.getMessage());
        }
    }

    // Feature 4: Admin Management Panel
    private static void adminPanelMenu() {
        System.out.println("\n--- ADMIN ACCESS ---");
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine().trim();

        if (!ADMIN_PASSWORD.equals(password)) {
            System.out.println("❌ Incorrect Password! Access Denied.");
            return;
        }

        while (true) {
            System.out.println("\n=== ADMIN PANEL ===");
            System.out.println("1. Add New Room");
            System.out.println("2. Update Room Price");
            System.out.println("3. View Total Revenue");
            System.out.println("4. Return to Main Menu");
            System.out.print("Choose option (1-4): ");

            int adminChoice = readIntInput();
            switch (adminChoice) {
                case 1:
                    System.out.print("Enter New Room Number: ");
                    int roomNum = readIntInput();
                    System.out.print("Enter Room Type (Single/Double/Suite): ");
                    String type = scanner.nextLine().trim();
                    System.out.print("Enter Price Per Night: ");
                    double price = readDoubleInput();

                    boolean added = roomDAO.addRoom(new Room(roomNum, type, price, true));
                    if (added) {
                        System.out.println("✅ Room #" + roomNum + " added successfully!");
                    } else {
                        System.out.println("❌ Failed to add room.");
                    }
                    break;
                case 2:
                    System.out.print("Enter Room Number to update: ");
                    int updateNum = readIntInput();
                    System.out.print("Enter New Price Per Night: ");
                    double newPrice = readDoubleInput();

                    boolean updated = roomDAO.updateRoomPrice(updateNum, newPrice);
                    if (updated) {
                        System.out.println("✅ Price updated successfully for Room #" + updateNum);
                    } else {
                        System.out.println("❌ Failed to update price.");
                    }
                    break;
                case 3:
                    double totalRev = bookingDAO.getTotalRevenue();
                    System.out.printf("\n💰 TOTAL HOTEL REVENUE: ₹%.2f\n", totalRev);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }

    private static int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input! Please enter a valid number: ");
            }
        }
    }

    private static double readDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input! Please enter a valid number: ");
            }
        }
    }

    private static LocalDate readDateInput(String prompt) {
        System.out.print(prompt);
        try {
            return LocalDate.parse(scanner.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format! Please use YYYY-MM-DD format (e.g., 2026-08-20).");
            return null;
        }
    }
}