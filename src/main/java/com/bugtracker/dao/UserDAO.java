package com.bugtracker.dao;

import com.bugtracker.model.User;
import java.util.List;

/**
 * Data Access Object (DAO) interface for User entity operations.
 * Defines standard CRUD operations for User Management.
 */
public interface UserDAO {

    /**
     * Inserts a new user into the database.
     *
     * @param user User object containing username/name, email, password, and role
     * @return true if insertion was successful, false otherwise
     */
    boolean addUser(User user);

    /**
     * Retrieves all users registered in the database.
     *
     * @return List of all User objects
     */
    List<User> getAllUsers();

    /**
     * Finds and retrieves a user by their unique primary key ID.
     *
     * @param id the user ID to search for
     * @return User object if found, or null if no user matches the ID
     */
    User getUserById(int id);

    /**
     * Updates an existing user's details (username/name, email, password, role).
     *
     * @param user User object with updated information and valid ID
     * @return true if update was successful, false otherwise
     */
    boolean updateUser(User user);

    /**
     * Deletes a user by their unique ID.
     *
     * @param id the ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteUser(int id);
}
