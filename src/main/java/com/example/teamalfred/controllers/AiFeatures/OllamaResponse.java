package com.example.teamalfred.controllers.AiFeatures;

import com.google.gson.Gson;

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
     */
    public static OllamaResponse fromJson(String body) {
        //for documentation of how to decode JSON response https://github.com/ollama/ollama/blob/main/docs/api.md
        Gson gson = new Gson();
        OllamaResponse response = gson.fromJson(body, OllamaResponse.class);
        return response;
    }
}