package com.datingapp.racontre.Controller;

import com.datingapp.racontre.Model.Message;
import com.datingapp.racontre.Model.User;
import com.datingapp.racontre.Service.MessageService;
import com.datingapp.racontre.Service.SessionManagementService;
import com.datingapp.racontre.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SessionManagementService sessionManagementService;

        @GetMapping("/conversations")
        public String listConversations(@AuthenticationPrincipal UserDetails currentUser, Model model) {
            if (currentUser == null) {
                return "redirect:/login";
            }

            User sender = userService.findByUsername(currentUser.getUsername());
            List<User> contacts = messageService.findContacts(sender);

            Map<User, Boolean> contactUnreadStatus = new HashMap<>();
            for (User contact : contacts) {
                boolean hasUnreadMessages = messageService.hasUnreadMessages(contact, sender);
                contactUnreadStatus.put(contact, hasUnreadMessages);

                // Récupérer et définir le dernier message pour chaque contact
                String lastMessage = messageService.getLastMessage(contact, sender);
                contact.setLastMessage(lastMessage);
            }

            for (User user : contacts) {
                boolean isOnline = sessionManagementService.isUserOnline(user); // Check if user is online
                user.setIsOnline(isOnline); // Set the user's online status
            }

            model.addAttribute("contacts", contacts);
            model.addAttribute("contactUnreadStatus", contactUnreadStatus);

            return "conversations";  // Utiliser un template distinct pour la liste
        }

        @GetMapping("/{receiverId}")
        public String viewConversation(@AuthenticationPrincipal UserDetails currentUser,
                                       @PathVariable Long receiverId, Model model) {
            if (currentUser == null) {
                return "redirect:/login";
            }

            User sender = userService.findByUsername(currentUser.getUsername());

            Optional<User> receiverOpt = userService.findById(receiverId);
            if (receiverOpt.isEmpty()) {
                return "redirect:/conversations"; // Rediriger si le destinataire n'est pas trouvé
            }

            User receiver = receiverOpt.get();
            List<Message> messages = messageService.findConversation(sender, receiver);

            // Marquer les messages comme lus
            messageService.markMessagesAsRead(sender, receiver);

            // Ajouter les contacts et le statut des messages non lus pour la barre latérale
            List<User> contacts = messageService.findContacts(sender);
            Map<User, Boolean> contactUnreadStatus = new HashMap<>();
            for (User contact : contacts) {
                boolean hasUnreadMessages = messageService.hasUnreadMessages(contact, sender);
                contactUnreadStatus.put(contact, hasUnreadMessages);

                // Récupérer et définir le dernier message pour chaque contact
                String lastMessage = messageService.getLastMessage(contact, sender);
                contact.setLastMessage(lastMessage);
            }
            for (User user : contacts) {
                boolean isOnline = sessionManagementService.isUserOnline(user); // Check if user is online
                user.setIsOnline(isOnline); // Set the user's online status
            }

            model.addAttribute("contacts", contacts);
            model.addAttribute("contactUnreadStatus", contactUnreadStatus);
            model.addAttribute("receiver", receiver);
            model.addAttribute("messages", messages);
            model.addAttribute("currentUser", sender);

            return "conversation"; // Utiliser un template distinct pour une conversation
        }



    @PostMapping("/{receiverId}")
    public String sendMessage(@AuthenticationPrincipal UserDetails currentUser,
                              @PathVariable Long receiverId,
                              @RequestParam("content") String content,
                              @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        User sender = userService.findByUsername(currentUser.getUsername());
        Optional<User> receiverOpt = userService.findById(receiverId);

        if (receiverOpt.isEmpty()) {
            return "redirect:/messages"; // If receiver not found, redirect to the conversations list
        }

        User receiver = receiverOpt.get();

        // Create and save the message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        // Handle image upload if exists
        if (image != null && !image.isEmpty()) {
            String imageUrl = messageService.saveImage(image);
            message.setImageUrl(imageUrl);
        }

        messageService.saveMessage(message);

        return "redirect:/messages/" + receiverId; // Redirect back to the conversation
    }


}