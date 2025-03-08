package org.example;


import java.util.List;
import java.util.stream.Collectors;

public class UserService {

    private UserRepository userRepository;
    private EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User getUserById(int id) {
        return userRepository.findById(id);
    }

    public User createUser(String name, String email) {
        User user = new User(0, name, email);
        User saved = userRepository.save(user);
        emailService.sendWelcomeEmail(saved.getEmail());
        return saved;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User updateUser(int id, String newName, String newEmail) {
        User existing = userRepository.findById(id);
        if (existing == null) {
            return null;
        }
        existing.setName(newName);
        existing.setEmail(newEmail);
        return userRepository.update(existing);
    }

    public boolean sendBulkWelcomeEmailsToAllUsers() {
        List<User> users = userRepository.findAll();
        List<String> emails = users.stream().map(User::getEmail).collect(Collectors.toList());
        return emailService.sendBulkWelcomeEmails(emails);
    }
}