package in.dpk.assistants.smart_screensaver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GreetingServiceTest {

    private GreetingService greetingService;

    @BeforeEach
    void setUp() {
        greetingService = new GreetingService();
    }

    @Test
    @DisplayName("Should return Good Morning for morning hours")
    void shouldReturnGoodMorningForMorningHours() {
        LocalDateTime morningTime = LocalDateTime.of(2024, 1, 1, 9, 0);
        String greeting = greetingService.getGreeting(morningTime);
        
        assertEquals("Good Morning", greeting);
    }

    @Test
    @DisplayName("Should return Good Afternoon for afternoon hours")
    void shouldReturnGoodAfternoonForAfternoonHours() {
        LocalDateTime afternoonTime = LocalDateTime.of(2024, 1, 1, 14, 0);
        String greeting = greetingService.getGreeting(afternoonTime);
        
        assertEquals("Good Afternoon", greeting);
    }

    @Test
    @DisplayName("Should return Good Evening for evening hours")
    void shouldReturnGoodEveningForEveningHours() {
        LocalDateTime eveningTime = LocalDateTime.of(2024, 1, 1, 19, 0);
        String greeting = greetingService.getGreeting(eveningTime);
        
        assertEquals("Good Evening", greeting);
    }

    @Test
    @DisplayName("Should return Good Night for night hours")
    void shouldReturnGoodNightForNightHours() {
        LocalDateTime nightTime = LocalDateTime.of(2024, 1, 1, 23, 0);
        String greeting = greetingService.getGreeting(nightTime);
        
        assertEquals("Good Night", greeting);
    }

    @Test
    @DisplayName("Should return Good Night for early morning hours")
    void shouldReturnGoodNightForEarlyMorningHours() {
        LocalDateTime earlyMorningTime = LocalDateTime.of(2024, 1, 1, 3, 0);
        String greeting = greetingService.getGreeting(earlyMorningTime);
        
        assertEquals("Good Night", greeting);
    }

    @ParameterizedTest
    @MethodSource("greetingTimeProvider")
    @DisplayName("Should return correct greeting for different times")
    void shouldReturnCorrectGreetingForDifferentTimes(LocalDateTime time, String expectedGreeting) {
        String greeting = greetingService.getGreeting(time);
        assertEquals(expectedGreeting, greeting);
    }

    static Stream<Arguments> greetingTimeProvider() {
        return Stream.of(
            // Night hours (0-5)
            Arguments.of(LocalDateTime.of(2024, 1, 1, 0, 0), "Good Night"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 2, 30), "Good Night"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 5, 59), "Good Night"),
            
            // Morning hours (6-11)
            Arguments.of(LocalDateTime.of(2024, 1, 1, 6, 0), "Good Morning"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 8, 15), "Good Morning"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 11, 59), "Good Morning"),
            
            // Afternoon hours (12-16)
            Arguments.of(LocalDateTime.of(2024, 1, 1, 12, 0), "Good Afternoon"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 14, 30), "Good Afternoon"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 16, 59), "Good Afternoon"),
            
            // Evening hours (17-20)
            Arguments.of(LocalDateTime.of(2024, 1, 1, 17, 0), "Good Evening"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 18, 45), "Good Evening"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 20, 59), "Good Evening"),
            
            // Night hours (21-23)
            Arguments.of(LocalDateTime.of(2024, 1, 1, 21, 0), "Good Night"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 22, 30), "Good Night"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 23, 59), "Good Night")
        );
    }

    @Test
    @DisplayName("Should return greeting with name")
    void shouldReturnGreetingWithName() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 10, 0);
        String userName = "John";
        
        String greeting = greetingService.getGreetingWithName(time, userName);
        
        assertEquals("Good Morning, John!", greeting);
    }

    @Test
    @DisplayName("Should handle empty user name")
    void shouldHandleEmptyUserName() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 15, 0);
        String userName = "";
        
        String greeting = greetingService.getGreetingWithName(time, userName);
        
        assertEquals("Good Afternoon, !", greeting);
    }

    @Test
    @DisplayName("Should handle null user name")
    void shouldHandleNullUserName() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 20, 0);
        String userName = null;
        
        String greeting = greetingService.getGreetingWithName(time, userName);
        
        assertEquals("Good Evening, null!", greeting);
    }

    @Test
    @DisplayName("Should get current greeting")
    void shouldGetCurrentGreeting() {
        String currentGreeting = greetingService.getCurrentGreeting();
        
        assertNotNull(currentGreeting);
        assertTrue(currentGreeting.startsWith("Good "));
        assertTrue(currentGreeting.equals("Good Morning") || 
                  currentGreeting.equals("Good Afternoon") || 
                  currentGreeting.equals("Good Evening") || 
                  currentGreeting.equals("Good Night"));
    }

    @Test
    @DisplayName("Should get current greeting with name")
    void shouldGetCurrentGreetingWithName() {
        String userName = "Alice";
        String currentGreeting = greetingService.getCurrentGreetingWithName(userName);
        
        assertNotNull(currentGreeting);
        assertTrue(currentGreeting.startsWith("Good "));
        assertTrue(currentGreeting.endsWith(", Alice!"));
    }

    @Test
    @DisplayName("Should handle time boundaries correctly")
    void shouldHandleTimeBoundariesCorrectly() {
        // Test boundary times
        assertEquals("Good Night", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 5, 59)));
        assertEquals("Good Morning", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 6, 0)));
        assertEquals("Good Morning", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 11, 59)));
        assertEquals("Good Afternoon", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 12, 0)));
        assertEquals("Good Afternoon", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 16, 59)));
        assertEquals("Good Evening", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 17, 0)));
        assertEquals("Good Evening", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 20, 59)));
        assertEquals("Good Night", greetingService.getGreeting(LocalDateTime.of(2024, 1, 1, 21, 0)));
    }

    @Test
    @DisplayName("Should handle different days consistently")
    void shouldHandleDifferentDaysConsistently() {
        // Test that greeting is consistent across different days
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 12, 31, 10, 0);
        
        String greeting1 = greetingService.getGreeting(time1);
        String greeting2 = greetingService.getGreeting(time2);
        
        assertEquals(greeting1, greeting2);
    }

    @Test
    @DisplayName("Should handle leap year dates")
    void shouldHandleLeapYearDates() {
        // Test February 29th in leap year
        LocalDateTime leapYearTime = LocalDateTime.of(2024, 2, 29, 14, 0);
        String greeting = greetingService.getGreeting(leapYearTime);
        
        assertEquals("Good Afternoon", greeting);
    }

    @Test
    @DisplayName("Should handle special characters in user name")
    void shouldHandleSpecialCharactersInUserName() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 12, 0);
        String userName = "José María";
        
        String greeting = greetingService.getGreetingWithName(time, userName);
        
        assertEquals("Good Afternoon, José María!", greeting);
    }

    @Test
    @DisplayName("Should handle long user names")
    void shouldHandleLongUserNames() {
        LocalDateTime time = LocalDateTime.of(2024, 1, 1, 18, 0);
        String userName = "Very Long User Name With Many Words";
        
        String greeting = greetingService.getGreetingWithName(time, userName);
        
        assertEquals("Good Evening, Very Long User Name With Many Words!", greeting);
    }

    @Test
    @DisplayName("Should maintain consistency between current time methods")
    void shouldMaintainConsistencyBetweenCurrentTimeMethods() {
        String currentGreeting = greetingService.getCurrentGreeting();
        String currentGreetingWithName = greetingService.getCurrentGreetingWithName("Test");
        
        // The greeting part should be the same
        assertTrue(currentGreetingWithName.startsWith(currentGreeting));
        assertTrue(currentGreetingWithName.endsWith(", Test!"));
    }
} 