package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
}
