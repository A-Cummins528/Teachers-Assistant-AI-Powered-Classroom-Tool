/**
 * AiLessonPlansController oversees generating educational lesson plans using AI.
 * 
 * This controller:
 * - Accepts a user-defined topic
 * - Sends a request to an AI backend to generate a full lesson plan
 * - Displays the result in a text area
 * - Allows exporting the lesson plan to a file
 *
 * It utilizes a background thread to avoid UI freezing during network operations
 * and inherits BaseAiController functionalities to manage shared behavior.
 */
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller for generating and exporting AI-generated lesson plans based on user input topics.
 * Connects to a local Ollama LLM endpoint to fetch generated content.
 */
public class AiLessonPlansController extends BaseAiController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Handles the generation of a lesson plan when the user clicks the generate button.
     * Shows an alert dialog while the task is running and updates the UI with the result.
     */
    @FXML
    public void handleGenerateLessonPlan() {
        String topic = topicTextField.getText().trim();
        if (topic.isEmpty()) {
            outputArea.setText("Please enter a topic.");
            return;
        }

        Platform.runLater(() -> outputArea.setText("Generating lesson plan, please wait..."));

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generating...");
            alert.setHeaderText(null);
            alert.setContentText("Please wait while your lesson plan is being generated. " +
                    "This might take one or two minutes depending on your hardware.");
            alert.show();

            executor.submit(() -> {
                try {
                    String prompt = "Generate a detailed lesson plan about: " + topic;
                    String lessonPlan = sendRequestToLLM(prompt);
                    Platform.runLater(() -> {
                        alert.close();
                        if (lessonPlan == null || lessonPlan.trim().isEmpty()) {
                            outputArea.setText("Failed to generate a lesson plan. Please try again.");
                        } else {
                            outputArea.setText(lessonPlan);
                        }
                    });
                } catch (IOException e) {
                    Platform.runLater(() -> displayError("Failed to generate lesson plan: " + e.getMessage()));
                    Platform.runLater(() -> {
                        alert.close();
                        outputArea.setText("Error: " + e.getMessage());
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
        String lessonText = outputArea.getText();
        if (lessonText.isEmpty()) {
            outputArea.setText("Nothing to export. Please generate a lesson plan first.");
            return;
        }

        try {
            String userHome = System.getProperty("user.home");
            File file = new File(userHome + "/Downloads/LessonPlan.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(lessonText);
            }
            outputArea.setText("Lesson plan saved to Downloads folder.");
        } catch (IOException e) {
            Platform.runLater(() -> displayError("Failed to export lesson plan: " + e.getMessage()));
            outputArea.setText("Failed to save file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
