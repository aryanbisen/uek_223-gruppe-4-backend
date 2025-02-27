package com.example.demo.domain.event;

import com.example.demo.core.generic.AbstractRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends AbstractRepository<Event> {
    public Page<Event> findByEventCreator_Id(UUID id, Pageable pageable);
}
