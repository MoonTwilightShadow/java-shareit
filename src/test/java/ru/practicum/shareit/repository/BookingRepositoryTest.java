package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void testFindCompletedBookingFalse() {
        assertTrue(bookingRepository.findCompletedBooking(1, 1).isEmpty());
    }

    @Test
    public void testFindCompletedBookingTrue() {
        User user1 = new User("name1", "email1@box.ru");
        User user2 = new User("name2", "email2@box.ru");
        int user1Id = userRepository.save(user1).getId();
        int user2Id = userRepository.save(user2).getId();

        Item item = new Item("itemName", "description", true);
        item.setOwner(user1);
        item.setRequest(null);
        int itemId = itemRepository.save(item).getId();

        Booking booking = new Booking(
                LocalDateTime.of(2024, 7, 7, 12, 0, 0, 0),
                LocalDateTime.of(2024, 7, 8, 12, 0, 0, 0)
        );
        booking.setItem(itemRepository.findById(itemId).get());
        booking.setBooker(userRepository.findById(user2Id).get());
        booking.setStatus(Status.APPROVED);
        int bookingId = bookingRepository.save(booking).getId();

        assertTrue(bookingRepository.findCompletedBooking(itemId, user2Id).isPresent());
    }
}
