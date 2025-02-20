package com.example.demo.domain.event;

import com.example.demo.core.generic.AbstractEntity;
import com.example.demo.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "events")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Event extends AbstractEntity {
    @ManyToOne()
    @JoinColumn(name = "event_creator", referencedColumnName = "id", nullable = false)
    private User eventCreator;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "events_guests", // TODO: naming?
            joinColumns = @JoinColumn(name = "events_id", referencedColumnName = "id"), // Event id
            inverseJoinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id") // User id
    )
    private Set<User> guestList = new HashSet<>();

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "location")
    private String location;

    public Event(UUID id, Set<User> guestList, String eventName, LocalDate date, String location) {
        super(id);
        this.guestList = guestList;
        this.eventName = eventName;
        this.date = date;
        this.location = location;
    }
}
