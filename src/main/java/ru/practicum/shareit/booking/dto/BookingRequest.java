package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    @NotNull
    Integer itemId;
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @FutureOrPresent
    LocalDateTime end;
}
