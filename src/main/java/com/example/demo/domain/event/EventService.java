package com.example.demo.domain.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;
import java.util.UUID;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents (){
        return eventRepository.findAll();
    }

    public Optional<Event> getEvent(UUID id) {
        return eventRepository.findById(id);
    }

    @Transactional
    public Optional<Event> createEvent(Event event) {
        if (isValid(event)) {
            eventRepository.save(event);
            try {
                return Optional.of(event);
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Event> updateEvent(Event event) {
        if (eventRepository.findById(event.getId()).isEmpty()) {
            return Optional.empty();
        }

        if (isValid(event)) {
            eventRepository.save(event);
            try {
                return Optional.of(event);
            } catch (Exception e) {
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
