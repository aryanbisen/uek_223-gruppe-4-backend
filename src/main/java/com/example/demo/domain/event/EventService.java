package com.example.demo.domain.event;

import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.UUID;


@Service
public class EventService {
    public EventService() {
    }

    @Autowired
    private EventRepository eventRepository;


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

        if (!isValidEvent(event)) {
            throw new IllegalArgumentException("Invalid event details provided.");
        }

        return eventRepository.save(event);
    }


    @Transactional
    public Event updateEvent(Event newEvent) {
        Event event = getEvent(newEvent.getId());
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userDetails.getUser();

        if (!(user.getId()).equals(event.getEventCreator().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the creator of this event. Can't " +
                    "update events you don't own");
        }
        event.setEventName(newEvent.getEventName());
        event.setDate(newEvent.getDate());
        event.setLocation(newEvent.getLocation());
        event.setGuestList(newEvent.getGuestList());

        if (!isValidEvent(event)) {
            throw new IllegalArgumentException("Invalid event details provided.");
        }

        return eventRepository.save(event);
    }


    public void deleteEventById(UUID id) throws NoSuchElementException {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new NoSuchElementException(String.format("Event with ID '%s' could not be found", id));
        }
    }

    private boolean isValidEvent(Event event) {
        if (event == null) {
            return false;
        }

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
