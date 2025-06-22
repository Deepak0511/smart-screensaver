package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.config.ExternalApiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.web-application-type=reactive",
    "app.external.location.api-url=https://ipapi.co/json/",
    "app.external.weather.api-url=https://api.open-meteo.com/v1/forecast"
})
class LocationServiceTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private ExternalApiConfig apiConfig;

    private LocationService locationService;

    @BeforeEach
    void setUp() {
        locationService = new LocationService(webClient, apiConfig);
    }

    @Test
    @DisplayName("Should fetch IP-based location from external API")
    void shouldFetchIpBasedLocation() {
        Map<String, Object> locationInfo = locationService.getLocationInfo();
        
        assertNotNull(locationInfo);
        assertTrue(locationInfo.containsKey("latitude"));
        assertTrue(locationInfo.containsKey("longitude"));
        assertTrue(locationInfo.containsKey("city"));
        assertTrue(locationInfo.containsKey("country"));
        assertTrue(locationInfo.containsKey("source"));
        
        // Verify data types
        assertNotNull(locationInfo.get("latitude"));
        assertNotNull(locationInfo.get("longitude"));
        assertNotNull(locationInfo.get("city"));
        assertNotNull(locationInfo.get("country"));
        assertEquals("ip", locationInfo.get("source"));
        
        // Verify coordinates are valid
        try {
            double lat = Double.parseDouble(locationInfo.get("latitude").toString());
            double lon = Double.parseDouble(locationInfo.get("longitude").toString());
            assertTrue(lat >= -90 && lat <= 90);
            assertTrue(lon >= -180 && lon <= 180);
        } catch (NumberFormatException e) {
            fail("Invalid coordinate format");
        }
    }

    @Test
    @DisplayName("Should set browser location with coordinates")
    void shouldSetBrowserLocation() {
        // Test coordinates for Bangalore, India
        double latitude = 12.9716;
        double longitude = 77.5946;
        String city = "Bangalore";
        String region = "Karnataka";
        String country = "India";
        
        locationService.setBrowserLocation(latitude, longitude, city, region, country);
        
        Map<String, Object> locationInfo = locationService.getLocationInfo();
        
        assertNotNull(locationInfo);
        assertEquals(String.valueOf(latitude), locationInfo.get("latitude"));
        assertEquals(String.valueOf(longitude), locationInfo.get("longitude"));
        assertEquals(city, locationInfo.get("city"));
        assertEquals(region, locationInfo.get("region"));
        assertEquals(country, locationInfo.get("country"));
        assertEquals("browser", locationInfo.get("source"));
    }

    @Test
    @DisplayName("Should perform reverse geocoding for coordinates")
    void shouldPerformReverseGeocoding() {
        // Test coordinates for New York City
        double latitude = 40.7128;
        double longitude = -74.0060;
        
        locationService.setBrowserLocation(latitude, longitude, "Unknown", null, null);
        
        Map<String, Object> locationInfo = locationService.getLocationInfo();
        
        assertNotNull(locationInfo);
        assertEquals(String.valueOf(latitude), locationInfo.get("latitude"));
        assertEquals(String.valueOf(longitude), locationInfo.get("longitude"));
        assertEquals("browser", locationInfo.get("source"));
        
        // Should have city information from reverse geocoding
        assertTrue(locationInfo.containsKey("city"));
        assertNotNull(locationInfo.get("city"));
        assertFalse(locationInfo.get("city").toString().isEmpty());
    }

    @Test
    @DisplayName("Should handle location permission status")
    void shouldHandleLocationPermissionStatus() {
        // Initially should not have browser location
        assertFalse(locationService.isLocationPermissionGranted());
        
        // Set browser location
        locationService.setBrowserLocation(12.9716, 77.5946, "Bangalore", "Karnataka", "India");
        
        // Now should have browser location
        assertTrue(locationService.isLocationPermissionGranted());
    }

    @Test
    @DisplayName("Should validate location data")
    void shouldValidateLocationData() {
        // Initially should have valid location data (IP-based)
        assertTrue(locationService.hasLocationData());
        assertTrue(locationService.hasValidLocationData());
        
        // Set invalid location
        locationService.setBrowserLocation(0.0, 0.0, "Unknown", null, null);
        
        // Should still have location data but may not be valid
        assertTrue(locationService.hasLocationData());
    }

    @Test
    @DisplayName("Should clear location data")
    void shouldClearLocation() {
        // Set browser location first
        locationService.setBrowserLocation(12.9716, 77.5946, "Bangalore", "Karnataka", "India");
        assertTrue(locationService.hasLocationData());
        
        // Clear location
        locationService.clearLocation();
        
        // Should fall back to IP-based location
        Map<String, Object> locationInfo = locationService.getLocationInfo();
        assertNotNull(locationInfo);
        assertEquals("ip", locationInfo.get("source"));
    }

    @Test
    @DisplayName("Should get location status information")
    void shouldGetLocationStatus() {
        Map<String, Object> status = locationService.getLocationStatus();
        
        assertNotNull(status);
        assertTrue(status.containsKey("hasLocationData"));
        assertTrue(status.containsKey("hasValidLocationData"));
        assertTrue(status.containsKey("isLocationPermissionGranted"));
        assertTrue(status.containsKey("isLocationRequested"));
        assertTrue(status.containsKey("isLocationExpired"));
    }

    @Test
    @DisplayName("Should handle location with unknown city")
    void shouldHandleLocationWithUnknownCity() {
        locationService.setBrowserLocation(12.9716, 77.5946, "Unknown", null, null);
        
        Map<String, Object> locationInfo = locationService.getLocationInfo();
        
        assertNotNull(locationInfo);
        assertEquals("browser", locationInfo.get("source"));
        
        // Should attempt reverse geocoding or fall back to IP-based location
        assertTrue(locationInfo.containsKey("city"));
        assertNotNull(locationInfo.get("city"));
    }

    @Test
    @DisplayName("Should maintain backward compatibility methods")
    void shouldMaintainBackwardCompatibility() {
        // Test getBrowserLocation method (backward compatibility)
        Map<String, Object> locationInfo = locationService.getBrowserLocation();
        assertNotNull(locationInfo);
        
        // Test hasLocationData method
        assertTrue(locationService.hasLocationData());
        
        // Test hasValidLocationData method
        assertTrue(locationService.hasValidLocationData());
    }

    @Test
    @DisplayName("Should handle location permission granted flag")
    void shouldHandleLocationPermissionGrantedFlag() {
        // Initially false
        assertFalse(locationService.isLocationPermissionGranted());
        
        // Set permission granted
        locationService.setLocationPermissionGranted(true);
        
        // Should still be false until browser location is set
        assertFalse(locationService.isLocationPermissionGranted());
        
        // Set browser location
        locationService.setBrowserLocation(12.9716, 77.5946, "Bangalore", "Karnataka", "India");
        
        // Now should be true
        assertTrue(locationService.isLocationPermissionGranted());
    }

    @Test
    @DisplayName("Should handle location requested flag")
    void shouldHandleLocationRequestedFlag() {
        // Initially should be true (has IP-based location)
        assertTrue(locationService.isLocationRequested());
        
        // Set requested flag
        locationService.setLocationRequested(false);
        
        // Should still be true because we have location data
        assertTrue(locationService.isLocationRequested());
    }
} 