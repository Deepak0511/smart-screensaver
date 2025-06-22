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
import java.time.Duration;
import java.util.Random;

@Service
@Slf4j
public class ExternalDataService {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ExternalApiConfig apiConfig;
    private final LocationService locationService;
    private final SystemSettingsService systemSettingsService;
    private final Random random = new Random();
    
    public ExternalDataService(WebClient webClient, ExternalApiConfig apiConfig, 
                             LocationService locationService, SystemSettingsService systemSettingsService) {
        this.webClient = webClient;
        this.apiConfig = apiConfig;
        this.locationService = locationService;
        this.systemSettingsService = systemSettingsService;
        this.objectMapper = new ObjectMapper();
    }
    
    public Map<String, Object> getWeatherInfo() {
        // Check if weather API is enabled
        if (!systemSettingsService.isApiEnabled("weather")) {
            log.info("Weather API is disabled, using fallback data");
            return createEmptyWeatherData();
        }
        
        try {
            // Get current location first
            Map<String, Object> location = locationService.getLocationInfo();
            if (location == null || location.get("latitude") == null) {
                log.warn("No location data available for weather");
                return createEmptyWeatherData();
            }
            
            double latitude = Double.parseDouble(location.get("latitude").toString());
            double longitude = Double.parseDouble(location.get("longitude").toString());
            
            // Get weather API URL from system settings
            String weatherApiUrl = systemSettingsService.getApiUrl("weather");
            if (weatherApiUrl == null || weatherApiUrl.trim().isEmpty()) {
                log.warn("Weather API URL not configured, using fallback data");
                return createEmptyWeatherData();
            }
            
            // Fetch weather data from configured API
            String weatherUrl = String.format("%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,weather_code&timezone=auto",
                    weatherApiUrl, latitude, longitude);
            
            String response = webClient.get()
                    .uri(weatherUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(systemSettingsService.getApiTimeout()))
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
        
        // Return fallback data if API fails and fallback mode is enabled
        if (systemSettingsService.isFallbackModeEnabled()) {
            return createFallbackWeatherData();
        }
        
        return createEmptyWeatherData();
    }
    
    public Map<String, Object> getTrafficInfo() {
        // Check if traffic API is enabled
        if (!systemSettingsService.isApiEnabled("traffic")) {
            log.info("Traffic API is disabled, using fallback data");
            return createFallbackTrafficData();
        }
        
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
        
        // Return fallback data if API fails and fallback mode is enabled
        if (systemSettingsService.isFallbackModeEnabled()) {
            return createFallbackTrafficData();
        }
        
        return createEmptyTrafficData();
    }
    
    public Map<String, Object> getQuoteOfTheDay() {
        // Check if quote API is enabled
        if (!systemSettingsService.isApiEnabled("quote")) {
            log.info("Quote API is disabled, using fallback quotes");
            return getFallbackQuote();
        }
        
        // Get quote API URL from system settings
        String quoteApiUrl = systemSettingsService.getApiUrl("quote");
        if (quoteApiUrl == null || quoteApiUrl.trim().isEmpty()) {
            log.warn("Quote API URL not configured, using fallback quotes");
            return getFallbackQuote();
        }
        
        // Use more reliable quote APIs with better error handling
        QuoteApi[] quoteApis = {
            new QuoteApi(quoteApiUrl, "quotable"),
            new QuoteApi("https://zenquotes.io/api/random", "zenquotes"),
            new QuoteApi("https://api.goprogram.ai/inspiration", "goprogram")
        };
        
        for (QuoteApi api : quoteApis) {
            try {
                log.debug("Attempting to fetch quote from: {}", api.url);
                
                String response = webClient.get()
                        .uri(api.url)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(systemSettingsService.getApiTimeout()))
                        .block();
                
                if (response != null && !response.trim().isEmpty()) {
                    JsonNode quoteData = objectMapper.readTree(response);
                    Map<String, Object> quote = parseQuoteResponse(quoteData, api.type);
                    
                    if (quote != null && quote.get("text") != null && !quote.get("text").toString().isEmpty()) {
                        log.info("Quote fetched successfully from {}: {}", api.url, quote.get("text"));
                        return quote;
                    }
                }
            } catch (WebClientResponseException e) {
                log.warn("HTTP error fetching quote from {}: {} - {}", api.url, e.getStatusCode(), e.getMessage());
            } catch (Exception e) {
                log.warn("Failed to fetch quote from {}: {}", api.url, e.getMessage());
            }
        }
        
        // If all APIs fail, return a fallback quote
        log.warn("All quote APIs failed, using fallback quote");
        return getFallbackQuote();
    }
    
