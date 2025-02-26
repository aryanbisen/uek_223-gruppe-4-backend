package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventMapper eventMapper;

    @GetMapping({"", "/"})
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok().body(events.stream().map(eventMapper::toDTO).toList());
    }

    @GetMapping({"{id}", "/{id}"})
    public ResponseEntity<EventDTO> getEvent(@PathVariable UUID id) {
        Optional<Event> result = eventService.getEvent(id);
        return result.map(event -> new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDto) {
        Event event = eventMapper.fromDTO(eventDto);
        Optional<Event> result = eventService.createEvent(event);
        return result.map(url -> new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping({"", "/"})
    public ResponseEntity<EventDTO> updateEvent(@RequestBody EventDTO eventDto) {
        Event event = eventMapper.fromDTO(eventDto);
        Optional<Event> result = eventService.updateEvent(event);
        return result.map(url -> new ResponseEntity<>(eventMapper.toDTO(event), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable UUID id) {
        eventService.deleteEventById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
