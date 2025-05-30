/**
 * AiQuizController manages the logic for generating AI-powered quizzes.
 * 
 * This controller:
 * - Accepts a topic input from the user
 * - Sends it to an AI backend to retrieve a list of quiz questions
 * - Parses and displays the quiz questions in the output area
 * - Allows users to export the quiz as a file
 *
 * It inherits shared behaviors from BaseAiController to manage UI state and alerts.
 */
package com.example.teamalfred.controllers.AiFeatures;

import com.example.teamalfred.controllers.SwitchSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import java.io.IOException;

/**
 * Controller for the AiQuiz scene.
 * Handles generating a quiz from a topic using a local LLM.
 */
public class AiQuizController extends BaseAiController{

    /** Text field for user to input the quiz topic. */
    @FXML
    private TextField topicTextField;

    /** Text area to display the generated quiz and answers. */
    @FXML
    private TextArea quizOutputArea;

    /**
     * Called when the "Generate Quiz" button is clicked.
     * Retrieves the topic from the user, constructs a prompt, and fetches
     * a quiz from the LLM, displaying it in the output area.
     * @param event The action event triggered by the button click.
     */
    @FXML
    public void onGenerateQuizClicked(ActionEvent event) {
        String userInput = topicTextField.getText();
        String fullPrompt = "Create a five-question multiple choice quiz on the following topic: "
                + userInput
                + ". The quiz should be understandable by high school students. "
                + "Each question must have four answer options labeled A through D. "
                + "At the end, include an answer key listing the correct answer for each question. "
                + "Only include the quiz and the answer key in your response.";

        outputArea.setText("Generating AI quiz, please wait...");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Generating...");
        alert.setHeaderText(null);
        alert.setContentText("Please wait while your AI quiz is being generated. " +
                "This might take one or two minutes depending on your hardware.");
        alert.show();

        new Thread(() -> {
            try {
                String response = sendRequestToLLM(fullPrompt);
                Platform.runLater(() -> {
                    alert.close();
                    outputArea.setText(response);
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    alert.close();
                    displayError("Failed to generate quiz: " + e.getMessage());
                });
            }
        }).start();
    }

}