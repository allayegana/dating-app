package com.datingapp.racontre.Controller;

import com.datingapp.racontre.Dto.UserDto;
import com.datingapp.racontre.Model.User;
import com.datingapp.racontre.Service.LikeService;
import com.datingapp.racontre.Service.MessageService;
import com.datingapp.racontre.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/login")
    public String getUser(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/terms")
    public String showTerms() {
        return "terms";
    }

    @GetMapping("/privacy")
    public String showprivacy() {
        return "privacy";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDto());
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserDto userDTO, @RequestParam("profilePicture") MultipartFile file) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setAge(userDTO.getAge());
        user.setLocation(userDTO.getLocation());
        user.setMaritalStatus(userDTO.getMaritalStatus());
        user.setGender(userDTO.getGender());
        user.setHasChildren(userDTO.isHasChildren());
        user.setMutualLike(userDTO.isMutualLike());
        user.setSkinTone(userDTO.getSkinTone());
        user.setTermsAccepted(userDTO.isTermsAccepted());


        try {
            if (!file.isEmpty()) {
                byte[] imageBytes = file.getBytes();
                user.setProfilePicture(imageBytes);
            }
            userService.saveUser(user);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/register?error=uploadFailed";
        }
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        // Get the logged-in user
        User user = userService.findByUsername(currentUser.getUsername());
        model.addAttribute("user", user);

        // List other users excluding those already "liked"
        List<User> otherUsers = userService.findAllExceptLiked(currentUser.getUsername());
        model.addAttribute("otherUsers", otherUsers);

//        List<User> otherUser = userService.findAllUsers(); // Fetch all users with online status
//        model.addAttribute("otherUser", otherUser);

        // Users who have "liked" the current user
        Set<User> likedByUsers = userService.getUsersWhoLikedMe(user);
        model.addAttribute("likedByUsers", likedByUsers);


        // Check if the current user has unread messages
        boolean hasUnreadMessages = messageService.hasUnreadMessagesForUser(user);
        model.addAttribute("hasUnreadMessages", hasUnreadMessages);

        return "profile"; // Return the profile template
    }




    @PostMapping("/upload-profile-picture")
    public String uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/profile?error=userNotFound";
        }

        try {
            // Stocke l'image en BLOB
            byte[] imageBytes = file.getBytes();
            user.setProfilePicture(imageBytes);
            userService.saveUser(user);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/profile?error=uploadFailed";
        }

        return "redirect:/profile";
    }

    @GetMapping("/profile-picture/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable("id") Long id) throws IOException {
        User user = userService.findById(id).orElse(null);
        if (user == null || user.getProfilePicture() == null) {
            // Retourne une image par défaut si l'image n'existe pas
            InputStream defaultImageStream = getClass().getResourceAsStream("/static/images/default.png");
            byte[] defaultImageBytes = StreamUtils.copyToByteArray(defaultImageStream);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(defaultImageBytes);
        }

        byte[] imageBytes = user.getProfilePicture();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

        try {
            BufferedImage bufferedImage = ImageIO.read(bis);
            if (bufferedImage == null) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

}
