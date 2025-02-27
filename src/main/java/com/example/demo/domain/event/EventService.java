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
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EventService {
    public EventService() {
    }

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;


    public List<Event> getAllEvents(Integer size, Integer offset) {
        log.info("Fetching all events with pagination: size = {}, offset = {}", size, offset);

        // Ensure pagination values are valid
        if (size < 1 || offset < 0) {
            log.error("Invalid pagination details provided: size = {}, offset = {}", size, offset);
            throw new IllegalArgumentException("Invalid pagination details provided.");
        }

        PageRequest request = PageRequest.of(offset, size);
        List<Event> events = eventRepository.findAll(request).toList(); // Fetch events based on pagination.
        log.info("Fetched {} events.", events.size());
        return events;
    }

    public List<Event> getAllEventsWithEventCreator(UUID id, Integer size, Integer offset) {
        // Ensure pagination values are valid
        if (size < 1 || offset < 0) {
            throw new IllegalArgumentException("Invalid pagination details provided.");
        }
        PageRequest request = PageRequest.of(offset, size);
        return eventRepository.findByEventCreator_Id(id, request).toList(); // Fetch events based on event creator and pagination
    }

    // Method to retrieve a list of guests for a specific event with pagination
    public List<User> getAllGuests(UUID eventId, Integer size, Integer offset) {
        log.info("Fetching guests for event with ID: {} with pagination: size = {}, offset = {}", eventId, size, offset);

        if (size < 1 || offset < 0) {
            log.error("Invalid pagination details provided for event with ID: {}: size = {}, offset = {}", eventId, size, offset);
            throw new IllegalArgumentException("Invalid pagination details provided.");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID: {} not found.", eventId);
            return new NoSuchElementException("Event not found.");
        });

        List<User> guests = event.getGuestList().stream().toList();  // Convert guest list to a list
        log.info("Fetched {} guests for event with ID: {}", guests.size(), eventId);
        return guests.subList(offset * size, (offset * size) + size);
    }

    public Event getEvent(UUID id) {
        log.info("Fetching event with ID: {}", id);
        return eventRepository.findById(id).orElseThrow(() -> {
            log.error("Event with ID: {} not found.", id);
            return new NoSuchElementException("Event not found.");
        });
    }

    @Transactional
    public Event createEvent(Event event) {
        log.info("Creating event: {}", event);

        // Retrieve the currently authenticated user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userDetails.getUser();
        event.setEventCreator(user);

        // Check if any admins are in the guest list
        if (guestListContainsAdmin(event.getGuestList())) {
            log.error("Attempt to create an event with admin in the guest list: {}", event);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admins can't participate in events.");
        }

        if (!isValidEvent(event)) {
            log.error("Invalid event details: {}", event);
            throw new IllegalArgumentException("Invalid event details provided.");
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully with ID: {}", savedEvent.getId());
        return savedEvent;
    }

    @Transactional
    public Event updateEvent(Event newEvent) {
        log.info("Updating event with ID: {} with new details: {}", newEvent.getId(), newEvent);

        // Fetch existing event by ID
        Event event = getEvent(newEvent.getId());

        // Retrieve the currently authenticated user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userDetails.getUser();

        // Ensure that the authenticated user is the creator of the event
        if (!(user.getId()).equals(event.getEventCreator().getId())) {
            log.error("User with ID: {} is not the creator of event with ID: {}. Update not allowed.", user.getId(), event.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the creator of this event. Can't update events you don't own");
        }

        // Update event details with new data
        event.setEventName(newEvent.getEventName());
        event.setDate(newEvent.getDate());
        event.setLocation(newEvent.getLocation());

        // Validate if the guest list contains an admin
        if (guestListContainsAdmin(newEvent.getGuestList())) {
            log.error("Attempt to update event with ID: {} with admin in the guest list", newEvent.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admins can't participate in events.");
        }
        event.setGuestList(newEvent.getGuestList());

        // Validate event details again before saving
        if (!isValidEvent(event)) {
            log.error("Invalid event details for update: {}", event);
            throw new IllegalArgumentException("Invalid event details provided.");
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Event updated successfully with ID: {}", savedEvent.getId());
        return savedEvent;
    }

    @Transactional
    public void deleteEventById(UUID id) {
        log.info("Deleting event with ID: {}", id);

        // Fetch existing event by ID
        Event event = getEvent(id);

        // Retrieve the currently authenticated user
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userDetails.getUser();

        // Ensure the user is the creator of the event before allowing deletion
        if (!(user.getId()).equals(event.getEventCreator().getId())) {
            log.error("User with ID: {} is not the creator of event with ID: {}. Deletion not allowed.", user.getId(), event.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the creator of this event. Can't delete events you don't own");
        }

        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            log.info("Event with ID: {} deleted successfully.", id);
        } else {
            log.error("Event with ID: {} could not be found for deletion.", id);
            throw new NoSuchElementException(String.format("Event with ID '%s' could not be found", id));
        }
    }

    // Helper method to check if any guest in the event is an admin
    private boolean guestListContainsAdmin(Set<User> users) {
        log.info("Checking if any guest is an admin in the guest list.");
        for (User guest : users) {
            User fullGuest = userService.findById(guest.getId()); // Fetch full User object
            if (fullGuest.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
                log.info("Admin found in guest list: {}", guest.getId());
                return true;
            }
        }
        return false;
    }

    // Helper method to validate event details.
    private boolean isValidEvent(Event event) {
        log.info("Validating event details: {}", event);
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
