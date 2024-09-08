package com.datingapp.racontre.Controller;


import com.datingapp.racontre.Model.User;
import com.datingapp.racontre.Service.LikeService;
import com.datingapp.racontre.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @PostMapping("/like")
    public ResponseEntity<Map<String, Object>> likeUser(@AuthenticationPrincipal UserDetails currentUser,
                                                        @RequestBody Map<String, Long> payload) {
        Long likedUserId = payload.get("likedUserId");
        User liker = userService.findByUsername(currentUser.getUsername());
        User liked = userService.findById(likedUserId).orElse(null);

        Map<String, Object> response = new HashMap<>();
        if (liked != null) {
            likeService.likeUser(liker, liked);

            boolean mutualLike = likeService.isMutualLike(liker, liked);
            response.put("mutualLike", mutualLike);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/likes")
    public ResponseEntity<Set<User>> getUsersWhoLikedMe(@AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.findByUsername(currentUser.getUsername());
        Set<User> likedByUsers = likeService.getUsersWhoLiked(user);
        return ResponseEntity.ok(likedByUsers);
    }
}

