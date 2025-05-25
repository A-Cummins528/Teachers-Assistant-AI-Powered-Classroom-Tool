package com.example.teamalfred.controllers.AiFeatures;

/*
This code was based on Jollama https://github.com/aholinch/jollama/tree/main
and modified by Alessandro Soro (QUT) to use in CAB302

MIT License

Copyright (c) jollama

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fetches responses from an Ollama API endpoint.
 * This class handles the HTTP connection and request/response lifecycle
 * for interacting with an Ollama server.
 */
public class OllamaResponseFetcher {

    /** User-Agent string used for HTTP requests. */
    private static final String USERAGENT = "OLLAMA FETCHER";
    /** Logger instance for this class. */
    public static final Logger logger = Logger.getLogger(OllamaResponseFetcher.class.getName());
    /** The base URL of the Ollama API endpoint. */
    private final String apiURL;

    /**
     * Constructs an OllamaResponseFetcher with the specified API URL.
     * @param apiURL The base URL of the Ollama API (e.g., "http://localhost:11434/api/generate/").
     */
    public OllamaResponseFetcher(String apiURL) {
        this.apiURL = apiURL;
    }

    /**
     * Establishes and configures an HTTP connection to the Ollama API URL.
     * @return An HttpURLConnection object, or null if an error occurs.
     */
    protected HttpURLConnection getConnection() {
        HttpURLConnection conn = null;

        try {
            URL urlObj = new URI(apiURL).toURL(); // URL constructor is deprecated since java20
            conn = (HttpURLConnection)urlObj.openConnection();
            conn.setRequestProperty("User-Agent", USERAGENT);
        } catch(Exception ex){
            logger.log(Level.WARNING,"Error getting connection",ex);
        }
        return conn;
    }

    /**
     * Sends a JSON payload to the Ollama API and retrieves the response.
     * This is a private helper method used by the public-facing fetch methods.
     * @param simpleJsonObj A String containing the JSON request payload.
     * @return An OllamaResponse object parsed from the API's JSON response, or null on error.
     */
    private OllamaResponse fetchOllamaResponse(String simpleJsonObj) {
        HttpURLConnection conn = null;
        String output = null;
        OutputStream os = null;
        OllamaResponse response = null;

        try {
            logger.info("Attempting POST on " + apiURL);

            conn = getConnection();
            conn.setRequestMethod("POST");

            // send json to server
            conn.setDoOutput(true);
            os = conn.getOutputStream();
            os.write(simpleJsonObj.getBytes());
            os.flush();
            os.close();
            os = null;

            // read response
            int code = conn.getResponseCode();
            logger.info("Response: " + code);

            if(code == HttpURLConnection.HTTP_OK) {
                output = readConnInput(conn);
                response = OllamaResponse.fromJson(output);
            }
        } catch(Exception ex) {
            logger.log(Level.WARNING,"Error performing POST",ex);
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch(Exception ex) {}
            }
            // Note: HttpURLConnection itself doesn't need explicit closing in finally here,
            // as getInputStream/getOutputStream handle its lifecycle for typical cases.
            // If it were a different resource, explicit close for 'conn' might be considered.
        }
        return response;
    }

    /**
     * Fetches a response from the Ollama API for a given model and prompt.
     * For JSON request formatting, see <a href="https://github.com/ollama/ollama/blob/main/docs/api.md">Ollama API documentation</a>.
     * @param model The name of the Ollama model to use (e.g., "llama3").
     * @param prompt The user's prompt for the model.
     * @return An OllamaResponse object, or null if an error occurs.
     */
    public OllamaResponse fetchOllamaResponse(String model, String prompt) {
        // tested with model llama v3.2 -for documentation on how to format the JSON request https://github.com/ollama/ollama/blob/main/docs/api.md
        String simpleJsonObj = String.format("""
                {
                  "model": "%s",
                  "prompt": "%s",
                  "stream": false
                }
                """, model, prompt);
        return fetchOllamaResponse(simpleJsonObj);
    }

    /**
     * Fetches an Ollama response asynchronously on a new thread.
     * When the response is received, the onResponseReceived method of the provided
     * listener is called.
     * @param model The name of the Ollama model.
     * @param prompt The user's prompt.
     * @param responseListener The listener to be notified upon response reception.
     */
    public void fetchAsynchronousOllamaResponse(String model, String prompt, ResponseListener responseListener) {
        Thread thread = new Thread(){
            public void run(){
                OllamaResponse response = fetchOllamaResponse(model, prompt);
                responseListener.onResponseReceived(response);
            }
        };
        thread.start();
    }

    /**
     * Reads the entire input stream from an HTTP connection and returns it as a String.
     * @param conn The HttpURLConnection from which to read the input.
     * @return A String containing the response body, or an empty string on error.
     */
    protected String readConnInput(HttpURLConnection conn) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer(10000);
        try {
            is = conn.getInputStream();
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            br = new BufferedReader(isr);

            String line = br.readLine();

            while(line != null) {
                sb.append(line);
                sb.append("\n"); // Preserve newlines, useful for formatted text responses
                line = br.readLine();
            }
        }
        catch(Exception ex) {
            logger.log(Level.WARNING,"Error reading response",ex);
        } finally {
            if(is != null)try {is.close();}catch(Exception ex) {}
            if(isr != null)try {isr.close();}catch(Exception ex) {}
            if(br != null)try {br.close();}catch(Exception ex) {}
        }

        return sb.toString();
    }
}