package com.example.teamalfred.database;

import java.util.List;
public interface IUserDAO {

    public interface IContactDAO {
        /**
         * Adds a new contact to the database.
         * @param contact The contact to add.
         */
        public void addContact(User contact);
        /**
         * Updates an existing contact in the database.
         * @param contact The contact to update.
         */
        public void updateContact(User contact);
        /**
         * Deletes a contact from the database.
         * @param contact The contact to delete.
         */
        public void deleteContact(User contact);
        /**
         * Retrieves a contact from the database.
         * @param id The id of the contact to retrieve.
         * @return The contact with the given id, or null if not found.
         */
        public User getContact(int id);
        /**
         * Retrieves all contacts from the database.
         * @return A list of all contacts in the database.
         */
        public List<User> getAllContacts();
    }
}
