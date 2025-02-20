package com.example.demo.domain.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public EventService() {
    }

    public List<Event> getAllEvents (){
        return eventRepository.findAll();
    }

    @Transactional
    public Optional<URL> createEvent(Event event) {
        if (isValid(event)) {
            eventRepository.save(event);
            try {
                URL result = new URL("https://localhost:8080/events/" + event.getId());
                return Optional.of(result);
            } catch (MalformedURLException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<URL> updateEvent(Event event) {
        if (eventRepository.findById(event.getId()).isEmpty()) {
            return Optional.empty();
        }

        if (isValid(event)) {
            eventRepository.save(event);
            try {
                URL result = new URL("https://localhost:8080/events/" + event.getId());
                return Optional.of(result);
            } catch (MalformedURLException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
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
