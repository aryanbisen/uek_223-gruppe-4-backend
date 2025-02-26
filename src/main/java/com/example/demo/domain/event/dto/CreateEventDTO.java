package com.example.demo.domain.event.dto;

import com.example.demo.core.generic.AbstractDTO;
import com.example.demo.domain.user.dto.UserIdDTO;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
public class CreateEventDTO extends AbstractDTO {
    private String eventName;
    private LocalDate date;
    private String location;
    private Set<UserIdDTO> guestList;
}
