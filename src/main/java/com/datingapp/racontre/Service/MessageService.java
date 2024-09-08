package com.datingapp.racontre.Service;

import com.datingapp.racontre.Model.Message;
import com.datingapp.racontre.Model.User;
import com.datingapp.racontre.Repository.MessageRepository;
import com.datingapp.racontre.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MessageService {

    private final String uploadDir = "uploads/";

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Save a new message
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    // Find conversation between two users
    public List<Message> findConversation(User sender, User receiver) {
        return messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(sender, receiver, sender, receiver);
    }


    // Get a list of users the current user has contacted

    public List<User> findContacts(User user) {
        List<Message> messages = messageRepository.findBySenderOrReceiver(user, user);
        for (Message m : messages) {
            System.out.println("Message: " + m.getContent() + " Sender: " + m.getSender().getUsername() + " Receiver: " + m.getReceiver().getUsername());
        }

        return messages.stream()
                .flatMap(m -> Stream.of(m.getSender(), m.getReceiver()))
                .filter(u -> !u.equals(user))
                .distinct()
                .collect(Collectors.toList());

    }


    // Save the uploaded image
    public String saveImage(MultipartFile image) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
         Path uploadPath = Paths.get("/uploads/");


        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName; // Store only the filename in the database
    }




    public void markMessagesAsRead(User sender, User receiver) {
        List<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(sender, receiver, sender, receiver);
        for (Message message : messages) {
            if (!message.isRead() && message.getReceiver().equals(receiver)) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    public boolean hasUnreadMessages(User sender, User receiver) {
        return messageRepository.existsBySenderAndReceiverAndIsReadFalse(sender, receiver);
    }

    public LocalDateTime getLastMessageTime(User contact) {
        Message lastMessage = messageRepository.findTopBySenderOrReceiverOrderByTimestampDesc(contact, contact);
        return lastMessage != null ? lastMessage.getTimestamp() : null;
    }

    public String getLastMessage(User contact, User currentUser) {
        // Find the last message between the contact and the current user
        Message lastMessage = messageRepository.findFirstBySenderAndReceiverOrReceiverAndSenderOrderByTimestampDesc(contact, currentUser, contact, currentUser);

        if (lastMessage != null) {
            return lastMessage.getContent();  // Return the content of the last message
        } else {
            return "";  // No messages found, return an empty string
        }
    }

    public Message getLastMessageBetween(User sender, User receiver) {
        return messageRepository.findFirstBySenderAndReceiverOrderByTimestampDesc(sender, receiver);
    }



}
