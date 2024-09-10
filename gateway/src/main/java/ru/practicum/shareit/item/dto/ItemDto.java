package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255, groups = {Marker.OnCreate.class})
    private String name;
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 2000, groups = {Marker.OnCreate.class})
    private String description;
    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;
    private Long owner;
    private Long requestId;
}
