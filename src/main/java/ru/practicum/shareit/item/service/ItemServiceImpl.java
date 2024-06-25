package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NotOwnerException;
import ru.practicum.shareit.exception.exceptions.OwnerExeption;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ModelMapper modelMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item getById(Integer id) {
        log.info("getById item method");

        return itemStorage.getById(id);
    }

    @Override
    public List<Item> getByOwner(Integer owner) {
        log.info("getByOwner item method");

        if (userStorage.getById(owner) == null)
            throw new NotOwnerException();

        return itemStorage.getByOwner(owner);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemStorage.search(text.toLowerCase());
    }

    @Override
    public Item create(CreateItemRequest request) {
        log.info("create item method");

        Item item = modelMapper.map(request, Item.class);

        if (userStorage.getById(item.getOwner()) == null)
            throw new NotOwnerException();

        return itemStorage.create(item);
    }

    @Override
    public Item update(UpdateItemRequest request) {
        log.info("update item method");

        Item saveItem = itemStorage.getById(request.getId());

        if (saveItem.getOwner() != request.getOwner())
            throw new OwnerExeption();

        if (request.getName() != null) {
            saveItem.setName(request.getName());
        }

        if (request.getDescription() != null) {
            saveItem.setDescription(request.getDescription());
        }

        if (request.getAvailable() != null) {
            saveItem.setAvailable(request.getAvailable());
        }

        return itemStorage.update(saveItem);
    }

    @Override
    public void delete(Integer id) {
        log.info("delete item method");
    }
}
