package com.example.teamalfred.controllers.AiFeatures;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException; // Import it

/**
 * Represents a response object from the Ollama API.
 * Contains fields that map to the JSON response structure.
 */
public class OllamaResponse {

    /** The name of the model that generated the response. */
    public String model;
    /** The timestamp of when the response was created. */
    public String created_at;
    /** The main content of the response from the Ollama API. */
    public String response;
    /** Indicates if the response generation is complete (true if so, often a boolean represented as a string or absent). */
    public String done; // Or boolean if the API/Gson handles it. Assuming String as per initial code.


    /**
     * Gets the main content of the response from the Ollama API.
     * @return The response string.
     */
    public String getResponse() {
        return response;
    }

    /**
     * Deserializes a JSON string into an {@code OllamaResponse} object.
     * For details on the JSON response structure, refer to the
     * <a href="https://github.com/ollama/ollama/blob/main/docs/api.md">Ollama API documentation</a>.
     *
     * @param body The JSON string received from the Ollama API.
     * @return A new {@code OllamaResponse} instance populated from the JSON string.
     * @throws JsonSyntaxException if the input body is null, empty, or malformed JSON.
     */
    public static OllamaResponse fromJson(String body) {
        if (body == null || body.isEmpty()) {
            // You can choose to trim if you also want to consider strings with only whitespace as invalid
            // if (body == null || body.trim().isEmpty()) {
            throw new JsonSyntaxException("Input JSON string cannot be null or empty for OllamaResponse deserialization.");
        }
        Gson gson = new Gson();
        // This will still throw a JsonSyntaxException if 'body' is not valid JSON (e.g., "{}a")
        // but the check above handles the specific empty/null case.
        OllamaResponse response = gson.fromJson(body, OllamaResponse.class);
        return response;
    }
}