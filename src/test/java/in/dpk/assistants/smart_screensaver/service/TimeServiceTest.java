package in.dpk.assistants.smart_screensaver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimeServiceTest {

    private TimeService timeService;

    @BeforeEach
    void setUp() {
        timeService = new TimeService();
    }

    @Test
    @DisplayName("Should format time correctly")
    void shouldFormatTimeCorrectly() {
        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        String formattedTime = timeService.formatTime(testDateTime);
        
        assertEquals("14:30", formattedTime);
    }

    @Test
    @DisplayName("Should format date correctly")
    void shouldFormatDateCorrectly() {
        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        String formattedDate = timeService.formatDate(testDateTime);
        
        assertEquals("Monday, January 15", formattedDate);
    }

    @Test
    @DisplayName("Should format timestamp correctly")
    void shouldFormatTimestampCorrectly() {
        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        String formattedTimestamp = timeService.formatTimestamp(testDateTime);
        
        assertEquals("2024-01-15 14:30:45", formattedTimestamp);
    }

    @Test
    @DisplayName("Should get current time")
    void shouldGetCurrentTime() {
        String currentTime = timeService.getCurrentTime();
        
        assertNotNull(currentTime);
        assertTrue(currentTime.matches("\\d{2}:\\d{2}"));
        
        // Verify it's a valid time format
        String[] timeParts = currentTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        assertTrue(hour >= 0 && hour <= 23);
        assertTrue(minute >= 0 && minute <= 59);
    }

    @Test
    @DisplayName("Should get current date")
    void shouldGetCurrentDate() {
        String currentDate = timeService.getCurrentDate();
        
        assertNotNull(currentDate);
        assertTrue(currentDate.matches("\\w+, \\w+ \\d+"));
        
        // Verify it contains day of week, month, and day
        assertTrue(currentDate.contains(","));
        assertTrue(currentDate.split(" ").length >= 3);
    }

    @Test
    @DisplayName("Should get current timestamp")
    void shouldGetCurrentTimestamp() {
        String currentTimestamp = timeService.getCurrentTimestamp();
        
        assertNotNull(currentTimestamp);
        assertTrue(currentTimestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        
        // Verify it's a valid timestamp format
        String[] parts = currentTimestamp.split(" ");
        assertEquals(2, parts.length);
        
        String datePart = parts[0];
        String timePart = parts[1];
        
        assertTrue(datePart.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertTrue(timePart.matches("\\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("Should get current date time")
    void shouldGetCurrentDateTime() {
        LocalDateTime currentDateTime = timeService.getCurrentDateTime();
        
        assertNotNull(currentDateTime);
        
        // Verify it's close to the actual current time (within 1 second)
        LocalDateTime now = LocalDateTime.now();
        assertTrue(Math.abs(currentDateTime.getSecond() - now.getSecond()) <= 1);
    }

    @ParameterizedTest
    @MethodSource("timeFormatProvider")
    @DisplayName("Should format different times correctly")
    void shouldFormatDifferentTimes(LocalDateTime input, String expectedTime) {
        String result = timeService.formatTime(input);
        assertEquals(expectedTime, result);
    }

    static Stream<Arguments> timeFormatProvider() {
        return Stream.of(
            Arguments.of(LocalDateTime.of(2024, 1, 1, 0, 0), "00:00"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 12, 30), "12:30"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 23, 59), "23:59"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 9, 5), "09:05"),
            Arguments.of(LocalDateTime.of(2024, 1, 1, 15, 45), "15:45")
        );
    }

    @ParameterizedTest
    @MethodSource("dateFormatProvider")
    @DisplayName("Should format different dates correctly")
    void shouldFormatDifferentDates(LocalDateTime input, String expectedDate) {
        String result = timeService.formatDate(input);
        assertEquals(expectedDate, result);
    }

    static Stream<Arguments> dateFormatProvider() {
        return Stream.of(
            Arguments.of(LocalDateTime.of(2024, 1, 1, 12, 0), "Monday, January 1"),
            Arguments.of(LocalDateTime.of(2024, 2, 15, 12, 0), "Thursday, February 15"),
            Arguments.of(LocalDateTime.of(2024, 12, 31, 12, 0), "Tuesday, December 31"),
            Arguments.of(LocalDateTime.of(2024, 6, 10, 12, 0), "Monday, June 10"),
            Arguments.of(LocalDateTime.of(2024, 3, 20, 12, 0), "Wednesday, March 20")
        );
    }

    @ParameterizedTest
    @MethodSource("timestampFormatProvider")
    @DisplayName("Should format different timestamps correctly")
    void shouldFormatDifferentTimestamps(LocalDateTime input, String expectedTimestamp) {
        String result = timeService.formatTimestamp(input);
        assertEquals(expectedTimestamp, result);
    }

    static Stream<Arguments> timestampFormatProvider() {
        return Stream.of(
            Arguments.of(LocalDateTime.of(2024, 1, 1, 0, 0, 0), "2024-01-01 00:00:00"),
            Arguments.of(LocalDateTime.of(2024, 12, 31, 23, 59, 59), "2024-12-31 23:59:59"),
            Arguments.of(LocalDateTime.of(2024, 6, 15, 14, 30, 45), "2024-06-15 14:30:45"),
            Arguments.of(LocalDateTime.of(2024, 2, 29, 12, 0, 0), "2024-02-29 12:00:00"),
            Arguments.of(LocalDateTime.of(2024, 6, 4, 18, 30, 15), "2024-06-04 18:30:15")
        );
    }

    @Test
    @DisplayName("Should handle edge case times")
    void shouldHandleEdgeCaseTimes() {
        // Test midnight
        LocalDateTime midnight = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        assertEquals("00:00", timeService.formatTime(midnight));
        
        // Test noon
        LocalDateTime noon = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        assertEquals("12:00", timeService.formatTime(noon));
        
        // Test end of day
        LocalDateTime endOfDay = LocalDateTime.of(2024, 1, 1, 23, 59, 59);
        assertEquals("23:59", timeService.formatTime(endOfDay));
    }

    @Test
    @DisplayName("Should handle leap year dates")
    void shouldHandleLeapYearDates() {
        // Test February 29th in leap year
        LocalDateTime leapYearDate = LocalDateTime.of(2024, 2, 29, 12, 0, 0);
        String formattedDate = timeService.formatDate(leapYearDate);
        assertEquals("Thursday, February 29", formattedDate);
    }

    @Test
    @DisplayName("Should maintain consistency between current time methods")
    void shouldMaintainConsistencyBetweenCurrentTimeMethods() {
        LocalDateTime currentDateTime = timeService.getCurrentDateTime();
        String currentTime = timeService.getCurrentTime();
        String currentDate = timeService.getCurrentDate();
        String currentTimestamp = timeService.getCurrentTimestamp();
        
        // All should be consistent
        assertEquals(timeService.formatTime(currentDateTime), currentTime);
        assertEquals(timeService.formatDate(currentDateTime), currentDate);
        assertEquals(timeService.formatTimestamp(currentDateTime), currentTimestamp);
    }

    @Test
    @DisplayName("Should handle different time zones consistently")
    void shouldHandleDifferentTimeZonesConsistently() {
        // Test that the service handles time consistently regardless of system timezone
        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        
        String formattedTime = timeService.formatTime(testDateTime);
        String formattedDate = timeService.formatDate(testDateTime);
        String formattedTimestamp = timeService.formatTimestamp(testDateTime);
        
        // Should always return the same format regardless of system timezone
        assertEquals("14:30", formattedTime);
        assertEquals("Monday, January 15", formattedDate);
        assertEquals("2024-01-15 14:30:45", formattedTimestamp);
    }
} 