    private Map<String, Object> parseQuoteResponse(JsonNode quoteData, String apiType) {
        Map<String, Object> quote = new HashMap<>();
        
        try {
            switch (apiType) {
                case "quotable":
                    if (quoteData.has("content") && quoteData.has("author")) {
                        quote.put("text", quoteData.get("content").asText());
                        quote.put("author", quoteData.get("author").asText());
                        quote.put("category", quoteData.has("tags") && quoteData.get("tags").isArray() && quoteData.get("tags").size() > 0 
                                ? quoteData.get("tags").get(0).asText() : "Inspiration");
                    }
                    break;
                    
                case "zenquotes":
                    if (quoteData.isArray() && quoteData.size() > 0) {
                        JsonNode firstQuote = quoteData.get(0);
                        if (firstQuote.has("q") && firstQuote.has("a")) {
                            quote.put("text", firstQuote.get("q").asText());
                            quote.put("author", firstQuote.get("a").asText());
                            quote.put("category", "Inspiration");
                        }
                    }
                    break;
                    
                case "goprogram":
                    if (quoteData.has("quote") && quoteData.has("author")) {
                        quote.put("text", quoteData.get("quote").asText());
                        quote.put("author", quoteData.get("author").asText());
                        quote.put("category", "Inspiration");
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("Error parsing quote response from {}: {}", apiType, e.getMessage());
            return null;
        }
        
        return quote;
    }
    
    private Map<String, Object> getFallbackQuote() {
        // Expanded fallback quotes
        String[][] fallbackQuotes = {
            {"The only way to do great work is to love what you do.", "Steve Jobs", "Inspiration"},
            {"Life is what happens when you're busy making other plans.", "John Lennon", "Life"},
            {"The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt", "Dreams"},
            {"Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill", "Success"},
            {"The journey of a thousand miles begins with one step.", "Lao Tzu", "Wisdom"},
            {"Be the change you wish to see in the world.", "Mahatma Gandhi", "Change"},
            {"In the middle of difficulty lies opportunity.", "Albert Einstein", "Opportunity"},
            {"The best way to predict the future is to invent it.", "Alan Kay", "Innovation"},
            {"Everything you've ever wanted is on the other side of fear.", "George Addair", "Courage"},
            {"The only limit to our realization of tomorrow is our doubts of today.", "Franklin D. Roosevelt", "Belief"}
        };
        
        String[] selectedQuote = fallbackQuotes[random.nextInt(fallbackQuotes.length)];
        
        Map<String, Object> quote = new HashMap<>();
        quote.put("text", selectedQuote[0]);
        quote.put("author", selectedQuote[1]);
        quote.put("category", selectedQuote[2]);
        
        return quote;
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
    
    // Fallback data methods (with mock data)
    private Map<String, Object> createFallbackWeatherData() {
        Map<String, Object> weather = new HashMap<>();
        weather.put("temperature", "22°C");
        weather.put("condition", "Partly Cloudy");
        weather.put("humidity", "65%");
        weather.put("location", "Bangalore");
        weather.put("source", "Fallback");
        return weather;
    }
    
    private Map<String, Object> createFallbackTrafficData() {
        Map<String, Object> traffic = new HashMap<>();
        traffic.put("status", "Moderate");
        traffic.put("travelTime", "25 min");
        traffic.put("distance", "8.5 km");
        traffic.put("message", "Normal traffic conditions");
        traffic.put("location", "Bangalore");
        traffic.put("source", "Fallback");
        return traffic;
    }
    
    // Helper class for quote APIs
    private static class QuoteApi {
        final String url;
        final String type;
        
        QuoteApi(String url, String type) {
            this.url = url;
            this.type = type;
        }
    }
} 