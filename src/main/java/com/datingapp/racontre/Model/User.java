package com.datingapp.racontre.Model;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String name;
    private int age;
    private String location;
    private boolean mutualLike;

    private String maritalStatus; // Célibataire, Marié, Divorcé
    private boolean hasChildren;
    private String gender; // Homme, Femme, Autre
    private boolean termsAccepted;
    private String skinTone;

    private LocalDateTime lastMessageTime;

    @Transient  // This ensures that this field is not persisted in the database
    private String lastMessage;




    @ManyToMany
    @JoinTable(
            name = "user_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "liked_user_id"))
    private Set<User> likedUsers = new HashSet<>();

    @ManyToMany(mappedBy = "likedUsers")
    private Set<User> likedByUsers = new HashSet<>();

    @Lob
    @Column(name = "profile_picture", columnDefinition = "MEDIUMBLOB")
    private byte[] profilePicture;

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver")
    private List<Message> receivedMessages;
}