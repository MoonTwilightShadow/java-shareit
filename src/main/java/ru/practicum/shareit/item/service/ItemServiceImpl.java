package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.NotOwnerException;
import ru.practicum.shareit.exception.exceptions.OwnerExeption;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item getById(Integer id) {
        log.info("getById item method");

        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            throw new NotFoundException();
        }

        return item.get();
    }

    @Override
    public List<Item> getByOwner(Integer owner) {
        log.info("getByOwner item method");

        if (userRepository.findById(owner).isEmpty()) {
            throw new NotOwnerException();
        }

        return itemRepository.findItemsByOwner(owner);
    }

    @Override
    public List<Item> search(String text) {
        log.info("search item method");

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findItemsByNameOrDescriptionContainsIgnoreCaseAndAvailableTrue(text, text);
    }

    @Override
    public Item create(CreateItemRequest request) {
        log.info("create item method");

        Item item = modelMapper.map(request, Item.class);

        if (userRepository.findById(item.getOwner()).isEmpty()) {
            throw new NotOwnerException();
        }

        return itemRepository.save(item);
    }

    @Override
    public Item update(UpdateItemRequest request) {
        log.info("update item method");

        Optional<Item> item = itemRepository.findById(request.getId());

        if (item.isEmpty()) {
            throw new NotFoundException();
        }

        if (!Objects.equals(item.get().getOwner(), request.getOwner())) {
            throw new OwnerExeption();
        }

        if (request.getName() != null) {
            item.get().setName(request.getName());
        }

        if (request.getDescription() != null) {
            item.get().setDescription(request.getDescription());
        }

        if (request.getAvailable() != null) {
            item.get().setAvailable(request.getAvailable());
        }

        return itemRepository.save(item.get());
    }

    @Override
    public void delete(Integer id) {
        log.info("delete item method");

        itemRepository.deleteById(id);
    }
}
