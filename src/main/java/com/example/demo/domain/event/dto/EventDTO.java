package com.example.demo.domain.event.dto;

import com.example.demo.domain.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private UUID id;
    private String eventName;
    private LocalDate date;
    private String location;
    private Set<UserDTO> guestList; // Uses UserDTO instead of User
}
