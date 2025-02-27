package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.CreateEventDTO;
import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserService;
import com.example.demo.domain.user.dto.UserDTO;
import com.example.demo.domain.user.dto.UserMapper;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
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

    /**
     * Retrieves all events with pagination.
     *
     * @param size   Number of events per page.
     * @param offset Starting index for pagination.
     * @return ResponseEntity containing a list of EventDTO objects.
     */
    @GetMapping({"", "/"})
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<List<EventDTO>> getAllEvents(@RequestParam("size") Integer size,
                                                       @RequestParam("offset") Integer offset) {
        log.info("Fetching all events with size: {} and offset: {}", size, offset);
        List<Event> events = eventService.getAllEvents(size, offset);
        log.info("Fetched {} events.", events.size());
        return new ResponseEntity<>(eventMapper.toDTOs(events), HttpStatus.OK);
    }

    /**
     * Retrieves all events with pagination.
     *
     * @param id
     * @param size   Number of events per page.
     * @param offset Starting index for pagination.
     * @return ResponseEntity containing a list of EventDTO objects.
     */
    @GetMapping({"creator/{id}", "/creator/{id}"})
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<List<EventDTO>> getAllEventsWithEventCreator(@PathVariable("id") UUID id,
                                                                       @RequestParam("size") Integer size,
                                                                       @RequestParam("offset") Integer offset) {
        List<Event> events = eventService.getAllEventsWithEventCreator(id, size, offset);
        return new ResponseEntity<>(eventMapper.toDTOs(events), HttpStatus.OK);
    }

    /**
     * Retrieves all guests of a specific event with pagination.
     *
     * @param id     UUID of the event.
     * @param size   Number of guests per page.
     * @param offset Starting index for pagination.
     * @return ResponseEntity containing a list of UserDTO objects.
     */
    @GetMapping({"{id}/guests/", "/{id}/guests/"})
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<List<UserDTO>> getAllGuests(@PathVariable("id") UUID id,
                                                      @RequestParam("size") Integer size,
                                                      @RequestParam("offset") Integer offset) {
        log.info("Fetching guests for event with id: {} with size: {} and offset: {}", id, size, offset);
        List<User> guests = eventService.getAllGuests(id, size, offset);
        log.info("Fetched {} guests for event with id: {}", guests.size(), id);
        return new ResponseEntity<>(userMapper.toDTOs(guests), HttpStatus.OK);
    }

    /**
     * Retrieves a specific event by its ID.
     *
     * @param id UUID of the event.
     * @return ResponseEntity containing the EventDTO object.
     */
    @GetMapping({"{id}", "/{id}"})
    @PreAuthorize("hasAuthority('EVENT_READ')")
    public ResponseEntity<EventDTO> getEvent(@PathVariable("id") UUID id) {
        log.info("Fetching event with id: {}", id);
        Event event = eventService.getEvent(id);
        log.info("Fetched event with id: {}", id);
        return new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK);
    }
    /**
     * Creates a new event.
     *
     * @param eventDto Data transfer object containing event details.
     * @return ResponseEntity containing the created EventDTO object.
     */
    @PostMapping({"", "/"})
    @PreAuthorize("hasAuthority('EVENT_CREATE')")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventDTO eventDto) {
        log.info("Creating new event with details: {}", eventDto);
        Event event = eventMapper.fromCreateEventDTO(eventDto);
        Set<User> guestList = userService.getUsersById(eventDto.getGuestList());
        event.setGuestList(guestList);
        Event savedEvent = null;
        try {
            savedEvent = eventService.createEvent(event);
            log.info("Event created successfully with id: {}", savedEvent.getId());
        } catch (ResponseStatusException responseStatusException){
            log.error("Error creating event: {}", responseStatusException.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }

    /**
     * Updates an existing event.
     *
     * @param eventDto Data transfer object containing updated event details.
     * @param id       UUID of the event to update.
     * @return ResponseEntity containing the updated EventDTO object.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EVENT_MODIFY')")
    public ResponseEntity<EventDTO> updateEvent(@RequestBody CreateEventDTO eventDto, @PathVariable("id") UUID id) {
        log.info("Updating event with id: {} with details: {}", id, eventDto);
        Event event = eventMapper.fromCreateEventDTO(eventDto);
        event.setId(id);
        Event savedEvent = null;

        try {
            savedEvent = eventService.updateEvent(event);
        } catch (ResponseStatusException responseStatusException){
            log.error("Error updating event with id: {}: {}", id, responseStatusException.getMessage());
            if (responseStatusException.getStatusCode() == HttpStatus.FORBIDDEN) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else if (responseStatusException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }


        return new ResponseEntity<>(eventMapper.toDTO(savedEvent), HttpStatus.CREATED);
    }

    /**
     * Deletes an event by its ID.
     *
     * @param id UUID of the event to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping({"{id}", "/{id}"})
    @PreAuthorize("hasAuthority('EVENT_DELETE')")
    public ResponseEntity<Void> deleteEventById(@PathVariable("id") UUID id) {
        log.info("Deleting event with id: {}", id);
        eventService.deleteEventById(id);
        log.info("Event with id: {} deleted successfully.", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
