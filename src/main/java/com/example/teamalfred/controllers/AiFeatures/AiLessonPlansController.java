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

public class AiLessonPlansController {

    @FXML
    private TextField topicInput;
    @FXML
    private TextArea lessonPlanOutput;
    @FXML
    private Button generateButton;
    @FXML
    private Button exportButton;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @FXML
    public void handleGenerateLessonPlan() {
        String topic = topicInput.getText().trim();
        if (topic.isEmpty()) {
            lessonPlanOutput.setText("Please enter a topic.");
            return;
        }

        // Show loading text in the TextArea
        Platform.runLater(() -> lessonPlanOutput.setText("Generating lesson plan, please wait..."));

        // Optionally show a popup
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generating...");
            alert.setHeaderText(null);
            alert.setContentText("Please wait while your lesson plan is being generated.");
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

    public static class OllamaPrompt {
        public String model;
        public String prompt;
        public boolean stream;

        public OllamaPrompt(String model, String prompt, boolean stream) {
            this.model = model;
            this.prompt = prompt;
            this.stream = stream;
        }
    }

    public static class OllamaResponse {
        public String response;
        public String getResponse() {
            return response;
        }
    }
}
