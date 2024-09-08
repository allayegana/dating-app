package com.datingapp.racontre.Repository;

import com.datingapp.racontre.Model.Like;
import com.datingapp.racontre.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByLikerAndLiked(User liker, User liked);
    boolean existsByLikerAndLiked(User liker, User liked);

    @Query("SELECT l.liked FROM Like l WHERE l.liker.id = :likerId")
    List<User> findLikedUsersByLiker(@Param("likerId") Long likerId);
}
