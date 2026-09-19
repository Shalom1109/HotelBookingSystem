package com.hotel.service;

import com.hotel.dto.BookingRequest;
import com.hotel.model.Booking;
import com.hotel.model.Guest;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          GuestRepository guestRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    @Transactional
    public Booking createBooking(BookingRequest request) {
        // 1. Date Validation
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        // 2. Room Verification
        Room room = roomRepository.findById(request.getRoomNumber())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.getRoomNumber()));

        if (!room.isAvailable()) {
            throw new IllegalStateException("Room " + request.getRoomNumber() + " is already booked.");
        }

        // 3. Find or Create Guest
        Guest guest = guestRepository.findByEmail(request.getGuestEmail())
                .orElseGet(() -> {
                    Guest newGuest = new Guest(request.getGuestName(), request.getGuestEmail(), request.getGuestPhone());
                    return guestRepository.save(newGuest);
                });

        // 4. Calculate Total Days & Price
        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double totalAmount = days * room.getPricePerNight();

        // 5. Update Room Availability
        room.setAvailable(false);
        roomRepository.save(room);

        // 6. Save and Return Booking
        Booking booking = new Booking(guest, room, request.getCheckInDate(), request.getCheckOutDate(), totalAmount);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}