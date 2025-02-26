package com.example.demo.domain.event;

import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.UUID;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents (Integer size, Integer offset) {
        if (size < 1 || offset < 0) {
            throw new IllegalArgumentException("Invalid pagination details provided.");
        }
        PageRequest request = PageRequest.of(offset, size);
        return eventRepository.findAll(request).toList();
    }

    public List<User> getAllGuests (UUID eventId, Integer size, Integer offset) {
        if (size < 1 || offset < 0) {
            throw new IllegalArgumentException("Invalid pagination details provided.");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(NoSuchElementException::new);
        List<User> guests = event.getGuestList().stream().toList();
        return guests.subList(offset * size, (offset * size) + size);
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
    public Event updateEvent(Event event) {
        if (!isValid(event)) {
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

    private boolean isValid(Event event) {
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
