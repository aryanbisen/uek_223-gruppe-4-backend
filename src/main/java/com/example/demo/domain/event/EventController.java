package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

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
    public ResponseEntity<EventDTO> getEvent(@PathVariable UUID id) {
        Event event = eventService.getEvent(id);
        return new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK);
    }

    @PostMapping({"", "/"})
    public ResponseEntity<URL> createEvent(@RequestBody EventDTO event) {
        Optional<URL> result = eventService.createEvent(event);
        return result.map(url -> new ResponseEntity<>(url, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping({"", "/"})
    public ResponseEntity<URL> updateEvent(@RequestBody EventDTO event) {
        Optional<URL> result = eventService.updateEvent(event);
        return result.map(url -> new ResponseEntity<>(url, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable UUID id) {
        eventService.deleteEventById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
