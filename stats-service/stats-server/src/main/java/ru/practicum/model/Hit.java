package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hits_model")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private Long id;
    @Column(name = "hit_app")
    private String app;
    @Column(name = "hit_uri")
    private String uri;
    @Column(name = "hit_ip")
    private String ip;
    @Column(name = "hit_time")
    private LocalDateTime timestamp;
}
