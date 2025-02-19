package com.example.demo.domain.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service

public class EventService {
    public EventService(){
    }
    @Autowired
    private EventRepository eventRepository;

}
