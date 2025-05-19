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

public class OllamaResponseFetcher {

    private static final String USERAGENT = "OLLAMA FETCHER";
    public static final Logger logger = Logger.getLogger(OllamaResponseFetcher.class.getName());
    private final String apiURL;

    public OllamaResponseFetcher(String apiURL) {
        this.apiURL = apiURL;
    }

    protected HttpURLConnection getConnection() {
        HttpURLConnection conn = null;

        try {
            URL urlObj = new URI(apiURL).toURL(); //URL constructor is deprecated since java20
            conn = (HttpURLConnection)urlObj.openConnection();
            conn.setRequestProperty("User-Agent", USERAGENT);
        } catch(Exception ex){
            logger.log(Level.WARNING,"Error getting connection",ex);
        }
        return conn;
    }

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
        }
        return response;
    }

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

    public void fetchAsynchronousOllamaResponse(String model, String prompt, ResponseListener responseListener) {
        Thread thread = new Thread(){
            public void run(){
                OllamaResponse response = fetchOllamaResponse(model, prompt);
                responseListener.onResponseReceived(response);
            }
        };
        thread.start();
    }

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
                sb.append("\n");
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

