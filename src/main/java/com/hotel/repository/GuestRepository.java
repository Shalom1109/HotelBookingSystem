package com.hotel.repository;

import com.hotel.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
    // Find an existing guest by email to avoid duplicates
    Optional<Guest> findByEmail(String email);
}