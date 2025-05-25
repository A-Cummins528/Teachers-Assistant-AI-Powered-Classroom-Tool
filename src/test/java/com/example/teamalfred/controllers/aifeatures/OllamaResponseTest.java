package com.example.teamalfred.controllers.aifeatures;

import com.example.teamalfred.controllers.AiFeatures.OllamaResponse;
import com.google.gson.JsonSyntaxException; // For testing malformed JSON behavior
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link OllamaResponse} class.
 * Focuses on JSON deserialization and basic getter functionality.
 */
public class OllamaResponseTest {

    /**
     * Tests that a valid JSON string is correctly deserialized into an OllamaResponse object
     * with all fields populated as expected.
     */
    @Test
    void testFromJson_validFullJsonResponse_parsesCorrectly() {
        String validJson = "{\"model\":\"gemma:2b\",\"created_at\":\"2024-05-24T10:30:00.12345Z\",\"response\":\"This is the AI response content.\",\"done\":\"true\"}";
        OllamaResponse response = OllamaResponse.fromJson(validJson);

        assertNotNull(response, "OllamaResponse object should not be null after parsing valid JSON.");
        assertEquals("gemma:2b", response.model, "Model field not parsed correctly.");
        assertEquals("2024-05-24T10:30:00.12345Z", response.created_at, "Created_at field not parsed correctly.");
        assertEquals("This is the AI response content.", response.response, "Response field not parsed correctly.");
        assertEquals("true", response.done, "Done field not parsed correctly.");
    }

    /**
     * Tests deserialization with a JSON string where some fields might be present but empty,
     * or where the 'done' field might represent an ongoing stream (though 'done' as String "false" is more likely).
     * This also tests if a typical successful, non-empty response parses.
     */
    @Test
    void testFromJson_validPartialInfo_parsesCorrectly() {
        String partialJson = "{\"model\":\"llama3\",\"created_at\":\"2024-05-24T11:00:00Z\",\"response\":\"Another response.\",\"done\":\"false\"}";
        OllamaResponse response = OllamaResponse.fromJson(partialJson);

        assertNotNull(response);
        assertEquals("llama3", response.model);
        assertEquals("2024-05-24T11:00:00Z", response.created_at);
        assertEquals("Another response.", response.response);
        assertEquals("false", response.done);
    }

    /**
     * Tests that attempting to deserialize a malformed JSON string throws a JsonSyntaxException.
     */
    @Test
    void testFromJson_malformedJson_throwsJsonSyntaxException() {
        String malformedJson = "{\"model\":\"gemma:2b\",\"created_at\":\"2024-05-24T10:30:00Z\", --malformed-- \"response\":\"AI content.\",\"done\":\"true\"}";
        assertThrows(JsonSyntaxException.class, () -> {
            OllamaResponse.fromJson(malformedJson);
        }, "Parsing malformed JSON should throw JsonSyntaxException.");
    }

    /**
     * Tests that attempting to deserialize an empty JSON string results in an exception.
     */
    @Test
    void testFromJson_emptyJsonString_throwsJsonSyntaxException() {
        String emptyJson = "";
        assertThrows(JsonSyntaxException.class, () -> {
            OllamaResponse.fromJson(emptyJson);
        }, "Parsing an empty JSON string should throw JsonSyntaxException.");
    }

    /**
     * Tests that attempting to deserialize a JSON string representing an empty object
     * results in an OllamaResponse object with null fields (as per Gson's default behavior).
     */
    @Test
    void testFromJson_emptyJsonObject_parsesToNullFields() {
        String emptyJsonObject = "{}";
        OllamaResponse response = OllamaResponse.fromJson(emptyJsonObject);

        assertNotNull(response, "Response object should not be null even for empty JSON object.");
        assertNull(response.model, "Model field should be null for empty JSON object.");
        assertNull(response.created_at, "Created_at field should be null for empty JSON object.");
        assertNull(response.response, "Response field should be null for empty JSON object.");
        assertNull(response.done, "Done field should be null for empty JSON object.");
    }

    /**
     * Tests the {@link OllamaResponse#getResponse()} getter method.
     * It ensures that the getter returns the correct value of the 'response' field.
     */
    @Test
    void testGetResponse_returnsCorrectResponseField() {
        OllamaResponse ollamaResponse = new OllamaResponse();
        String expectedResponseText = "This is a test response.";
        ollamaResponse.response = expectedResponseText; // Directly set the public field for testing

        assertEquals(expectedResponseText, ollamaResponse.getResponse(), "getResponse() should return the value of the response field.");
    }

    /**
     * Tests the {@link OllamaResponse#getResponse()} getter method when the 'response' field is null.
     */
    @Test
    void testGetResponse_whenResponseFieldIsNull_returnsNull() {
        OllamaResponse ollamaResponse = new OllamaResponse();
        ollamaResponse.response = null; // Explicitly set to null

        assertNull(ollamaResponse.getResponse(), "getResponse() should return null if the response field is null.");
    }
}