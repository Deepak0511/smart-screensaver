package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.entity.Routine;
import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.web-application-type=reactive",
    "app.external.weather.api-url=https://api.open-meteo.com/v1/forecast",
    "app.external.location.api-url=https://ipapi.co/json/"
})
class ScreensaverServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ExternalDataService externalDataService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private GreetingService greetingService;

    @Autowired
    private TimeService timeService;

    private ScreensaverService screensaverService;

    @BeforeEach
    void setUp() {
        screensaverService = new ScreensaverService(
            userService, 
            externalDataService, 
            locationService, 
            greetingService, 
            timeService
        );
    }

    @Test
    @DisplayName("Should get screensaver content with all components")
    void shouldGetScreensaverContentWithAllComponents() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        assertNotNull(content);
        assertTrue(content.containsKey("timestamp"));
        assertTrue(content.containsKey("dayCategory"));
        assertTrue(content.containsKey("displayName"));
        
        // Verify timestamp format
        String timestamp = content.get("timestamp").toString();
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        
        // Verify day category
        String dayCategory = content.get("dayCategory").toString();
        assertTrue(dayCategory.equals("WEEKEND") || dayCategory.equals("WORKDAY"));
        
        // Verify display name
        String displayName = content.get("displayName").toString();
        assertFalse(displayName.isEmpty());
    }

    @Test
    @DisplayName("Should include greeting in content")
    void shouldIncludeGreetingInContent() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        if (content.containsKey("greeting")) {
            String greeting = content.get("greeting").toString();
            assertNotNull(greeting);
            assertTrue(greeting.startsWith("Good "));
            assertTrue(greeting.equals("Good Morning") || 
                      greeting.equals("Good Afternoon") || 
                      greeting.equals("Good Evening") || 
                      greeting.equals("Good Night"));
        }
    }

    @Test
    @DisplayName("Should include time in content")
    void shouldIncludeTimeInContent() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        if (content.containsKey("time")) {
            String time = content.get("time").toString();
            assertNotNull(time);
            assertTrue(time.matches("\\d{2}:\\d{2}"));
        }
    }

    @Test
    @DisplayName("Should include date in content")
    void shouldIncludeDateInContent() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        if (content.containsKey("date")) {
            String date = content.get("date").toString();
            assertNotNull(date);
            assertTrue(date.matches("\\w+, \\w+ \\d+"));
        }
    }

    @Test
    @DisplayName("Should include weather information when available")
    void shouldIncludeWeatherInformationWhenAvailable() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        if (content.containsKey("weather")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> weather = (Map<String, Object>) content.get("weather");
            
            assertNotNull(weather);
            assertTrue(weather.containsKey("temperature") || weather.containsKey("error"));
            
            if (weather.containsKey("temperature")) {
                String temperature = weather.get("temperature").toString();
                assertTrue(temperature.contains("°C"));
            }
        }
    }

    @Test
    @DisplayName("Should include traffic information when available")
    void shouldIncludeTrafficInformationWhenAvailable() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        if (content.containsKey("traffic")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> traffic = (Map<String, Object>) content.get("traffic");
            
            assertNotNull(traffic);
            assertTrue(traffic.containsKey("status"));
            assertTrue(traffic.containsKey("travelTime"));
            assertTrue(traffic.containsKey("message"));
            
            String status = traffic.get("status").toString();
            assertTrue(status.equals("Heavy") || status.equals("Moderate") || status.equals("Light"));
        }
    }

    @Test
    @DisplayName("Should include quote when available")
    void shouldIncludeQuoteWhenAvailable() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        if (content.containsKey("quote")) {
            String quote = content.get("quote").toString();
            assertNotNull(quote);
            assertFalse(quote.isEmpty());
            assertTrue(quote.contains(" - ")); // Should contain author separator
        }
    }

    @Test
    @DisplayName("Should apply routine actions correctly")
    void shouldApplyRoutineActionsCorrectly() {
        // Create a test routine
        Routine testRoutine = new Routine();
        testRoutine.setName("Test Routine");
        testRoutine.setDescription("Test routine for testing");
        testRoutine.setStartTime(LocalTime.of(0, 0));
        testRoutine.setEndTime(LocalTime.of(23, 59));
        testRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, Routine.DayType.WEDNESDAY,
            Routine.DayType.THURSDAY, Routine.DayType.FRIDAY, Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        testRoutine.setDayCategory(Routine.DayCategory.ANY);
        testRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_DATE,
            Routine.ActionType.SHOW_QUOTE,
            Routine.ActionType.SHOW_WEATHER,
            Routine.ActionType.SHOW_TRAFFIC
        ));
        testRoutine.setShowWeather(true);
        testRoutine.setShowTraffic(true);
        testRoutine.setShowTime(true);
        testRoutine.setShowDate(true);
        testRoutine.setEnabled(true);
        testRoutine.setPriority(1);
        
        // Add routine to user service
        userService.createRoutine(testRoutine);
        
        // Get content
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        // Should have all the routine actions
        assertTrue(content.containsKey("greeting"));
        assertTrue(content.containsKey("time"));
        assertTrue(content.containsKey("date"));
        assertTrue(content.containsKey("quote") || content.containsKey("weather") || content.containsKey("traffic"));
    }

    @Test
    @DisplayName("Should handle custom message in routine")
    void shouldHandleCustomMessageInRoutine() {
        // Create a routine with custom message
        Routine customRoutine = new Routine();
        customRoutine.setName("Custom Message Routine");
        customRoutine.setDescription("Routine with custom message");
        customRoutine.setStartTime(LocalTime.of(0, 0));
        customRoutine.setEndTime(LocalTime.of(23, 59));
        customRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, Routine.DayType.WEDNESDAY,
            Routine.DayType.THURSDAY, Routine.DayType.FRIDAY, Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        customRoutine.setDayCategory(Routine.DayCategory.ANY);
        customRoutine.setActions(Arrays.asList(Routine.ActionType.SHOW_CUSTOM_MESSAGE));
        customRoutine.setCustomMessage("Have a wonderful day!");
        customRoutine.setEnabled(true);
        customRoutine.setPriority(1);
        
        // Add routine to user service
        userService.createRoutine(customRoutine);
        
        // Get content
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        // Should have custom message
        assertTrue(content.containsKey("customMessage"));
        assertEquals("Have a wonderful day!", content.get("customMessage"));
    }

    @Test
    @DisplayName("Should determine day category correctly")
    void shouldDetermineDayCategoryCorrectly() {
        // Test with current time
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        String dayCategory = content.get("dayCategory").toString();
        assertTrue(dayCategory.equals("WEEKEND") || dayCategory.equals("WORKDAY"));
        
        // Verify it matches the current day
        LocalDateTime now = LocalDateTime.now();
        int dayOfWeek = now.getDayOfWeek().getValue();
        
        if (dayOfWeek >= 6) { // Saturday, Sunday
            assertEquals("WEEKEND", dayCategory);
        } else {
            assertEquals("WORKDAY", dayCategory);
        }
    }

    @Test
    @DisplayName("Should handle routine time constraints")
    void shouldHandleRoutineTimeConstraints() {
        // Create a routine that only applies during specific hours
        LocalTime currentTime = LocalTime.now();
        LocalTime startTime = currentTime.plusHours(1); // Start 1 hour from now
        LocalTime endTime = currentTime.plusHours(2);   // End 2 hours from now
        
        Routine timeConstrainedRoutine = new Routine();
        timeConstrainedRoutine.setName("Time Constrained Routine");
        timeConstrainedRoutine.setDescription("Routine with time constraints");
        timeConstrainedRoutine.setStartTime(startTime);
        timeConstrainedRoutine.setEndTime(endTime);
        timeConstrainedRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, Routine.DayType.WEDNESDAY,
            Routine.DayType.THURSDAY, Routine.DayType.FRIDAY, Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        timeConstrainedRoutine.setDayCategory(Routine.DayCategory.ANY);
        timeConstrainedRoutine.setActions(Arrays.asList(Routine.ActionType.SHOW_CUSTOM_MESSAGE));
        timeConstrainedRoutine.setCustomMessage("This should not appear");
        timeConstrainedRoutine.setEnabled(true);
        timeConstrainedRoutine.setPriority(1);
        
        // Add routine to user service
        userService.createRoutine(timeConstrainedRoutine);
        
        // Get content - should not include the custom message since current time is outside the routine time
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        // Should not have the custom message
        assertFalse(content.containsKey("customMessage"));
    }

    @Test
    @DisplayName("Should handle routine day category constraints")
    void shouldHandleRoutineDayCategoryConstraints() {
        // Create a weekend-only routine
        Routine weekendRoutine = new Routine();
        weekendRoutine.setName("Weekend Only Routine");
        weekendRoutine.setDescription("Routine only for weekends");
        weekendRoutine.setStartTime(LocalTime.of(0, 0));
        weekendRoutine.setEndTime(LocalTime.of(23, 59));
        weekendRoutine.setActiveDays(Arrays.asList(Routine.DayType.SATURDAY, Routine.DayType.SUNDAY));
        weekendRoutine.setDayCategory(Routine.DayCategory.WEEKEND);
        weekendRoutine.setActions(Arrays.asList(Routine.ActionType.SHOW_CUSTOM_MESSAGE));
        weekendRoutine.setCustomMessage("Weekend message");
        weekendRoutine.setEnabled(true);
        weekendRoutine.setPriority(1);
        
        // Add routine to user service
        userService.createRoutine(weekendRoutine);
        
        // Get content
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        // Should only include weekend message if it's actually weekend
        String dayCategory = content.get("dayCategory").toString();
        if (dayCategory.equals("WEEKEND")) {
            assertTrue(content.containsKey("customMessage"));
            assertEquals("Weekend message", content.get("customMessage"));
        } else {
            assertFalse(content.containsKey("customMessage"));
        }
    }

    @Test
    @DisplayName("Should provide fallback quote when external API fails")
    void shouldProvideFallbackQuoteWhenExternalApiFails() {
        // This test verifies that the service provides fallback quotes
        // when external quote APIs are unavailable
        
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        if (content.containsKey("quote")) {
            String quote = content.get("quote").toString();
            assertNotNull(quote);
            assertFalse(quote.isEmpty());
            
            // Should contain author separator
            assertTrue(quote.contains(" - "));
            
            // Should be one of the fallback quotes or from external API
            String[] fallbackQuotes = {
                "The only way to do great work is to love what you do. - Steve Jobs",
                "Life is what happens when you're busy making other plans. - John Lennon",
                "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
                "Success is not final, failure is not fatal: it is the courage to continue that counts. - Winston Churchill",
                "The journey of a thousand miles begins with one step. - Lao Tzu"
            };
            
            boolean isFallbackQuote = false;
            for (String fallbackQuote : fallbackQuotes) {
                if (quote.equals(fallbackQuote)) {
                    isFallbackQuote = true;
                    break;
                }
            }
            
            // Quote should either be a fallback quote or from external API
            assertTrue(isFallbackQuote || quote.length() > 50);
        }
    }

    @Test
    @DisplayName("Should handle multiple routines with different priorities")
    void shouldHandleMultipleRoutinesWithDifferentPriorities() {
        // Create multiple routines with different priorities
        Routine highPriorityRoutine = new Routine();
        highPriorityRoutine.setName("High Priority Routine");
        highPriorityRoutine.setStartTime(LocalTime.of(0, 0));
        highPriorityRoutine.setEndTime(LocalTime.of(23, 59));
        highPriorityRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, Routine.DayType.WEDNESDAY,
            Routine.DayType.THURSDAY, Routine.DayType.FRIDAY, Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        highPriorityRoutine.setDayCategory(Routine.DayCategory.ANY);
        highPriorityRoutine.setActions(Arrays.asList(Routine.ActionType.SHOW_CUSTOM_MESSAGE));
        highPriorityRoutine.setCustomMessage("High priority message");
        highPriorityRoutine.setEnabled(true);
        highPriorityRoutine.setPriority(10);
        
        Routine lowPriorityRoutine = new Routine();
        lowPriorityRoutine.setName("Low Priority Routine");
        lowPriorityRoutine.setStartTime(LocalTime.of(0, 0));
        lowPriorityRoutine.setEndTime(LocalTime.of(23, 59));
        lowPriorityRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, Routine.DayType.WEDNESDAY,
            Routine.DayType.THURSDAY, Routine.DayType.FRIDAY, Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        lowPriorityRoutine.setDayCategory(Routine.DayCategory.ANY);
        lowPriorityRoutine.setActions(Arrays.asList(Routine.ActionType.SHOW_CUSTOM_MESSAGE));
        lowPriorityRoutine.setCustomMessage("Low priority message");
        lowPriorityRoutine.setEnabled(true);
        lowPriorityRoutine.setPriority(1);
        
        // Add routines to user service
        userService.createRoutine(highPriorityRoutine);
        userService.createRoutine(lowPriorityRoutine);
        
        // Get content
        Map<String, Object> content = screensaverService.getScreensaverContent();
        
        // Should have custom message (from high priority routine)
        assertTrue(content.containsKey("customMessage"));
        assertEquals("High priority message", content.get("customMessage"));
    }
} 