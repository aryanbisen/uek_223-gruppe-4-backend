package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.CreateEventDTO;
import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserService;
import com.example.demo.domain.user.dto.UserDTO;
import com.example.demo.domain.user.dto.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private final UserMapper userMapper;

    public EventController(EventMapper eventMapper, UserMapper userMapper) {
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<EventDTO>> getAllEvents(@RequestParam("size") Integer size,
                                                       @RequestParam("offset") Integer offset) {
        List<Event> events = eventService.getAllEvents(size, offset);
        return new ResponseEntity<>(eventMapper.toDTOs(events), HttpStatus.OK);
    }

    @GetMapping({"{id}/guests/", "/{id}/guests/"})
    public ResponseEntity<List<UserDTO>> getAllGuests(@PathVariable("id") UUID id,
                                                      @RequestParam("size") Integer size,
                                                      @RequestParam("offset") Integer offset) {
        List<User> guests = eventService.getAllGuests(id, size, offset);
        return new ResponseEntity<>(userMapper.toDTOs(guests), HttpStatus.OK);
    }

    @GetMapping({"{id}", "/{id}"})
    public ResponseEntity<EventDTO> getEvent(@PathVariable("id") UUID id) {
        Event event = eventService.getEvent(id);
        return new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventDTO eventDto) {
        Event event = eventMapper.fromCreateEventDTO(eventDto);
        Set<User> guestList = userService.getUsersById(eventDto.getGuestList());
        event.setGuestList(guestList);
        Event savedEvent = eventService.createEvent(event);
        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }

    @PutMapping({"", "/"})
    public ResponseEntity<EventDTO> updateEvent(@RequestBody EventDTO eventDto) {
        Event event = eventMapper.fromDTO(eventDto);
        Event savedEvent = eventService.updateEvent(event);
        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }

    @DeleteMapping({"{id}", "/{id}"})
    public ResponseEntity<Void> deleteEventById(@PathVariable("id") UUID id) {
        eventService.deleteEventById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
