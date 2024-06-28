package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_date")
    LocalDate start;
    @Column(name = "end_date")
    LocalDate end;
    @Column(name = "item_id")
    Integer item;
    @Column(name = "booker_id")
    Integer booker;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
