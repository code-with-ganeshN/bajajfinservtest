package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Component
public class StartupRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) throws Exception {
        // 1. Prepare the request body
        Map<String, String> requestBody = Map.of(
            "name", "Natakasala Ganesh",
            "regNo", "22BCE20387",  // your registration number
            "email", "ganesh901044@gmail.com"
        );

        // 2. Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        // 3. POST request to generate webhook
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if(response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();

            // 4. Fetch webhook and accessToken
            String webhookUrl = (String) responseBody.get("webhook");
            String accessToken = (String) responseBody.get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            // TODO: Based on your regNo, fetch the question manually from Google Drive link
            System.out.println("Check your assigned SQL question from the drive link.");
        } else {
            System.out.println("Failed to generate webhook. Status: " + response.getStatusCode());
        }
    }
}
