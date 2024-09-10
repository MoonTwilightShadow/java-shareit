package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findBookingsByBooker_IdOrderByStartDesc(Integer bookerId, Pageable page);

    Page<Booking> findBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartAsc(Integer bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime localDateTime, Pageable page);

    Page<Booking> findBookingsByBooker_IdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime localDateTime, Pageable page);

    Page<Booking> findBookingsByBooker_IdAndStatusOrderByStartDesc(Integer bookerId, Status status, Pageable page);

    Page<Booking> findBookingsByItemOwnerIdOrderByStartDesc(Integer ownerId, Pageable page);

    Page<Booking> findBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Booking> findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime localDateTime, Pageable page);

    Page<Booking> findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime localDateTime, Pageable page);

    Page<Booking> findBookingsByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, Status status, Pageable page);

    Optional<Booking> findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(LocalDateTime localDateTime, Status status, Integer itemId);

    Optional<Booking> findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(LocalDateTime localDateTime, Status status, Integer itemId);

    @Query(value = "select * from bookings as b " +
            "left join items as i on b.item_id = i.id " +
            "where b.end_date <= now() and i.id = ? and b.booker_id = ? and b.status = 'APPROVED'" +
            "limit 1", nativeQuery = true)
    Optional<Booking> findCompletedBooking(Integer itemId, Integer bookerId);


}
