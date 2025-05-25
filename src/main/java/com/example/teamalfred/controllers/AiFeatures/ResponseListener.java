package com.example.teamalfred.controllers.AiFeatures;

/**
 * A functional interface for receiving asynchronous responses from the Ollama API.
 * Implementations of this interface can define custom actions to be performed
 * once an {@link OllamaResponse} is available.
 */
public interface ResponseListener {

    /**
     * Called when an {@link OllamaResponse} has been successfully received.
     *
     * @param response The {@link OllamaResponse} object containing the data
     * returned by the Ollama API.
     */
    public void onResponseReceived(OllamaResponse response);
}