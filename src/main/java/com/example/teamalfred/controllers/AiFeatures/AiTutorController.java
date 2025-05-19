package com.example.teamalfred.controllers.AiFeatures;

import com.example.teamalfred.controllers.SwitchSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class AiTutorController implements Initializable {

    @FXML private TextField topicTextField;
    @FXML private TextArea promptOutputArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String fullPrompt = "You are an AI assistant designed to help high school students and teachers. " +
                "All responses must be age-appropriate, family-friendly, and suitable for an educational environment. " +
                "Do not use profanity, adult themes, or controversial political opinions. " +
                "Explanations should be clear, concise, and accessible to students aged 13–18. " +
                "Avoid slang and complex jargon unless clearly defined. " +
                "When assisting teachers, maintain a professional tone. " +
                "Do not generate or promote harmful, deceptive, or unethical content. " +
                "If a query is inappropriate, respond with a polite refusal and a brief reason.";

        String apiURL = "http://127.0.0.1:11434/api/generate/";
        String model = "gemma3";

        OllamaResponseFetcher fetcher = new OllamaResponseFetcher(apiURL);
        fetcher.fetchAsynchronousOllamaResponse(model, fullPrompt, response ->
                promptOutputArea.setText(response.getResponse()));
    }

    @FXML
    public void onGeneratePromptClicked(ActionEvent event) {
        String userInput = topicTextField.getText();

        String apiURL = "http://127.0.0.1:11434/api/generate/";
        String model = "gemma3";

        OllamaResponseFetcher fetcher = new OllamaResponseFetcher(apiURL);
        fetcher.fetchAsynchronousOllamaResponse(model, userInput, response ->
                promptOutputArea.setText(response.getResponse()));
    }

    @FXML
    public void onBackToDashboardClicked(ActionEvent event) {
        SwitchSceneController switcher = new SwitchSceneController();
        switcher.switchScene(event, "/com/example/teamalfred/Dashboard.fxml");
    }
}
