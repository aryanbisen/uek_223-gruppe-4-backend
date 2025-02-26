package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserDetailsImpl;
import com.example.demo.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Autowired
    private UserService userService;

    public EventService(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEvent(UUID id) {
        return eventRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public Event createEvent(Event event) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userDetails.getUser();
        event.setEventCreator(user);

        if (!isValid(event)) {
            throw new IllegalArgumentException("Invalid event details provided.");
        }

        return eventRepository.save(event);
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
        if (event == null) {
            return false;
        }

        UUID id = event.getId();
        User eventCreator = event.getEventCreator();
        Set<User> guestList = event.getGuestList();
        String eventName = event.getEventName();
        LocalDate eventDate = event.getDate();
        String eventLocation = event.getLocation();
        return
                eventCreator != null &&
                eventName != null && !eventName.isBlank() &&
                eventDate != null && !eventDate.isBefore(LocalDate.now()) &&
                eventLocation != null && !eventLocation.isBlank() &&
                guestList != null; // Ensuring guest list is initialized (can be empty but not null).
    }

}
