package edu.kingston.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MappingService {

    private static final String API_KEY = System.getenv("GOOGLE_MAPS_API_KEY");

    private static final String DISTANCE_MATRIX_URL =
            "https://maps.googleapis.com/maps/api/distancematrix/json";

    public String getDeliveryEstimate(String origin, String destination) {
        String url = String.format("%s?origins=%s&destinations=%s&key=%s",
                DISTANCE_MATRIX_URL, origin, destination, API_KEY);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            return parseResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calculating delivery estimate.";
        }
    }

    private String parseResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the JSON response
            JsonNode rootNode = objectMapper.readTree(response);

            // Check the overall status
            String apiStatus = rootNode.path("status").asText();
            if (!"OK".equals(apiStatus)) {
                return "API Error: " + apiStatus;
            }

            JsonNode rowsNode = rootNode.path("rows");
            if (rowsNode.isArray() && rowsNode.size() > 0) {
                JsonNode elementsNode = rowsNode.get(0).path("elements");
                if (elementsNode.isArray() && elementsNode.size() > 0) {
                    JsonNode durationNode = elementsNode.get(0).path("duration");
                    String deliveryTime = durationNode.path("text").asText();

                    if (!deliveryTime.isEmpty()) {
                        return deliveryTime;
                    }
                }
            }

            return "Unable to calculate delivery estimate.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing delivery estimate.";
        }
    }
}
