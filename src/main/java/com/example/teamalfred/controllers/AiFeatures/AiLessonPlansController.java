package com.example.teamalfred.controllers.AiFeatures;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for generating and exporting AI-generated lesson plans based on user input topics.
 * Connects to a local Ollama LLM endpoint to fetch generated content.
 */
public class AiLessonPlansController {

    @FXML
    private TextField topicInput;

    @FXML
    private TextArea lessonPlanOutput;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Handles the generation of a lesson plan when the user clicks the generate button.
     * Shows an alert dialog while the task is running and updates the UI with the result.
     */
    @FXML
    public void handleGenerateLessonPlan() {
        String topic = topicInput.getText().trim();
        if (topic.isEmpty()) {
            lessonPlanOutput.setText("Please enter a topic.");
            return;
        }

        Platform.runLater(() -> lessonPlanOutput.setText("Generating lesson plan, please wait..."));

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generating...");
            alert.setHeaderText(null);
            alert.setContentText("Please wait while your lesson plan is being generated. " +
                    "This might take one or two minutes depending on your hardware.");
            alert.show();

            executor.submit(() -> {
                try {
                    String lessonPlan = fetchLessonPlanFromOllama(topic);
                    Platform.runLater(() -> {
                        alert.close();
                        if (lessonPlan == null || lessonPlan.trim().isEmpty()) {
                            lessonPlanOutput.setText("Failed to generate a lesson plan. Please try again.");
                        } else {
                            lessonPlanOutput.setText(lessonPlan);
                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        alert.close();
                        lessonPlanOutput.setText("Error: " + e.getMessage());
                    });
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * Handles exporting the currently displayed lesson plan to a text file in the user's Downloads folder.
     */
    @FXML
    public void handleExportLessonPlan() {
        String lessonText = lessonPlanOutput.getText();
        if (lessonText.isEmpty()) {
            lessonPlanOutput.setText("Nothing to export. Please generate a lesson plan first.");
            return;
        }

        try {
            String userHome = System.getProperty("user.home");
            Path downloadPath = Paths.get(userHome, "Downloads", "LessonPlan.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(downloadPath.toFile()))) {
                writer.write(lessonText);
            }
            lessonPlanOutput.setText("Lesson plan saved to Downloads folder.");
        } catch (IOException e) {
            lessonPlanOutput.setText("Failed to save file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a request to the local Ollama endpoint to generate a lesson plan based on the provided topic.
     *
     * @param topic The topic the user entered.
     * @return A generated lesson plan string or an empty string on failure.
     * @throws IOException if the network connection or data transfer fails.
     */
    private String fetchLessonPlanFromOllama(String topic) throws IOException {
        URL url = new URL("http://127.0.0.1:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        String payload = new Gson().toJson(new OllamaPrompt("gemma3", "Generate a detailed lesson plan about: " + topic, false));

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
        }

        OllamaResponse ollamaResponse = new Gson().fromJson(response.toString(), OllamaResponse.class);
        if (ollamaResponse == null || ollamaResponse.getResponse() == null) {
            return "";
        }
        return ollamaResponse.getResponse();
    }

    /**
     * Inner class representing the JSON payload sent to the Ollama API.
     */
    public static class OllamaPrompt {
        public String model;
        public String prompt;
        public boolean stream;

        /**
         * Constructs an OllamaPrompt object.
         *
         * @param model  the LLM model name (e.g., \"gemma3\")
         * @param prompt the prompt to send to the model
         * @param stream whether to stream the response
         */
        public OllamaPrompt(String model, String prompt, boolean stream) {
            this.model = model;
            this.prompt = prompt;
            this.stream = stream;
        }
    }

    /**
     * Inner class representing the JSON response returned by the Ollama API.
     */
    public static class OllamaResponse {
        public String response;

        /**
         * Returns the generated response content.
         *
         * @return the response text
         */
        public String getResponse() {
            return response;
        }
    }
}
