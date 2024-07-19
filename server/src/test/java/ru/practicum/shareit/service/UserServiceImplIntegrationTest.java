package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void testCreateAndGetById() {
        UserRequest ur = new UserRequest("name", "email@box.ru");
        int id = userService.create(ur).getId();

        User saveUser = userService.getById(id);
        assertThat(saveUser.getId(), notNullValue());
        assertThat(saveUser.getName(), equalTo(ur.getName()));
        assertThat(saveUser.getEmail(), equalTo(ur.getEmail()));
    }

    @Test
    @DirtiesContext
    public void testGetAll() {
        UserRequest ur1 = new UserRequest("name1", "email1@box.ru");
        UserRequest ur2 = new UserRequest("name2", "email2@box.ru");
        userService.create(ur1);
        userService.create(ur2);

        assertEquals(2, userService.getAll().size());
    }

    @Test
    @DirtiesContext
    public void testCreateDoubleEmail() {
        UserRequest ur1 = new UserRequest("name1", "email1@box.ru");
        UserRequest ur2 = new UserRequest("name2", "email1@box.ru");
        userService.create(ur1);

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.create(ur2));
    }

    @Test
    @DirtiesContext
    public void testUpdate() {
        UserRequest ur1 = new UserRequest("name1", "email1@box.ru");
        int id = userService.create(ur1).getId();

        UserRequest ur2 = new UserRequest("name2", "email2@box.ru");

        User user = userService.update(ur2, id);
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(ur2.getName()));
        assertThat(user.getEmail(), equalTo(ur2.getEmail()));
    }

    @Test
    @DirtiesContext
    public void testUpdateDoubleEmail() {
        UserRequest ur1 = new UserRequest("name1", "email1@box.ru");
        UserRequest ur2 = new UserRequest("name2", "email2@box.ru");
        userService.create(ur1);
        int id = userService.create(ur2).getId();

        ur2.setEmail("email1@box.ru");

        assertThrows(DataIntegrityViolationException.class,
                () -> userService.update(ur2, id));
    }

    @Test
    @DirtiesContext
    public void testDelete() {
        UserRequest ur1 = new UserRequest("name1", "email1@box.ru");
        int id = userService.create(ur1).getId();

        userService.delete(id);

        assertEquals(0, userService.getAll().size());
    }
}
