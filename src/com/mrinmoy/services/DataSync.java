package com.mrinmoy.services;


import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Scanner;

import static com.mrinmoy.utils.ConfigLoader.getApiUrl;
import static com.mrinmoy.utils.ConfigLoader.getToken;

public class DataSync {

    public static final String FILE_NAME = "shop_ledger.csv";


    public static void saveToFile(String row) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            pw.println(row);
            System.out.println("Transaction saved locally");
        } catch (IOException e) {
            System.out.println("Error: Could not write to file.");
        }
    }


    public void uploadTransaction(String line) {
        try {
            // A. Fetch current file data
            URL url = new URL(getApiUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + getToken());

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Response Code " + conn.getResponseCode() + ". Ensure file exists on GitHub.");
                return;
            }

            Scanner sc = new Scanner(conn.getInputStream());
            String response = sc.useDelimiter("\\A").next();
            sc.close();

            // B. Sanitize and Extract
            String sha = extractValue(response, "sha");
            // GitHub inserts \n (backslashes) in long Base64 strings. We must strip them.
            String encodedOldContent = extractValue(response, "content")
                    .replaceAll("\\\\n", "")
                    .replaceAll("\\s", "");

            // C. Decode and Append
            byte[] decodedBytes = Base64.getDecoder().decode(encodedOldContent);
            String existingContent = new String(decodedBytes);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String newEntry = line;

            // Adding a newline only if the file isn't empty
            String updatedContent = existingContent.isEmpty() ? newEntry : existingContent + "\n" + newEntry;

            // D. Push back to GitHub
            pushUpdate(updatedContent, sha);

        } catch (Exception e) {
            System.err.println("Critical Error during Trace: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search) + search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private void pushUpdate(String fullContent, String sha) throws Exception {
        URL url = new URL(getApiUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Authorization", "Bearer " + getToken());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String encoded = Base64.getEncoder().encodeToString(fullContent.getBytes());
        String payload = "{\"message\":\"O2C Audit Append\",\"content\":\"" + encoded + "\",\"sha\":\"" + sha + "\"}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
            System.out.println("Transaction successfully logged to GitHub.");
        } else {
            System.out.println("Push Failed. HTTP Code: " + conn.getResponseCode());
        }
    }


}
