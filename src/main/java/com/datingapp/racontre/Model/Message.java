package com.datingapp.racontre.Model;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "users_messages")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    private String content;

    @Column(name = "is_read")
    private boolean isRead = false;
    private String imageUrl;
    private LocalDateTime timestamp;

}
