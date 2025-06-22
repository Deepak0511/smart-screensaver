package in.dpk.assistants.smart_screensaver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.dpk.assistants.smart_screensaver.config.ExternalApiConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LocationService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient;
    private final ExternalApiConfig apiConfig;
    private final Map<String, Object> currentLocation = new ConcurrentHashMap<>();
    
    public LocationService(WebClient webClient, ExternalApiConfig apiConfig) {
        this.webClient = webClient;
        this.apiConfig = apiConfig;
        // Initialize with IP-based location on startup
        initializeLocation();
    }
    
    private void initializeLocation() {
        try {
            log.info("Initializing location service...");
            Map<String, Object> ipLocation = getIPBasedLocation();
            if (ipLocation != null) {
                currentLocation.putAll(ipLocation);
                log.info("Successfully initialized with IP-based location: {}", ipLocation.get("city"));
            } else {
                log.warn("Failed to get IP-based location during initialization");
            }
        } catch (Exception e) {
            log.error("Failed to initialize location: {}", e.getMessage(), e);
        }
    }
    
    public Map<String, Object> getLocationInfo() {
        // Always return current location (IP-based or browser-based)
        if (!currentLocation.isEmpty()) {
            log.info("Returning current location: {} (source: {})", 
                    currentLocation.get("city"), currentLocation.get("source"));
            return new HashMap<>(currentLocation);
        }
        
        log.info("No current location, falling back to IP-based location");
        // Fallback to IP-based location
        return getIPBasedLocation();
    }
    
    public void setBrowserLocation(double latitude, double longitude, String city, String region, String country) {
        log.info("Setting browser location: lat={}, lon={}, city={}, region={}, country={}", 
                latitude, longitude, city, region, country);
        
        // Clear current location
        currentLocation.clear();
        
        // Set coordinates
        currentLocation.put("latitude", String.valueOf(latitude));
        currentLocation.put("longitude", String.valueOf(longitude));
        currentLocation.put("source", "browser");
        
        // If city is provided and not "Unknown", use it
        if (city != null && !"Unknown".equals(city)) {
            currentLocation.put("city", city);
            currentLocation.put("region", region != null ? region : "");
            currentLocation.put("country", country != null ? country : "");
            log.info("Browser location set with provided city: {}", city);
        } else {
            // Try to get city name from coordinates using reverse geocoding
            try {
                Map<String, Object> geocodedLocation = reverseGeocode(latitude, longitude);
                if (geocodedLocation != null) {
                    currentLocation.putAll(geocodedLocation);
                    log.info("Reverse geocoding successful: {}", geocodedLocation.get("city"));
                } else {
                    // Fallback to IP-based location if reverse geocoding fails
                    Map<String, Object> ipLocation = getIPBasedLocation();
                    if (ipLocation != null) {
                        currentLocation.putAll(ipLocation);
                        log.info("Using IP-based location as fallback: {}", ipLocation.get("city"));
                    }
                }
            } catch (Exception e) {
                log.warn("Reverse geocoding failed, using IP-based location: {}", e.getMessage());
                Map<String, Object> ipLocation = getIPBasedLocation();
                if (ipLocation != null) {
                    currentLocation.putAll(ipLocation);
                }
            }
        }
        
        log.info("Final location set: {}", currentLocation.get("city"));
    }
    
    private Map<String, Object> getIPBasedLocation() {
        try {
            log.info("Fetching IP-based location from: {}", apiConfig.getLocationApiUrl());
            
            String response = webClient.get()
                    .uri(apiConfig.getLocationApiUrl())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response != null) {
                JsonNode locationData = objectMapper.readTree(response);
                
                Map<String, Object> location = new HashMap<>();
                location.put("latitude", locationData.get("latitude").asText());
                location.put("longitude", locationData.get("longitude").asText());
                location.put("city", locationData.get("city").asText());
                location.put("region", locationData.get("region").asText());
                location.put("country", locationData.get("country_name").asText());
                location.put("timezone", locationData.get("timezone").asText());
                location.put("source", "ip");
                
                log.info("IP-based location data fetched successfully: {}", location);
                return location;
            }
        } catch (Exception e) {
            log.error("Error fetching IP-based location data: {}", e.getMessage());
        }
        
        return null;
    }
    
    private Map<String, Object> reverseGeocode(double latitude, double longitude) {
        try {
            // Use Open-Meteo's reverse geocoding API (free and reliable)
            String geocodingUrl = String.format("https://geocoding-api.open-meteo.com/v1/search?name=&count=1&language=en&format=json&latitude=%.4f&longitude=%.4f",
                    latitude, longitude);
            
            log.info("Reverse geocoding coordinates: {}, {}", latitude, longitude);
            
            String response = webClient.get()
                    .uri(geocodingUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response != null) {
                JsonNode geocodingData = objectMapper.readTree(response);
                JsonNode results = geocodingData.get("results");
                
                if (results != null && results.isArray() && results.size() > 0) {
                    JsonNode location = results.get(0);
                    
                    Map<String, Object> geocodedLocation = new HashMap<>();
                    geocodedLocation.put("city", location.get("name").asText());
                    geocodedLocation.put("region", location.get("admin1").asText());
                    geocodedLocation.put("country", location.get("country").asText());
                    geocodedLocation.put("timezone", location.get("timezone").asText());
                    
                    log.info("Reverse geocoding successful: {} {}, {}", 
                            geocodedLocation.get("city"), 
                            geocodedLocation.get("region"), 
                            geocodedLocation.get("country"));
                    
                    return geocodedLocation;
                }
            }
        } catch (Exception e) {
            log.warn("Reverse geocoding failed: {}", e.getMessage());
        }
        
        return null;
    }
    
    // Backward compatibility methods
    public Map<String, Object> getBrowserLocation() {
        return getLocationInfo();
    }
    
    public boolean hasLocationData() {
        return !currentLocation.isEmpty();
    }
    
    public boolean hasValidLocationData() {
        if (currentLocation.isEmpty()) {
            return false;
        }
        
        String city = (String) currentLocation.get("city");
        return city != null && !city.isEmpty() && !city.equals("Unknown");
    }
    
    public boolean isLocationPermissionGranted() {
        return "browser".equals(currentLocation.get("source"));
    }
    
    public boolean isLocationRequested() {
        return hasLocationData();
    }
    
    public boolean isLocationExpired() {
        return currentLocation.isEmpty();
    }
    
    public void setLocationPermissionGranted(boolean granted) {
        // This is now handled automatically based on location source
        log.info("Location permission status: {}", granted);
    }
    
    public void setLocationRequested(boolean requested) {
        // This is now handled automatically
        log.info("Location requested: {}", requested);
    }
    
    public void clearLocation() {
        currentLocation.clear();
        log.info("Location data cleared");
        // Re-initialize with IP-based location
        initializeLocation();
    }
    
    public Map<String, Object> getLocationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("hasLocationData", hasLocationData());
        status.put("hasValidLocationData", hasValidLocationData());
        status.put("permissionGranted", isLocationPermissionGranted());
        status.put("locationRequested", isLocationRequested());
        status.put("isExpired", isLocationExpired());
        status.put("source", currentLocation.get("source"));
        
        if (hasLocationData()) {
            status.put("location", getLocationInfo());
        }
        
        return status;
    }
} 