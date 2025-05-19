package com.example.teamalfred.controllers.AiFeatures;

import com.example.teamalfred.controllers.SwitchSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for the AiQuiz scene.
 * Handles generating a quiz from a topic using a local LLM.
 */
public class AiQuizController {

    @FXML
    private TextField topicTextField;

    @FXML
    private TextArea quizOutputArea;

    /**
     * Called when the "Generate Quiz" button is clicked.
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

        class MyResponseListener implements ResponseListener {
            @Override
            public void onResponseReceived(OllamaResponse response) {
                quizOutputArea.setText(response.getResponse());
            }
        }

        String apiURL = "http://127.0.0.1:11434/api/generate/";
        String model = "gemma3"; // Replace with your actual model if different

        OllamaResponseFetcher fetcher = new OllamaResponseFetcher(apiURL);
        fetcher.fetchAsynchronousOllamaResponse(model, fullPrompt, new MyResponseListener());
    }

    /**
     * Called when the "Back to Dashboard" button is clicked.
     */
    @FXML
    public void onBackToDashboardClicked(ActionEvent event) {
        SwitchSceneController switcher = new SwitchSceneController();
        switcher.switchScene(event, "/com/example/teamalfred/Dashboard.fxml");
    }
}
