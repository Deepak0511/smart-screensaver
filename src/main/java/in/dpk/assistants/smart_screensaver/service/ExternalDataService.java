package in.dpk.assistants.smart_screensaver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.dpk.assistants.smart_screensaver.config.ExternalApiConfig;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ExternalDataService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ExternalApiConfig apiConfig;
    private final LocationService locationService;
    
    public ExternalDataService(WebClient webClient, ExternalApiConfig apiConfig, LocationService locationService) {
        this.webClient = webClient;
        this.apiConfig = apiConfig;
        this.locationService = locationService;
        this.objectMapper = new ObjectMapper();
    }
    
    public Map<String, Object> getWeatherInfo() {
        try {
            // Get current location first
            Map<String, Object> location = locationService.getLocationInfo();
            if (location == null || location.get("latitude") == null) {
                log.warn("No location data available for weather");
                return createEmptyWeatherData();
            }
            
            double latitude = Double.parseDouble(location.get("latitude").toString());
            double longitude = Double.parseDouble(location.get("longitude").toString());
            
            // Fetch weather data from Open-Meteo API
            String weatherUrl = String.format("%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,weather_code&timezone=auto",
                    apiConfig.getWeatherApiUrl(), latitude, longitude);
            
            String response = webClient.get()
                    .uri(weatherUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response != null) {
                JsonNode weatherData = objectMapper.readTree(response);
                JsonNode current = weatherData.get("current");
                
                Map<String, Object> weather = new HashMap<>();
                weather.put("temperature", formatTemperature(current.get("temperature_2m").asDouble()));
                weather.put("condition", getWeatherCondition(current.get("weather_code").asInt()));
                weather.put("humidity", current.get("relative_humidity_2m").asInt() + "%");
                weather.put("location", location.get("city"));
                weather.put("source", location.get("source"));
                
                log.info("Weather data fetched successfully: {}", weather);
                return weather;
            }
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage());
        }
        
        return createEmptyWeatherData();
    }
    
    public Map<String, Object> getTrafficInfo() {
        try {
            // Get current location
            Map<String, Object> location = locationService.getLocationInfo();
            String city = location != null ? location.get("city").toString() : "";
            
            // For now, provide traffic estimates based on time of day
            // In a real implementation, you would integrate with Google Maps API or similar
            Map<String, Object> traffic = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            
            if (hour >= 7 && hour <= 9) {
                // Morning rush hour
                traffic.put("status", "Heavy");
                traffic.put("travelTime", "35 min");
                traffic.put("distance", "8.5 km");
                traffic.put("message", "Morning rush hour traffic");
            } else if (hour >= 17 && hour <= 19) {
                // Evening rush hour
                traffic.put("status", "Heavy");
                traffic.put("travelTime", "40 min");
                traffic.put("distance", "8.5 km");
                traffic.put("message", "Evening rush hour traffic");
            } else {
                // Normal traffic
                traffic.put("status", "Moderate");
                traffic.put("travelTime", "20 min");
                traffic.put("distance", "8.5 km");
                traffic.put("message", "Normal traffic conditions");
            }
            
            traffic.put("location", city);
            traffic.put("source", location != null ? location.get("source") : "");
            log.info("Traffic data generated for {}: {}", city, traffic);
            return traffic;
            
        } catch (Exception e) {
            log.error("Error generating traffic data: {}", e.getMessage());
        }
        
        return createEmptyTrafficData();
    }
    
    public Map<String, Object> getQuoteOfTheDay() {
        // Try multiple quote APIs in case one fails
        String[] quoteApis = {
            "https://api.quotable.io/random",
            "https://zenquotes.io/api/random",
            "https://quotes.rest/qod"
        };
        
        for (String apiUrl : quoteApis) {
            try {
                String response = webClient.get()
                        .uri(apiUrl)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                if (response != null) {
                    JsonNode quoteData = objectMapper.readTree(response);
                    Map<String, Object> quote = new HashMap<>();
                    
                    // Handle different API response formats
                    if (apiUrl.contains("quotable.io")) {
                        quote.put("text", quoteData.get("content").asText());
                        quote.put("author", quoteData.get("author").asText());
                        quote.put("category", quoteData.get("tags").get(0).asText());
                    } else if (apiUrl.contains("zenquotes.io")) {
                        JsonNode firstQuote = quoteData.get(0);
                        quote.put("text", firstQuote.get("q").asText());
                        quote.put("author", firstQuote.get("a").asText());
                        quote.put("category", "Inspiration");
                    } else if (apiUrl.contains("quotes.rest")) {
                        JsonNode contents = quoteData.get("contents");
                        JsonNode firstQuote = contents.get("quotes").get(0);
                        quote.put("text", firstQuote.get("quote").asText());
                        quote.put("author", firstQuote.get("author").asText());
                        quote.put("category", firstQuote.get("category").asText());
                    }
                    
                    log.info("Quote fetched successfully from {}: {}", apiUrl, quote.get("text"));
                    return quote;
                }
            } catch (Exception e) {
                log.warn("Failed to fetch quote from {}: {}", apiUrl, e.getMessage());
                continue; // Try next API
            }
        }
        
        // If all APIs fail, return empty quote data
        log.warn("All quote APIs failed, returning empty data");
        return createEmptyQuoteData();
    }
    
    public Map<String, Object> getBusLocation() {
        // This would integrate with local transit APIs
        // For now, provide mock data based on time
        Map<String, Object> busInfo = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        int minute = now.getMinute();
        
        busInfo.put("busNumber", "401A");
        busInfo.put("estimatedArrival", (minute % 10 + 2) + " min");
        busInfo.put("currentLocation", "MG Road");
        busInfo.put("nextStop", "Koramangala");
        busInfo.put("status", "On Time");
        
        return busInfo;
    }
    
    // Helper methods
    private String formatTemperature(double temp) {
        return String.format("%.1f°C", temp);
    }
    
    private String getWeatherCondition(int weatherCode) {
        // WMO Weather interpretation codes
        if (weatherCode == 0) return "Clear Sky";
        else if (weatherCode >= 1 && weatherCode <= 3) return "Partly Cloudy";
        else if (weatherCode >= 45 && weatherCode <= 48) return "Foggy";
        else if (weatherCode >= 51 && weatherCode <= 55) return "Drizzle";
        else if (weatherCode >= 56 && weatherCode <= 57) return "Freezing Drizzle";
        else if (weatherCode >= 61 && weatherCode <= 65) return "Rain";
        else if (weatherCode >= 66 && weatherCode <= 67) return "Freezing Rain";
        else if (weatherCode >= 71 && weatherCode <= 75) return "Snow";
        else if (weatherCode >= 77 && weatherCode <= 77) return "Snow Grains";
        else if (weatherCode >= 80 && weatherCode <= 82) return "Rain Showers";
        else if (weatherCode >= 85 && weatherCode <= 86) return "Snow Showers";
        else if (weatherCode >= 95 && weatherCode <= 95) return "Thunderstorm";
        else if (weatherCode >= 96 && weatherCode <= 99) return "Thunderstorm with Hail";
        else return "Unknown";
    }
    
    // Empty data methods (no mock data)
    private Map<String, Object> createEmptyWeatherData() {
        Map<String, Object> weather = new HashMap<>();
        weather.put("temperature", "");
        weather.put("condition", "");
        weather.put("humidity", "");
        weather.put("location", "");
        weather.put("source", "");
        return weather;
    }
    
    private Map<String, Object> createEmptyTrafficData() {
        Map<String, Object> traffic = new HashMap<>();
        traffic.put("status", "");
        traffic.put("travelTime", "");
        traffic.put("distance", "");
        traffic.put("message", "");
        traffic.put("location", "");
        traffic.put("source", "");
        return traffic;
    }
    
    private Map<String, Object> createEmptyQuoteData() {
        Map<String, Object> quote = new HashMap<>();
        quote.put("text", "");
        quote.put("author", "");
        quote.put("category", "");
        return quote;
    }
} 