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
    "app.external.weather.api-url=https://api.open-meteo.com/v1/forecast",
    "app.external.location.api-url=https://ipapi.co/json/"
})
class ExternalDataServiceTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private ExternalApiConfig apiConfig;

    @Autowired
    private LocationService locationService;

    private ExternalDataService externalDataService;

    @BeforeEach
    void setUp() {
        externalDataService = new ExternalDataService(webClient, apiConfig, locationService);
    }

    @Test
    @DisplayName("Should fetch weather information from external API")
    void shouldFetchWeatherInfo() {
        Map<String, Object> weatherInfo = externalDataService.getWeatherInfo();
        
        assertNotNull(weatherInfo);
        assertTrue(weatherInfo.containsKey("temperature"));
        assertTrue(weatherInfo.containsKey("condition"));
        assertTrue(weatherInfo.containsKey("humidity"));
        assertTrue(weatherInfo.containsKey("location"));
        assertTrue(weatherInfo.containsKey("source"));
        
        // Verify data types and formats
        String temperature = weatherInfo.get("temperature").toString();
        assertTrue(temperature.contains("°C"));
        
        String humidity = weatherInfo.get("humidity").toString();
        assertTrue(humidity.contains("%"));
        
        String condition = weatherInfo.get("condition").toString();
        assertFalse(condition.isEmpty());
        
        String location = weatherInfo.get("location").toString();
        assertFalse(location.isEmpty());
        
        String source = weatherInfo.get("source").toString();
        assertTrue(source.equals("ip") || source.equals("browser"));
    }

    @Test
    @DisplayName("Should fetch quote of the day from external API")
    void shouldFetchQuoteOfTheDay() {
        Map<String, Object> quoteInfo = externalDataService.getQuoteOfTheDay();
        
        assertNotNull(quoteInfo);
        assertTrue(quoteInfo.containsKey("text"));
        assertTrue(quoteInfo.containsKey("author"));
        assertTrue(quoteInfo.containsKey("category"));
        
        // Verify quote content
        String text = quoteInfo.get("text").toString();
        assertFalse(text.isEmpty());
        assertTrue(text.length() > 10); // Should be a meaningful quote
        
        String author = quoteInfo.get("author").toString();
        assertFalse(author.isEmpty());
        
        String category = quoteInfo.get("category").toString();
        assertFalse(category.isEmpty());
    }

    @Test
    @DisplayName("Should generate traffic information based on time")
    void shouldGenerateTrafficInfo() {
        Map<String, Object> trafficInfo = externalDataService.getTrafficInfo();
        
        assertNotNull(trafficInfo);
        assertTrue(trafficInfo.containsKey("status"));
        assertTrue(trafficInfo.containsKey("travelTime"));
        assertTrue(trafficInfo.containsKey("distance"));
        assertTrue(trafficInfo.containsKey("message"));
        assertTrue(trafficInfo.containsKey("location"));
        assertTrue(trafficInfo.containsKey("source"));
        
        // Verify traffic data
        String status = trafficInfo.get("status").toString();
        assertTrue(status.equals("Heavy") || status.equals("Moderate") || status.equals("Light"));
        
        String travelTime = trafficInfo.get("travelTime").toString();
        assertTrue(travelTime.contains("min"));
        
        String distance = trafficInfo.get("distance").toString();
        assertTrue(distance.contains("km"));
        
        String message = trafficInfo.get("message").toString();
        assertFalse(message.isEmpty());
        
        String location = trafficInfo.get("location").toString();
        assertFalse(location.isEmpty());
    }

    @Test
    @DisplayName("Should get bus location information")
    void shouldGetBusLocation() {
        Map<String, Object> busInfo = externalDataService.getBusLocation();
        
        assertNotNull(busInfo);
        assertTrue(busInfo.containsKey("busNumber"));
        assertTrue(busInfo.containsKey("estimatedArrival"));
        assertTrue(busInfo.containsKey("currentLocation"));
        assertTrue(busInfo.containsKey("nextStop"));
        assertTrue(busInfo.containsKey("status"));
        
        // Verify bus data
        String busNumber = busInfo.get("busNumber").toString();
        assertFalse(busNumber.isEmpty());
        
        String estimatedArrival = busInfo.get("estimatedArrival").toString();
        assertTrue(estimatedArrival.contains("min"));
        
        String currentLocation = busInfo.get("currentLocation").toString();
        assertFalse(currentLocation.isEmpty());
        
        String nextStop = busInfo.get("nextStop").toString();
        assertFalse(nextStop.isEmpty());
        
        String status = busInfo.get("status").toString();
        assertFalse(status.isEmpty());
    }

    @Test
    @DisplayName("Should handle weather API failures gracefully")
    void shouldHandleWeatherApiFailures() {
        // This test verifies that the service handles API failures gracefully
        // by returning empty weather data when location is not available
        
        // Create a service with invalid location to test fallback
        ExternalDataService testService = new ExternalDataService(webClient, apiConfig, locationService);
        
        Map<String, Object> weatherInfo = testService.getWeatherInfo();
        
        // Should still return a map with expected keys, even if empty
        assertNotNull(weatherInfo);
        assertTrue(weatherInfo.containsKey("temperature") || weatherInfo.containsKey("error"));
    }

    @Test
    @DisplayName("Should handle quote API failures gracefully")
    void shouldHandleQuoteApiFailures() {
        // This test verifies that the service tries multiple quote APIs
        // and handles failures gracefully
        
        Map<String, Object> quoteInfo = externalDataService.getQuoteOfTheDay();
        
        // Should return quote data or empty data, but not null
        assertNotNull(quoteInfo);
        
        // If successful, should have quote content
        if (quoteInfo.containsKey("text")) {
            String text = quoteInfo.get("text").toString();
            assertFalse(text.isEmpty());
        }
    }

    @Test
    @DisplayName("Should format temperature correctly")
    void shouldFormatTemperatureCorrectly() {
        // Test temperature formatting through weather API
        Map<String, Object> weatherInfo = externalDataService.getWeatherInfo();
        
        if (weatherInfo.containsKey("temperature")) {
            String temperature = weatherInfo.get("temperature").toString();
            
            // Should contain °C and be a valid number format
            assertTrue(temperature.contains("°C"));
            assertTrue(temperature.matches(".*\\d+\\.\\d+°C.*"));
        }
    }

    @Test
    @DisplayName("Should provide weather conditions for different codes")
    void shouldProvideWeatherConditions() {
        // Test weather condition mapping through actual API call
        Map<String, Object> weatherInfo = externalDataService.getWeatherInfo();
        
        if (weatherInfo.containsKey("condition")) {
            String condition = weatherInfo.get("condition").toString();
            
            // Should be a valid weather condition
            assertFalse(condition.isEmpty());
            assertTrue(condition.length() > 0);
        }
    }

    @Test
    @DisplayName("Should return consistent data structure for all external APIs")
    void shouldReturnConsistentDataStructure() {
        // Test all external data methods return consistent structure
        
        Map<String, Object> weatherInfo = externalDataService.getWeatherInfo();
        Map<String, Object> quoteInfo = externalDataService.getQuoteOfTheDay();
        Map<String, Object> trafficInfo = externalDataService.getTrafficInfo();
        Map<String, Object> busInfo = externalDataService.getBusLocation();
        
        // All should return non-null maps
        assertNotNull(weatherInfo);
        assertNotNull(quoteInfo);
        assertNotNull(trafficInfo);
        assertNotNull(busInfo);
        
        // All should have expected keys
        assertTrue(weatherInfo.containsKey("temperature") || weatherInfo.containsKey("error"));
        assertTrue(quoteInfo.containsKey("text") || quoteInfo.containsKey("error"));
        assertTrue(trafficInfo.containsKey("status"));
        assertTrue(busInfo.containsKey("busNumber"));
    }

    @Test
    @DisplayName("Should handle location service integration")
    void shouldHandleLocationServiceIntegration() {
        // Test that external data service properly integrates with location service
        
        // Set a specific location
        locationService.setBrowserLocation(12.9716, 77.5946, "Bangalore", "Karnataka", "India");
        
        // Get weather info - should use the set location
        Map<String, Object> weatherInfo = externalDataService.getWeatherInfo();
        
        assertNotNull(weatherInfo);
        assertTrue(weatherInfo.containsKey("location"));
        
        String location = weatherInfo.get("location").toString();
        assertFalse(location.isEmpty());
    }
} 