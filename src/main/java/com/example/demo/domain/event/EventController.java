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
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final UserMapper userMapper;

    public EventController(EventMapper eventMapper, UserMapper userMapper) {
        this.eventMapper = eventMapper;
        this.userMapper = userMapper;
    }

    @GetMapping({"", "/"})
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<List<EventDTO>> getAllEvents(@RequestParam("size") Integer size,
                                                       @RequestParam("offset") Integer offset) {
        List<Event> events = eventService.getAllEvents(size, offset);
        return new ResponseEntity<>(eventMapper.toDTOs(events), HttpStatus.OK);
    }

    @GetMapping({"{id}/guests/", "/{id}/guests/"})
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<List<UserDTO>> getAllGuests(@PathVariable("id") UUID id,
                                                      @RequestParam("size") Integer size,
                                                      @RequestParam("offset") Integer offset) {
        List<User> guests = eventService.getAllGuests(id, size, offset);
        return new ResponseEntity<>(userMapper.toDTOs(guests), HttpStatus.OK);
    }

    @GetMapping({"{id}", "/{id}"})
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<EventDTO> getEvent(@PathVariable("id") UUID id) {
        Event event = eventService.getEvent(id);
        return new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventDTO eventDto) {
        Event event = eventMapper.fromCreateEventDTO(eventDto);
        Set<User> guestList = userService.getUsersById(eventDto.getGuestList());
        event.setGuestList(guestList);
        Event savedEvent = null;
        try {
            savedEvent = eventService.createEvent(event);
        } catch (ResponseStatusException responseStatusException){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EVENT_MODIFY')")
    public ResponseEntity<EventDTO> updateEvent(@RequestBody CreateEventDTO eventDto, @PathVariable("id") UUID id) {
        Event event = eventMapper.fromCreateEventDTO(eventDto);
        event.setId(id);
        Event savedEvent = null;

        try {
            savedEvent = eventService.updateEvent(event);
        } catch (ResponseStatusException responseStatusException){
            if (responseStatusException.getStatusCode() == HttpStatus.FORBIDDEN) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else if (responseStatusException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }


        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }

    @DeleteMapping({"{id}", "/{id}"})
    @PreAuthorize("hasAuthority('EVENT_DELETE')")
    public ResponseEntity<Void> deleteEventById(@PathVariable("id") UUID id) {
        eventService.deleteEventById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
