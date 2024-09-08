package com.datingapp.racontre.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "Like_h")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User liker;  // L'utilisateur qui aime

    @ManyToOne
    private User liked;  // L'utilisateur qui est aimé

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getLiker() {
        return liker;
    }

    public void setLiker(User liker) {
        this.liker = liker;
    }

    public User getLiked() {
        return liked;
    }

    public void setLiked(User liked) {
        this.liked = liked;
    }
}

