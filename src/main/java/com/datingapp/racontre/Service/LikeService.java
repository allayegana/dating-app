package com.datingapp.racontre.Service;

import com.datingapp.racontre.Model.Like;
import com.datingapp.racontre.Model.User;
import com.datingapp.racontre.Repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserService userService;

    public void likeUser(User liker, User liked) {
        // Vérifie si le like existe déjà
        Optional<Like> existingLike = likeRepository.findByLikerAndLiked(liker, liked);
        if (existingLike.isEmpty()) {
            // Crée un nouveau like s'il n'existe pas
            Like like = new Like();
            like.setLiker(liker);
            like.setLiked(liked);
            likeRepository.save(like);
        }
    }

    public boolean isMutualLike(User liker, User liked) {
        // Vérifie si le like est mutuel
        boolean likerLikesLiked = likeRepository.existsByLikerAndLiked(liker, liked);
        boolean likedLikesLiker = likeRepository.existsByLikerAndLiked(liked, liker);
        return likerLikesLiked && likedLikesLiker;
    }

    public Set<User> getUsersWhoLiked(User user) {
        // Récupère tous les utilisateurs qui ont liké cet utilisateur
        return user.getLikedByUsers();
    }
}
