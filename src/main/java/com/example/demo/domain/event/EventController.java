package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.CreateEventDTO;
import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }


    @GetMapping({"", "/"})
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return new ResponseEntity<>(eventMapper.toDTOs(events), HttpStatus.OK);
    }

    @GetMapping({"{id}", "/{id}"})
    public ResponseEntity<EventDTO> getEvent(@PathVariable("id") UUID id) {
        Event event = eventService.getEvent(id);
        return new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventDTO dto) {
        Event event = eventMapper.fromCreateEventDTO(dto);
        Set<User> guestList = userService.getUsersById(dto.getGuestList());
        event.setGuestList(guestList);
        Event savedEvent = eventService.createEvent(event);
        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@RequestBody CreateEventDTO eventDto, @PathVariable("id") UUID id) {
        Event event = eventMapper.fromCreateEventDTO(eventDto);
        event.setId(id);
        Event savedEvent;

        try {
            savedEvent = eventService.updateEvent(event);
        } catch (ResponseStatusException responseStatusException){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }


        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable UUID id) {
        eventService.deleteEventById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
