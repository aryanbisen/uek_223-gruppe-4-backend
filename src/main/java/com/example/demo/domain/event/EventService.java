package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventService(EventMapper eventMapper){
        this.eventMapper = eventMapper;
    }

    public List<EventDTO> getAllEvents (){
        return eventRepository.findAll().stream().map(eventMapper::toDTO).collect(Collectors.toList());
    }

    public Optional<EventDTO> getEvent(UUID id) {
        return eventRepository.findById(id).map(eventMapper::toDTO);
    }

    @Transactional
    public Optional<URL> createEvent(EventDTO eventDto) {
        Event event = eventMapper.fromDTO(eventDto);

        if (isValid(event)) {
            eventRepository.save(event);
            try {
                URL result = new URL("https://localhost:8080/event/" + event.getId());
                return Optional.of(result);
            } catch (MalformedURLException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<URL> updateEvent(EventDTO eventDto) {
        Event event = eventMapper.fromDTO(eventDto);

        if (eventRepository.findById(event.getId()).isEmpty()) {
            return Optional.empty();
        }

        if (isValid(event)) {
            eventRepository.save(event);
            try {
                URL result = new URL("https://localhost:8080/event/" + event.getId());
                return Optional.of(result);
            } catch (MalformedURLException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public void deleteEventById(UUID id) throws NoSuchElementException {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new NoSuchElementException(String.format("Event with ID '%s' could not be found", id));
        }
    }

    private boolean isValid(Event event) {
        String eventName = event.getEventName();
        LocalDate eventDate = event.getDate();
        String eventLocation = event.getLocation();

        return eventName != null && !eventName.isBlank() &&
                eventDate != null && !eventDate.isBefore(LocalDate.now()) &&
                eventLocation != null && !eventLocation.isBlank();
    }
}
