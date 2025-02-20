package com.example.demo.domain.event;

import com.example.demo.domain.event.dto.EventDTO;
import com.example.demo.domain.event.dto.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

}
