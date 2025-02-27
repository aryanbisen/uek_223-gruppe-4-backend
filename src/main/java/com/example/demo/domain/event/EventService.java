package com.example.demo.domain.event;

import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserDetailsImpl;
import com.example.demo.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    private UserService userService;


    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEvents(Integer size, Integer offset) {
        if (size < 1 || offset < 0) {
            throw new IllegalArgumentException("Invalid pagination details provided.");
        }
        PageRequest request = PageRequest.of(offset, size);
        return eventRepository.findAll(request).toList();
    }

    public List<User> getAllGuests(UUID eventId, Integer size, Integer offset) {
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
        if(guestListContainsAdmin(event.getGuestList())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admins can't participate in events.");
        }

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

        if(guestListContainsAdmin(newEvent.getGuestList())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admins can't participate in events.");
        }
        event.setGuestList(newEvent.getGuestList());

        if (!isValidEvent(event)) {
            throw new IllegalArgumentException("Invalid event details provided.");
        }

        return eventRepository.save(event);
    }


    public void deleteEventById(UUID id) throws NoSuchElementException {
        Event event = getEvent(id);
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userDetails.getUser();

        if (!(user.getId()).equals(event.getEventCreator().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the creator of this event. Can't " +
                    "delete events you don't own");
        }
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new NoSuchElementException(String.format("Event with ID '%s' could not be found", id));
        }
    }

    private boolean guestListContainsAdmin(Set<User> users) {
        for (User guest : users) {
            User fullGuest = userService.findById(guest.getId()); // Fetch full User object
            if (fullGuest.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
                return true;
            }
        }
        return false;
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
