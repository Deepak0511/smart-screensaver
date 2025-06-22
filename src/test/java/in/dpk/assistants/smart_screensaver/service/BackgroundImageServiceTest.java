package in.dpk.assistants.smart_screensaver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import in.dpk.assistants.smart_screensaver.entity.UserPreference;

class BackgroundImageServiceTest {

    private BackgroundImageService backgroundImageService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        backgroundImageService = new BackgroundImageService();
        userService = new UserService();
    }

    @Test
    @DisplayName("Should return sunrise image for early morning hours (5-7)")
    void shouldReturnSunriseImageForEarlyMorning() {
        // Test sunrise hours (5-7 AM)
        LocalTime sunriseTime = LocalTime.of(6, 30);
        String result = backgroundImageService.getBackgroundImageForTime(sunriseTime);
        
        assertNotNull(result);
        assertTrue(result.contains("unsplash.com"));
        assertTrue(result.contains("photo-1470252649378-9c29740c9fa8"));
    }

    @Test
    @DisplayName("Should return morning image for morning hours (8-11)")
    void shouldReturnMorningImageForMorning() {
        // Test morning hours (8-11 AM)
        LocalTime morningTime = LocalTime.of(9, 15);
        String result = backgroundImageService.getBackgroundImageForTime(morningTime);
        
        assertNotNull(result);
        assertTrue(result.contains("unsplash.com"));
        assertTrue(result.contains("photo-1506905925346-21bda4d32df4"));
    }

    @Test
    @DisplayName("Should return afternoon image for afternoon hours (12-16)")
    void shouldReturnAfternoonImageForAfternoon() {
        // Test afternoon hours (12-4 PM)
        LocalTime afternoonTime = LocalTime.of(14, 30);
        String result = backgroundImageService.getBackgroundImageForTime(afternoonTime);
        
        assertNotNull(result);
        assertTrue(result.contains("unsplash.com"));
        assertTrue(result.contains("photo-1441974231531-c6227db76b6e"));
    }

    @Test
    @DisplayName("Should return evening image for evening hours (17-19)")
    void shouldReturnEveningImageForEvening() {
        // Test evening hours (5-7 PM)
        LocalTime eveningTime = LocalTime.of(18, 45);
        String result = backgroundImageService.getBackgroundImageForTime(eveningTime);
        
        assertNotNull(result);
        assertTrue(result.contains("unsplash.com"));
        assertTrue(result.contains("photo-1506905925346-21bda4d32df4"));
    }

    @Test
    @DisplayName("Should return night image for night hours (20-4)")
    void shouldReturnNightImageForNight() {
        // Test night hours (8 PM - 4 AM)
        LocalTime nightTime = LocalTime.of(23, 30);
        String result = backgroundImageService.getBackgroundImageForTime(nightTime);
        
        assertNotNull(result);
        assertTrue(result.contains("unsplash.com"));
        assertTrue(result.contains("photo-1506905925346-21bda4d32df4"));
    }

    @ParameterizedTest
    @MethodSource("timeBoundaryProvider")
    @DisplayName("Should handle time boundaries correctly")
    void shouldHandleTimeBoundaries(LocalTime time, String expectedImageType) {
        String result = backgroundImageService.getBackgroundImageForTime(time);
        
        assertNotNull(result);
        assertTrue(result.contains("unsplash.com"));
        
        // Verify the correct image type based on expected
        if ("sunrise".equals(expectedImageType)) {
            assertTrue(result.contains("photo-1470252649378-9c29740c9fa8"));
        } else if ("morning".equals(expectedImageType)) {
            assertTrue(result.contains("photo-1506905925346-21bda4d32df4"));
        } else if ("afternoon".equals(expectedImageType)) {
            assertTrue(result.contains("photo-1441974231531-c6227db76b6e"));
        } else if ("evening".equals(expectedImageType)) {
            assertTrue(result.contains("photo-1506905925346-21bda4d32df4"));
        } else if ("night".equals(expectedImageType)) {
            assertTrue(result.contains("photo-1506905925346-21bda4d32df4"));
        }
    }

    static Stream<Arguments> timeBoundaryProvider() {
        return Stream.of(
            Arguments.of(LocalTime.of(5, 0), "sunrise"),   // Sunrise start
            Arguments.of(LocalTime.of(7, 59), "sunrise"),  // Sunrise end
            Arguments.of(LocalTime.of(8, 0), "morning"),   // Morning start
            Arguments.of(LocalTime.of(11, 59), "morning"), // Morning end
            Arguments.of(LocalTime.of(12, 0), "afternoon"), // Afternoon start
            Arguments.of(LocalTime.of(16, 59), "afternoon"), // Afternoon end
            Arguments.of(LocalTime.of(17, 0), "evening"),   // Evening start
            Arguments.of(LocalTime.of(19, 59), "evening"),  // Evening end
            Arguments.of(LocalTime.of(20, 0), "night"),     // Night start
            Arguments.of(LocalTime.of(4, 59), "night")      // Night end
        );
    }

    @Test
    @DisplayName("Should return current time background image")
    void shouldReturnCurrentTimeBackgroundImage() {
        String result = backgroundImageService.getBackgroundImageForCurrentTime();
        
        assertNotNull(result);
        assertTrue(result.contains("unsplash.com"));
        assertTrue(result.contains("w=1920&h=1080&fit=crop&crop=center"));
    }

    @Test
    @DisplayName("Should return valid URL format for all time periods")
    void shouldReturnValidUrlFormat() {
        // Test all time periods
        LocalTime[] testTimes = {
            LocalTime.of(6, 0),   // Sunrise
            LocalTime.of(10, 0),  // Morning
            LocalTime.of(15, 0),  // Afternoon
            LocalTime.of(18, 0),  // Evening
            LocalTime.of(22, 0)   // Night
        };
        
        for (LocalTime time : testTimes) {
            String result = backgroundImageService.getBackgroundImageForTime(time);
            
            assertNotNull(result);
            assertTrue(result.startsWith("https://"));
            assertTrue(result.contains("unsplash.com"));
            assertTrue(result.contains("w=1920&h=1080&fit=crop&crop=center"));
        }
    }

    @Test
    @DisplayName("Should initialize with default user preferences")
    void shouldInitializeWithDefaultUserPreferences() {
        UserPreference userPreference = userService.getUserPreference();
        assertNotNull(userPreference);
        assertEquals("default", userPreference.getUserId());
        assertEquals("User", userPreference.getDisplayName());
        // ...other assertions
    }
} 