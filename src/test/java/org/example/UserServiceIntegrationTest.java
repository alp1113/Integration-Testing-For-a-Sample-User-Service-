package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceIntegrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // CASE 1: Test UserService + UserRepository via getUserById()
    // Three behaviors: return valid user, return null, throw exception
    @Test
    void testGetUserById() {
        // Behavior 1: Valid user
        when(userRepository.findById(1)).thenReturn(new User(1, "John Doe", "john@example.com"));
        User user = userService.getUserById(1);
        assertNotNull(user);
        assertEquals("John Doe", user.getName());

        // Behavior 2: No user found
        when(userRepository.findById(999)).thenReturn(null);
        User noUser = userService.getUserById(999);
        assertNull(noUser);

        // Behavior 3: Exception scenario
        when(userRepository.findById(-1)).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> userService.getUserById(-1));
    }

    // CASE 2: Test UserService + EmailService via createUser()
    // Three behaviors: email send true, false, and exception
    @Test
    void testCreateUserSendsEmailToAlice() {
        when(userRepository.save(any(User.class))).thenReturn(new User(2, "Alice", "alice@example.com"));
        when(emailService.sendWelcomeEmail("alice@example.com")).thenReturn(true);

        userService.createUser("Alice", "alice@example.com");
        verify(emailService).sendWelcomeEmail("alice@example.com");
    }

    @Test
    void testCreateUserSendsEmailToBob() {
        when(userRepository.save(any(User.class))).thenReturn(new User(3, "Bob", "bob@example.com"));
        when(emailService.sendWelcomeEmail("bob@example.com")).thenReturn(true);

        userService.createUser("Bob", "bob@example.com");
        verify(emailService).sendWelcomeEmail("bob@example.com");
    }

    // CASE 3: Test UserService + UserRepository via getUsers()
    // Three behaviors: empty list, one user, multiple users
    @Test
    void testGetUsers() {
        // Behavior 1: Empty list
        when(userRepository.findAll()).thenReturn(List.of());
        List<User> emptyList = userService.getUsers();
        assertTrue(emptyList.isEmpty());

        // Behavior 2: One user
        when(userRepository.findAll()).thenReturn(List.of(new User(3, "Charlie", "charlie@example.com")));
        List<User> singleList = userService.getUsers();
        assertEquals(1, singleList.size());
        assertEquals("Charlie", singleList.get(0).getName());

        // Behavior 3: Multiple users
        when(userRepository.findAll()).thenReturn(List.of(
                new User(4, "Dave", "dave@example.com"),
                new User(5, "Eve", "eve@example.com")));
        List<User> multiList = userService.getUsers();
        assertEquals(2, multiList.size());
        assertEquals("Dave", multiList.get(0).getName());
        assertEquals("Eve", multiList.get(1).getName());
    }

    // CASE 4: Test UserService + UserRepository via updateUser()
    // Three behaviors: successful update, user not found (null), exception on update
    @Test
    void testUpdateUser() {
        // Behavior 1: Successful update
        when(userRepository.findById(10)).thenReturn(new User(10, "OldName", "old@example.com"));
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User updated = userService.updateUser(10, "NewName", "new@example.com");
        assertNotNull(updated);
        assertEquals("NewName", updated.getName());

        // Behavior 2: User not found
        when(userRepository.findById(9999)).thenReturn(null);
        User notFound = userService.updateUser(9999, "NoUser", "no@example.com");
        assertNull(notFound);

        // Behavior 3: Exception on update
        when(userRepository.findById(11)).thenReturn(new User(11, "Temp", "temp@example.com"));
        when(userRepository.update(any(User.class))).thenThrow(new RuntimeException("Update failed"));
        assertThrows(RuntimeException.class, () -> userService.updateUser(11, "FailName", "fail@example.com"));
    }

    // CASE 5: Test UserService + EmailService via sendBulkWelcomeEmailsToAllUsers()
    // Three behaviors: send bulk returns true, returns false, throws exception
    @Test
    void testSendBulkWelcomeEmailsToAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(20, "Frank", "frank@example.com"),
                new User(21, "Grace", "grace@example.com")
        ));

        // Behavior 1: Bulk send success
        when(emailService.sendBulkWelcomeEmails(List.of("frank@example.com", "grace@example.com"))).thenReturn(true);
        assertTrue(userService.sendBulkWelcomeEmailsToAllUsers());

        // Behavior 2: Bulk send fails
        when(emailService.sendBulkWelcomeEmails(anyList())).thenReturn(false);
        assertFalse(userService.sendBulkWelcomeEmailsToAllUsers());

        // Behavior 3: Exception thrown
        when(emailService.sendBulkWelcomeEmails(anyList())).thenThrow(new RuntimeException("Bulk send error"));
        assertThrows(RuntimeException.class, () -> userService.sendBulkWelcomeEmailsToAllUsers());
    }
}