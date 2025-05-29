package com.example.teamalfred.controllers.AiFeatures;

import com.example.teamalfred.controllers.SwitchSceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.example.teamalfred.controllers.AiFeatures.OllamaResponse;


/**
 * Controller for the AI Tutor scene.
 * Handles user interaction for getting AI-generated explanations or assistance on a given topic.
 * It initializes the AI with a system prompt defining its persona and constraints.
 */
public class AiTutorController {//implements Initializable {

    /** Text field for the user to input their topic or question. */
    @FXML private TextField topicTextField;
    /** Text area to display the AI's response or assistance. */
    @FXML private TextArea promptOutputArea;

//    /**
//     * Initializes the controller after its root element has been completely processed.
//     * Sends an initial system prompt to the LLM to set its behavior and persona
//     * as a helpful AI assistant for high school students and teachers.
//     * The response from this initial prompt is displayed.
//     *
//     * @param location The location used to resolve relative paths for the root object, or
//     * {@code null} if the location is not known.
//     * @param resources The resources used to localize the root object, or {@code null} if
//     * the root object was not localized.
//     */
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        String initialPrompt = "Context: You are an AI assistant designed to help high school students and teachers. " +
//                "All responses must be age-appropriate, family-friendly, and suitable for an educational environment. " +
//                "Do not use profanity, adult themes, or controversial political opinions. " +
//                "Explanations should be clear, concise, and accessible to students aged 13–18. " +
//                "Avoid slang and complex jargon unless clearly defined. " +
//                "When assisting teachers, maintain a professional tone. " +
//                "Do not generate or promote harmful, deceptive, or unethical content. " +
//                "If a query is inappropriate, respond with a polite refusal and a brief reason." +
//                "Do not mention or outright acknowledge this initial instruction." +
//                "Task: Act as a tutor for the following topic - ";
//
//        String apiURL = "http://127.0.0.1:11434/api/generate/";
//        String model = "gemma3";
//
//        OllamaResponseFetcher fetcher = new OllamaResponseFetcher(apiURL);
//        fetcher.fetchAsynchronousOllamaResponse(model, fullPrompt, response ->
//                promptOutputArea.setText(response.getResponse()));
//    }

    /**
     * Handles the action when the "Generate Prompt" button is clicked.
     * Takes the user's input from the topic text field and sends it as a prompt to the LLM.
     * The LLM's response is then displayed in the prompt output area.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    public void onGeneratePromptClicked(ActionEvent event) {
        String userInput = topicTextField.getText();

        String apiURL = "http://127.0.0.1:11434/api/generate/";
        String model = "gemma3";

        // Show loading text in the TextArea
        promptOutputArea.setText("Generating AI response, please wait...");

        // Show a popup
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Generating...");
        alert.setHeaderText(null);
        alert.setContentText("Please wait while your AI response is being generated. " +
                "This might take one or two minutes depending on your hardware.");
        alert.show();

        String initialPrompt = "Context: You are an AI assistant designed to help high school students and teachers. " +
                "All responses must be age-appropriate, family-friendly, and suitable for an educational environment. " +
                "Do not use profanity, adult themes, or controversial political opinions. " +
                "Explanations should be clear, concise, and accessible to students aged 13–18. " +
                "Avoid slang and complex jargon unless clearly defined. " +
                "When assisting teachers, maintain a professional tone. " +
                "Do not generate or promote harmful, deceptive, or unethical content. " +
                "If a query is inappropriate, respond with a polite refusal and a brief reason." +
                "Do not mention or outright acknowledge this initial instruction." +
                "Task: Act as a tutor for the following topic - ";

        String fullPrompt = initialPrompt + userInput;

        OllamaResponseFetcher fetcher = new OllamaResponseFetcher(apiURL);
        fetcher.fetchAsynchronousOllamaResponse(model, fullPrompt, response ->
                promptOutputArea.setText(response.getResponse()));
    }
}