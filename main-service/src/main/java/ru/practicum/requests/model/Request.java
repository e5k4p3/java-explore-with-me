package ru.practicum.requests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.model.Event;
import ru.practicum.requests.model.enums.RequestStatus;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "event", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester", nullable = false)
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;
}
