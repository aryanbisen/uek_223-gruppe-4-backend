package com.example.demo.domain.event.dto;

import com.example.demo.domain.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
 EventDTO toDTO (Event event);
 Event fromDTO (EventDTO dto);
}
