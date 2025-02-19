package com.example.demo.domain.event;

import com.example.demo.core.generic.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "events")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Event extends AbstractEntity {
    @Column(name = "guest_list")
    private String guestList;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "location")
    private String location;

    public Event(UUID id, String guestList, String eventName, LocalDate date, String location) {
        super(id);
        this.guestList = guestList;
        this.eventName = eventName;
        this.date = date;
        this.location = location;
    }
}
