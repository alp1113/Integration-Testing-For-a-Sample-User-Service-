package org.example;


import java.util.List;

public class UserRepository {

    public User findById(int id) {
        // Simulating DB lookup
        return null;
    }

    public User save(User user) {
        // Simulate saving user
        return user;
    }

    public List<User> findAll() {
        // Return all users (simulate DB)
        return List.of();
    }

    public User update(User user) {
        // Simulate update
        // In a real scenario, if user doesn't exist, return null
        return user;
    }
}