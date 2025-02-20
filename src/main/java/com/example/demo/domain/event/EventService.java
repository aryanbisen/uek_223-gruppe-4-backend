package com.example.demo.domain.event;

import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    public EventService(){
    }

    public List<Event> getAllEvents (){
        return eventRepository.findAll();
    }

}
