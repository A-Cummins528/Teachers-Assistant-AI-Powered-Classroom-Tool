/**
 * BaseAiController provides common functionality for AI-related controllers,
 * including handling prompt formatting, displaying alerts, and managing UI component states.
 *
 * This abstract class is designed to be extended by specific AI controllers,
 * such as those responsible for generating quizzes, lesson plans, or tutoring feedback.
 *
 * It promotes code reuse and consistent UI interactions across different AI features.
 */
package com.example.teamalfred.controllers.AiFeatures;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abstract base controller providing common functionality for AI-based JavaFX controllers.
 * Includes shared UI components and utility methods for interacting with a local LLM.
 */
public abstract class BaseAiController {

    /** Input field for entering the topic or prompt. */
    @FXML
    protected TextField topicTextField;

    /** Output area for displaying the AI-generated response. */
    @FXML
    protected TextArea outputArea;

    /**
     * Sends a prompt to the local Ollama LLM endpoint and returns the generated response.
     *
     * @param prompt The prompt string to send to the LLM.
     * @return The concatenated response text from the LLM.
     * @throws IOException if the request or response encounters an I/O error.
     */
    protected String sendRequestToLLM(String prompt) throws IOException {
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);

        String jsonInputString = "{\"model\": \"gemma3\", \"prompt\": \"" + prompt + "\"}";
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(new Gson().fromJson(responseLine, OllamaResponse.class).response);
            }
        }
        return response.toString();
    }

    /**
     * Displays an error message in a JavaFX alert dialog.
     * This method is executed on the JavaFX Application thread.
     *
     * @param message The message to display in the alert.
     */
    protected void displayError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.show();
        });
    }
}
