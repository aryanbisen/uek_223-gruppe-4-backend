package com.example.demo.domain.event;

import com.example.demo.core.generic.AbstractEntity;
import com.example.demo.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull()
    private User eventCreator;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "events_guests", // TODO: naming?
            joinColumns = @JoinColumn(name = "events_id", referencedColumnName = "id"), // Event id
            inverseJoinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id") // User id
    )
    private Set<User> guestList = new HashSet<>();

    @Column(name = "event_name")
    @NotBlank(message = "Event name cannot be blank")
    @Size(max = 200, message = "Event name must be at most 200 characters")
    private String eventName;

    @Column(name = "date")
    @NotNull(message = "Event date cannot be null")
    @FutureOrPresent(message = "Event date must be in the future or present")
    private LocalDate date;

    @Column(name = "location")
    @NotBlank(message = "Location cannot be blank")
    @Size(max = 200, message = "Location must be at most 200 characters")
    private String location;

    public Event(UUID id, Set<User> guestList, String eventName, LocalDate date, String location) {
        super(id);
        this.guestList = guestList;
        this.eventName = eventName;
        this.date = date;
        this.location = location;
    }
}
