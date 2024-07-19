package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Marker;
import ru.practicum.shareit.validation.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid(groups = {Marker.OnCreate.class})
public class BookRequestDto {
	@NotNull(groups = {Marker.OnCreate.class})
	private Integer itemId;
	@FutureOrPresent(groups = {Marker.OnCreate.class})
	private LocalDateTime start;
	@Future
	private LocalDateTime end;
}
