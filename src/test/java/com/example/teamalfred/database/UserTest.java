package com.example.teamalfred.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link User} class.
 * Covers validation, edge cases, and utility methods.
 */
public class UserTest {

    // --------------------------------------------------
    //                ID Field Tests
    // --------------------------------------------------

    @Test
    public void setId_ValidId_SetsCorrectly() {
        User user = new User();
        user.setId(42);
        assertEquals(42, user.getId());
    }

    @Test
    public void setId_NegativeId_SetsCorrectly() {
        User user = new User();
        user.setId(-1);
        assertEquals(-1, user.getId());
    }

    // --------------------------------------------------
    //             First Name Field Tests
    // --------------------------------------------------

    @Test
    public void setFirstName_ValidName_SetsCorrectly() {
        User user = new User();
        user.setFirstName("John");
        assertEquals("John", user.getFirstName());
    }

    @Test
    public void setFirstName_WithSymbols_SetsCorrectly() {
        User user = new User();
        user.setFirstName("@Jane#");
        assertEquals("@Jane#", user.getFirstName());
    }

    @Test
    public void setFirstName_WithWhitespace_TrimsCorrectly() {
        User user = new User();
        user.setFirstName("   Alice   ");
        assertEquals("Alice", user.getFirstName());
    }

    @Test
    public void setFirstName_NullOrEmpty_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setFirstName(null));
        assertThrows(IllegalArgumentException.class, () -> user.setFirstName(""));
        assertThrows(IllegalArgumentException.class, () -> user.setFirstName("   "));
    }

    // --------------------------------------------------
    //             Last Name Field Tests
    // --------------------------------------------------

    @Test
    public void setLastName_ValidName_SetsCorrectly() {
        User user = new User();
        user.setLastName("Doe");
        assertEquals("Doe", user.getLastName());
    }

    @Test
    public void setLastName_WithSymbols_SetsCorrectly() {
        User user = new User();
        user.setLastName("O'Connor");
        assertEquals("O'Connor", user.getLastName());
    }

    @Test
    public void setLastName_NullOrEmpty_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setLastName(null));
        assertThrows(IllegalArgumentException.class, () -> user.setLastName(""));
    }

    // --------------------------------------------------
    //                Email Field Tests
    // --------------------------------------------------

    @Test
    public void setEmail_ValidEmail_SetsCorrectly() {
        User user = new User();
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    public void setEmail_WithSymbols_SetsCorrectly() {
        User user = new User();
        user.setEmail("first.last+label@example-domain.com");
        assertEquals("first.last+label@example-domain.com", user.getEmail());
    }

    @Test
    public void setEmail_InvalidFormat_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setEmail("bad-email"));
        assertThrows(IllegalArgumentException.class, () -> user.setEmail("no-at-symbol.com"));
        assertThrows(IllegalArgumentException.class, () -> user.setEmail("no.domain@"));
    }

    @Test
    public void setEmail_NullOrEmpty_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(null));
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(""));
    }

    // --------------------------------------------------
    //              Mobile Field Tests
    // --------------------------------------------------

    @Test
    public void setMobile_ValidMobile_SetsCorrectly() {
        User user = new User();
        user.setMobile("+61412345678");
        assertEquals("+61412345678", user.getMobile());
    }

    @Test
    public void setMobile_WithWhitespace_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setMobile("  +61412345678  "));
    }

    @Test
    public void setMobile_InvalidFormat_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setMobile("123ABC"));
        assertThrows(IllegalArgumentException.class, () -> user.setMobile("!@#$%^&*()"));
        assertThrows(IllegalArgumentException.class, () -> user.setMobile("123"));
    }

    @Test
    public void setMobile_NullOrEmpty_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setMobile(null));
        assertThrows(IllegalArgumentException.class, () -> user.setMobile(""));
    }

    // --------------------------------------------------
    //             Password Field Tests
    // --------------------------------------------------

    @Test
    public void setPassword_ValidPassword_HashesAndStores() {
        User user = new User();
        user.setPassword("mySecretPassword");
        assertNotNull(user.getPassword());
        assertTrue(user.checkPassword("mySecretPassword"));
    }

    @Test
    public void setPassword_WithSymbols_HashesCorrectly() {
        User user = new User();
        user.setPassword("P@$$w0rd!#%");
        assertTrue(user.checkPassword("P@$$w0rd!#%"));
    }

    @Test
    public void setPassword_NullOrEmpty_ThrowsException() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.setPassword(null));
        assertThrows(IllegalArgumentException.class, () -> user.setPassword(""));
    }

    @Test
    public void checkPassword_NullPassword_ReturnsFalse() {
        User user = new User();
        user.setPassword("abc123");
        assertFalse(user.checkPassword(null));
    }

    @Test
    public void setPersistedPassword_SetsDirectly() {
        User user = new User();
        String hashedPassword = "$2a$10$7EqJtq98hPqEX7fNZaFWoO.";
        user.setPersistedPassword(hashedPassword);
        assertEquals(hashedPassword, user.getPassword());
    }

    // --------------------------------------------------
    //             Full Name Method Tests
    // --------------------------------------------------

    @Test
    public void getFullName_ConcatenatesCorrectly() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");
        assertEquals("Jane Smith", user.getFullName());
    }

    @Test
    public void getFullName_WithSymbols_ConcatenatesCorrectly() {
        User user = new User();
        user.setFirstName("@John");
        user.setLastName("O'Connor");
        assertEquals("@John O'Connor", user.getFullName());
    }

    // --------------------------------------------------
    //                toString Method Tests
    // --------------------------------------------------

    @Test
    public void toString_IncludesExpectedFields() {
        User user = new User("John", "Doe", "john@example.com",
                "+61412345678", "password123", "teacher");
        user.setId(1);
        String result = user.toString();
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("firstName='John'"));
        assertFalse(result.contains("password"));
    }

    // --------------------------------------------------
    //          hashCode and equals Method Tests
    // --------------------------------------------------

    @Test
    public void hashCode_SameId_ReturnsSameHashCode() {
        User user1 = new User();
        user1.setId(100);

        User user2 = new User();
        user2.setId(100);

        assertEquals(user1.hashCode(), user2.hashCode(), "Users with the same ID should have the same hash code.");
    }

    @Test
    public void hashCode_DifferentId_MayReturnDifferentHashCode() {
        User user1 = new User();
        user1.setId(100);

        User user2 = new User();
        user2.setId(200);

        assertNotEquals(user1.hashCode(), user2.hashCode(), "Users with different IDs should likely have different hash codes.");
    }

    @Test
    public void equals_SameId_ReturnsTrue() {
        User user1 = new User();
        user1.setId(5);

        User user2 = new User();
        user2.setId(5);

        assertEquals(user1, user2);
    }

    @Test
    public void equals_DifferentId_ReturnsFalse() {
        User user1 = new User();
        user1.setId(5);

        User user2 = new User();
        user2.setId(10);

        assertNotEquals(user1, user2);
    }

    @Test
    public void equals_NullOrDifferentType_ReturnsFalse() {
        User user = new User();
        user.setId(5);

        assertNotEquals(user, null);
        assertNotEquals(user, "NotAUserObject");
    }

    @Test
    public void equals_ZeroIds_ReturnsFalse() {
        User user1 = new User();
        user1.setId(0);

        User user2 = new User();
        user2.setId(0);

        assertNotEquals(user1, user2, "Two users with zero IDs should not be considered equal.");
    }

    @Test
    public void setEmail_SetsCorrectly() {
        User user = new User();
        user.setEmail("felix@example.com");
        assertEquals("felix@example.com", user.getEmail());
    }

    @Test
    public void setFirstName_SetsCorrectly() {
        User user = new User();
        user.setFirstName("Felix");
        assertEquals("Felix", user.getFirstName());
    }
    @Test
    public void setLastName_SetsCorrectly() {
        User user = new User();
        user.setLastName("Felix");
        assertEquals("Felix", user.getLastName());
    }


}
