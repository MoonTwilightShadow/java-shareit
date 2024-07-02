package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findBookingsByBooker_IdOrderByStartDesc(Integer bookerId);

    List<Booking> findBookingsByBooker_IdAndEndAfterOrderByStartDesc(Integer bookerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByBooker_IdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByBooker_IdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByBooker_IdAndStatusOrderByStartDesc(Integer bookerId, Status status);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(Integer ownerId);

    List<Booking> findBookingsByItemOwnerIdAndEndAfterOrderByStartDesc(Integer ownerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, Status status);

    Optional<Booking> findFirstByStartLessThanEqualAndStatusEqualsAndItemIdOrderByEndDesc(LocalDateTime localDateTime, Status status, Integer itemId);

    Optional<Booking> findFirstByStartAfterAndStatusEqualsAndItemIdOrderByStart(LocalDateTime localDateTime, Status status, Integer itemId);

    Optional<Booking> findBookingByItemAndBooker(Item item, User booker);
}
