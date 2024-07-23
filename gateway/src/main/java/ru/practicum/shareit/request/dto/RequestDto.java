package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 2000, groups = {Marker.OnCreate.class})
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
}
