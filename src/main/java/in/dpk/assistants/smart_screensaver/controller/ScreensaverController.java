package in.dpk.assistants.smart_screensaver.controller;

import in.dpk.assistants.smart_screensaver.service.ScreensaverService;
import in.dpk.assistants.smart_screensaver.service.ExternalDataService;
import in.dpk.assistants.smart_screensaver.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/screensaver")
@RequiredArgsConstructor
@Slf4j
public class ScreensaverController {
    
    private final ScreensaverService screensaverService;
    private final ExternalDataService externalDataService;
    private final LocationService locationService;
    
    @GetMapping("/content")
    public ResponseEntity<Map<String, Object>> getScreensaverContent() {
        try {
            Map<String, Object> content = screensaverService.getScreensaverContent();
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            log.error("Error getting screensaver content: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/weather")
    public ResponseEntity<Map<String, Object>> getWeatherInfo() {
        try {
            Map<String, Object> weather = externalDataService.getWeatherInfo();
            return ResponseEntity.ok(weather);
        } catch (Exception e) {
            log.error("Error getting weather info: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/location")
    public ResponseEntity<Map<String, Object>> getLocationInfo() {
        try {
            Map<String, Object> location = locationService.getLocationInfo();
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            log.error("Error getting location info: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/browser-location")
    public ResponseEntity<Map<String, Object>> getBrowserLocation() {
        try {
            Map<String, Object> location = locationService.getBrowserLocation();
            if (location != null && locationService.hasValidLocationData()) {
                location.put("status", "valid");
                location.put("permissionGranted", locationService.isLocationPermissionGranted());
                return ResponseEntity.ok(location);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No valid browser location available");
                response.put("status", "invalid");
                response.put("permissionGranted", locationService.isLocationPermissionGranted());
                response.put("hasLocationData", locationService.hasLocationData());
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.error("Error getting browser location: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/browser-location")
    public ResponseEntity<Map<String, Object>> setBrowserLocation(@RequestBody Map<String, Object> locationData) {
        try {
            Double latitude = Double.parseDouble(locationData.get("latitude").toString());
            Double longitude = Double.parseDouble(locationData.get("longitude").toString());
            String city = (String) locationData.get("city");
            String region = (String) locationData.get("region");
            String country = (String) locationData.get("country");
            
            locationService.setBrowserLocation(latitude, longitude, city, region, country);
            
            return ResponseEntity.ok(Map.of("message", "Browser location set successfully"));
        } catch (Exception e) {
            log.error("Error setting browser location: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid location data"));
        }
    }
    
    @PostMapping("/location-permission")
    public ResponseEntity<Map<String, Object>> setLocationPermission(@RequestBody Map<String, Object> permissionData) {
        try {
            Boolean granted = Boolean.parseBoolean(permissionData.get("granted").toString());
            locationService.setLocationPermissionGranted(granted);
            
            return ResponseEntity.ok(Map.of("message", "Location permission status updated"));
        } catch (Exception e) {
            log.error("Error setting location permission: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid permission data"));
        }
    }
    
    @DeleteMapping("/browser-location")
    public ResponseEntity<Map<String, Object>> clearBrowserLocation() {
        try {
            locationService.clearLocation();
            return ResponseEntity.ok(Map.of("message", "Browser location cleared successfully"));
        } catch (Exception e) {
            log.error("Error clearing browser location: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/quote")
    public ResponseEntity<Map<String, Object>> getQuoteOfTheDay() {
        try {
            Map<String, Object> quote = externalDataService.getQuoteOfTheDay();
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            log.error("Error getting quote: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/traffic")
    public ResponseEntity<Map<String, Object>> getTrafficInfo() {
        try {
            Map<String, Object> traffic = externalDataService.getTrafficInfo();
            return ResponseEntity.ok(traffic);
        } catch (Exception e) {
            log.error("Error getting traffic info: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/bus")
    public ResponseEntity<Map<String, Object>> getBusLocation() {
        try {
            Map<String, Object> busInfo = externalDataService.getBusLocation();
            return ResponseEntity.ok(busInfo);
        } catch (Exception e) {
            log.error("Error getting bus location: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/test-location")
    public ResponseEntity<Map<String, Object>> testLocationService() {
        try {
            log.info("Testing location service...");
            Map<String, Object> location = locationService.getLocationInfo();
            Map<String, Object> status = locationService.getLocationStatus();
            
            Map<String, Object> response = new HashMap<>();
            response.put("location", location);
            response.put("status", status);
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("Location test completed: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error testing location service: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "Smart Screensaver",
            "version", "1.0.0",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
} 