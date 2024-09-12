package com.datingapp.racontre.Service;

import com.datingapp.racontre.Model.User;
import com.datingapp.racontre.Repository.LikeRepository;
import com.datingapp.racontre.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private SessionManagementService sessionManagementService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public Set<User> getUsersWhoLikedMe(User user) {
        return user.getLikedByUsers();
    }

    public void setUserOnlineStatus(String username, boolean isOnline) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setIsOnline(isOnline);
            userRepository.save(user);
        }
    }

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        if (user.getAge() < 17) {
            throw new IllegalArgumentException("You must be at least 17 years old to join.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllExcept(String username) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    public List<User> findAllExceptLiked(String username) {
        User currentUser = userRepository.findByUsername(username);
        List<User> likedUsers = likeRepository.findLikedUsersByLiker(currentUser.getId());
        String currentUserGender = currentUser.getGender(); // Assurez-vous que cette propriété existe

        List<User> otherUsers = userRepository.findAll().stream()
                .filter(user -> !likedUsers.contains(user) && !user.getUsername().equals(username))
                .filter(user -> !user.getGender().equals(currentUserGender)) // Filtre par sexe
                .collect(Collectors.toList());

        for (User user : otherUsers) {
            boolean isMutualLike = likeRepository.existsByLikerAndLiked(user, currentUser) && likeRepository.existsByLikerAndLiked(currentUser, user);
            user.setMutualLike(isMutualLike);
        }

        for (User user : otherUsers) {
            boolean isOnline = sessionManagementService.isUserOnline(user); // Check if user is online
            user.setIsOnline(isOnline); // Set the user's online status
        }

        return otherUsers;
    }


    public Optional<User> findById(Long receiverId) {
        return  userRepository.findById(receiverId);
    }

//    public List<User> findAllUsers() {
//        List<User> allUsers = userRepository.findAll(); // Fetch all users from the database
//
//        // Update the online status for each user based on your session/activity logic
//        for (User user : allUsers) {
//            boolean isOnline = sessionManagementService.isUserOnline(user); // Check if user is online
//            user.setIsOnline(isOnline); // Set the user's online status
//        }
//
//        return allUsers;
//    }
}
