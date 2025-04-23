package com.example.teamalfred.database;

import java.util.List;
    public interface IUserDAO {
        /**
         * Adds a new user to the database.
         * @param user The user to add.
         */
        public void addUser(User user);
        /**
         * Updates an existing contact in the database.
         * @param contact The contact to update.
         */
        public void updateUser(User contact);
        /**
         * Deletes a contact from the database.
         * @param contact The contact to delete.
         */
        public void deleteUser(User contact);

        public User getUser(String email);

    }
//TODO: This class defines the contract for all User Data Access Object operations
// It lists what you can do with user data, not how.
