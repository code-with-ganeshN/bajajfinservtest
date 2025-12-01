package com.example.demo;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void executeFlow() {
        // Step 1: Generate webhook
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "John Doe");
        requestBody.put("regNo", "REG12347");
        requestBody.put("email", "john@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GenerateWebhookResponse> response = restTemplate.postForEntity(
                generateWebhookUrl, request, GenerateWebhookResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            GenerateWebhookResponse webhookResponse = response.getBody();
            String webhookUrl = webhookResponse.getWebhook();
            String accessToken = webhookResponse.getAccessToken();

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            // Step 2: Solve SQL problem
            String finalQuery = solveSQLProblem();

            // Step 3: Send final query to webhook
            sendFinalQuery(webhookUrl, finalQuery, accessToken);
        } else {
            System.out.println("Failed to generate webhook: " + response.getStatusCode());
        }
    }

    private String solveSQLProblem() {
        // TODO: Replace this with your SQL query solving logic
        String query = "WITH emp_age AS (\r\n" + //
                        "    SELECT \r\n" + //
                        "        e.EMP_ID,\r\n" + //
                        "        e.FIRST_NAME || ' ' || e.LAST_NAME AS FULL_NAME,\r\n" + //
                        "        d.DEPARTMENT_ID,\r\n" + //
                        "        d.DEPARTMENT_NAME,\r\n" + //
                        "        FLOOR(DATEDIFF(day, e.DOB, GETDATE()) / 365.25) AS AGE\r\n" + //
                        "    FROM EMPLOYEE e\r\n" + //
                        "    JOIN DEPARTMENT d \r\n" + //
                        "        ON e.DEPARTMENT = d.DEPARTMENT_ID\r\n" + //
                        "),\r\n" + //
                        "high_salary AS (\r\n" + //
                        "    SELECT \r\n" + //
                        "        p.EMP_ID,\r\n" + //
                        "        p.AMOUNT\r\n" + //
                        "    FROM PAYMENTS p\r\n" + //
                        "    WHERE p.AMOUNT > 70000\r\n" + //
                        "),\r\n" + //
                        "final_data AS (\r\n" + //
                        "    SELECT \r\n" + //
                        "        ea.DEPARTMENT_ID,\r\n" + //
                        "        ea.DEPARTMENT_NAME,\r\n" + //
                        "        ea.FULL_NAME,\r\n" + //
                        "        ea.AGE\r\n" + //
                        "    FROM emp_age ea\r\n" + //
                        "    JOIN high_salary hs \r\n" + //
                        "        ON ea.EMP_ID = hs.EMP_ID\r\n" + //
                        ")\r\n" + //
                        "SELECT \r\n" + //
                        "    DEPARTMENT_NAME,\r\n" + //
                        "    AVG(AGE) AS AVERAGE_AGE,\r\n" + //
                        "    STRING_AGG(FULL_NAME, ', ') WITHIN GROUP (ORDER BY FULL_NAME) \r\n" + //
                        "        OVER (PARTITION BY DEPARTMENT_ID) AS EMPLOYEE_LIST\r\n" + //
                        "FROM (\r\n" + //
                        "    SELECT *, \r\n" + //
                        "        ROW_NUMBER() OVER (PARTITION BY DEPARTMENT_ID ORDER BY FULL_NAME) AS rn\r\n" + //
                        "    FROM final_data\r\n" + //
                        ") x\r\n" + //
                        "WHERE rn <= 10\r\n" + //
                        "GROUP BY DEPARTMENT_ID, DEPARTMENT_NAME\r\n" + //
                        "ORDER BY DEPARTMENT_ID DESC;\r\n" + //
                        "";
        System.out.println("Final SQL Query: " + query);
        return query;
    }

    private void sendFinalQuery(String webhookUrl, String finalQuery, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken); // JWT token

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

        System.out.println("Submission Response: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
    }
}
