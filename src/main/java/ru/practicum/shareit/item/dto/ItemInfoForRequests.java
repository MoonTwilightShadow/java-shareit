package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoForRequests {
    private Integer id;
    private String name;
    private String description;
    private Integer requestId;
    private Boolean available;
}
