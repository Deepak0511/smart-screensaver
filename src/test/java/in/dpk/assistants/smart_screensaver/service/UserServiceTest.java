package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.entity.Routine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // The database will be automatically initialized by DataInitializer
        // No need to manually clean up since we're using in-memory database
    }

    @Test
    @DisplayName("Should initialize with default user preferences")
    void shouldInitializeWithDefaultUserPreferences() {
        UserPreference userPreference = userService.getUserPreference();
        
        assertNotNull(userPreference);
        assertEquals("default", userPreference.getUserId());
        assertEquals("User", userPreference.getDisplayName());
        assertEquals("Asia/Kolkata", userPreference.getTimezone());
        assertEquals("dark", userPreference.getTheme());
        assertEquals(30, userPreference.getRefreshInterval());
        assertEquals("bus", userPreference.getCommuteMode());
        assertEquals("Bangalore, India", userPreference.getWorkAddress());
        assertEquals("Bangalore, India", userPreference.getHomeAddress());
        assertTrue(userPreference.isEnableNotifications());
        assertFalse(userPreference.isEnableLocationServices());
    }

    @Test
    @DisplayName("Should update user preferences")
    void shouldUpdateUserPreferences() {
        UserPreference originalPreference = userService.getUserPreference();
        
        // Create updated preference
        UserPreference updatedPreference = new UserPreference();
        updatedPreference.setId(originalPreference.getId()); // Keep the same ID
        updatedPreference.setUserId("test-user");
        updatedPreference.setDisplayName("Test User");
        updatedPreference.setTimezone("America/New_York");
        updatedPreference.setTheme("light");
        updatedPreference.setRefreshInterval(60);
        updatedPreference.setCommuteMode("car");
        updatedPreference.setWorkAddress("New York, USA");
        updatedPreference.setHomeAddress("New Jersey, USA");
        updatedPreference.setEnableNotifications(false);
        updatedPreference.setEnableLocationServices(true);
        
        UserPreference result = userService.updateUserPreference(updatedPreference);
        
        assertNotNull(result);
        assertEquals("test-user", result.getUserId());
        assertEquals("Test User", result.getDisplayName());
        assertEquals("America/New_York", result.getTimezone());
        assertEquals("light", result.getTheme());
        assertEquals(60, result.getRefreshInterval());
        assertEquals("car", result.getCommuteMode());
        assertEquals("New York, USA", result.getWorkAddress());
        assertEquals("New Jersey, USA", result.getHomeAddress());
        assertFalse(result.isEnableNotifications());
        assertTrue(result.isEnableLocationServices());
    }

    @Test
    @DisplayName("Should initialize with default routines")
    void shouldInitializeWithDefaultRoutines() {
        List<Routine> routines = userService.getAllRoutines();
        
        assertNotNull(routines);
        assertTrue(routines.size() >= 3); // Should have at least 3 default routines
        
        // Check for morning routine
        Optional<Routine> morningRoutine = routines.stream()
                .filter(r -> r.getName().equals("Morning Routine"))
                .findFirst();
        assertTrue(morningRoutine.isPresent());
        
        // Check for evening routine
        Optional<Routine> eveningRoutine = routines.stream()
                .filter(r -> r.getName().equals("Evening Routine"))
                .findFirst();
        assertTrue(eveningRoutine.isPresent());
        
        // Check for weekend routine
        Optional<Routine> weekendRoutine = routines.stream()
                .filter(r -> r.getName().equals("Weekend Routine"))
                .findFirst();
        assertTrue(weekendRoutine.isPresent());
    }

    @Test
    @DisplayName("Should get enabled routines only")
    void shouldGetEnabledRoutinesOnly() {
        List<Routine> allRoutines = userService.getAllRoutines();
        List<Routine> enabledRoutines = userService.getEnabledRoutines();
        
        assertNotNull(enabledRoutines);
        assertTrue(enabledRoutines.size() <= allRoutines.size());
        
        // All enabled routines should be enabled
        for (Routine routine : enabledRoutines) {
            assertTrue(routine.isEnabled());
        }
    }

    @Test
    @DisplayName("Should get routine by ID")
    void shouldGetRoutineById() {
        List<Routine> allRoutines = userService.getAllRoutines();
        assertFalse(allRoutines.isEmpty());
        
        Routine firstRoutine = allRoutines.get(0);
        Routine foundRoutine = userService.getRoutineById(firstRoutine.getId());
        
        assertNotNull(foundRoutine);
        assertEquals(firstRoutine.getId(), foundRoutine.getId());
        assertEquals(firstRoutine.getName(), foundRoutine.getName());
    }

    @Test
    @DisplayName("Should return null for non-existent routine ID")
    void shouldReturnNullForNonExistentRoutineId() {
        Routine routine = userService.getRoutineById(999L);
        assertNull(routine);
    }

    @Test
    @DisplayName("Should create new routine")
    void shouldCreateNewRoutine() {
        Routine newRoutine = new Routine();
        newRoutine.setName("Test Routine");
        newRoutine.setDescription("A test routine for testing");
        newRoutine.setStartTime(LocalTime.of(10, 0));
        newRoutine.setEndTime(LocalTime.of(18, 0));
        newRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY
        ));
        newRoutine.setDayCategory(Routine.DayCategory.WORKDAY);
        newRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING, Routine.ActionType.SHOW_TIME
        ));
        newRoutine.setShowWeather(true);
        newRoutine.setShowTime(true);
        newRoutine.setEnabled(true);
        newRoutine.setPriority(5);
        
        Routine createdRoutine = userService.createRoutine(newRoutine);
        
        assertNotNull(createdRoutine);
        assertNotNull(createdRoutine.getId());
        assertEquals("Test Routine", createdRoutine.getName());
        assertEquals("A test routine for testing", createdRoutine.getDescription());
        assertEquals(LocalTime.of(10, 0), createdRoutine.getStartTime());
        assertEquals(LocalTime.of(18, 0), createdRoutine.getEndTime());
        assertEquals(Routine.DayCategory.WORKDAY, createdRoutine.getDayCategory());
        assertTrue(createdRoutine.isEnabled());
        assertEquals(5, createdRoutine.getPriority());
    }

    @Test
    @DisplayName("Should update existing routine")
    void shouldUpdateExistingRoutine() {
        List<Routine> allRoutines = userService.getAllRoutines();
        assertFalse(allRoutines.isEmpty());
        
        Routine originalRoutine = allRoutines.get(0);
        Long routineId = originalRoutine.getId();
        
        // Create updated routine
        Routine updatedRoutine = new Routine();
        updatedRoutine.setId(routineId);
        updatedRoutine.setName("Updated Routine Name");
        updatedRoutine.setDescription("Updated description");
        updatedRoutine.setStartTime(LocalTime.of(9, 0));
        updatedRoutine.setEndTime(LocalTime.of(17, 0));
        updatedRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.WEDNESDAY, Routine.DayType.FRIDAY
        ));
        updatedRoutine.setDayCategory(Routine.DayCategory.WORKDAY);
        updatedRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING, Routine.ActionType.SHOW_WEATHER
        ));
        updatedRoutine.setShowWeather(true);
        updatedRoutine.setShowTime(false);
        updatedRoutine.setEnabled(false);
        updatedRoutine.setPriority(10);
        
        Routine result = userService.updateRoutine(routineId, updatedRoutine);
        
        assertNotNull(result);
        assertEquals(routineId, result.getId());
        assertEquals("Updated Routine Name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(LocalTime.of(9, 0), result.getStartTime());
        assertEquals(LocalTime.of(17, 0), result.getEndTime());
        assertEquals(Routine.DayCategory.WORKDAY, result.getDayCategory());
        assertFalse(result.isEnabled());
        assertEquals(10, result.getPriority());
    }

    @Test
    @DisplayName("Should return null when updating non-existent routine")
    void shouldReturnNullWhenUpdatingNonExistentRoutine() {
        Routine updatedRoutine = new Routine();
        updatedRoutine.setId(999L);
        updatedRoutine.setName("Non-existent Routine");
        
        Routine result = userService.updateRoutine(999L, updatedRoutine);
        assertNull(result);
    }

    @Test
    @DisplayName("Should delete routine")
    void shouldDeleteRoutine() {
        List<Routine> allRoutines = userService.getAllRoutines();
        assertFalse(allRoutines.isEmpty());
        
        Routine routineToDelete = allRoutines.get(0);
        Long routineId = routineToDelete.getId();
        
        boolean deleted = userService.deleteRoutine(routineId);
        assertTrue(deleted);
        
        // Verify routine is deleted
        Routine foundRoutine = userService.getRoutineById(routineId);
        assertNull(foundRoutine);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent routine")
    void shouldReturnFalseWhenDeletingNonExistentRoutine() {
        boolean deleted = userService.deleteRoutine(999L);
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Should handle routine with custom message")
    void shouldHandleRoutineWithCustomMessage() {
        Routine customRoutine = new Routine();
        customRoutine.setName("Custom Message Routine");
        customRoutine.setDescription("Routine with custom message");
        customRoutine.setStartTime(LocalTime.of(8, 0));
        customRoutine.setEndTime(LocalTime.of(20, 0));
        customRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, Routine.DayType.WEDNESDAY,
            Routine.DayType.THURSDAY, Routine.DayType.FRIDAY
        ));
        customRoutine.setDayCategory(Routine.DayCategory.WORKDAY);
        customRoutine.setActions(Arrays.asList(Routine.ActionType.SHOW_CUSTOM_MESSAGE));
        customRoutine.setCustomMessage("Have a great day at work!");
        customRoutine.setEnabled(true);
        customRoutine.setPriority(1);
        
        Routine createdRoutine = userService.createRoutine(customRoutine);
        
        assertNotNull(createdRoutine);
        assertEquals("Custom Message Routine", createdRoutine.getName());
        assertEquals("Have a great day at work!", createdRoutine.getCustomMessage());
        assertTrue(createdRoutine.getActions().contains(Routine.ActionType.SHOW_CUSTOM_MESSAGE));
    }

    @Test
    @DisplayName("Should handle routine with weekend category")
    void shouldHandleRoutineWithWeekendCategory() {
        Routine weekendRoutine = new Routine();
        weekendRoutine.setName("Weekend Relaxation");
        weekendRoutine.setDescription("Relaxing weekend routine");
        weekendRoutine.setStartTime(LocalTime.of(9, 0));
        weekendRoutine.setEndTime(LocalTime.of(22, 0));
        weekendRoutine.setActiveDays(Arrays.asList(Routine.DayType.SATURDAY, Routine.DayType.SUNDAY));
        weekendRoutine.setDayCategory(Routine.DayCategory.WEEKEND);
        weekendRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING, Routine.ActionType.SHOW_QUOTE
        ));
        weekendRoutine.setShowWeather(true);
        weekendRoutine.setShowTime(true);
        weekendRoutine.setEnabled(true);
        weekendRoutine.setPriority(2);
        
        Routine createdRoutine = userService.createRoutine(weekendRoutine);
        
        assertNotNull(createdRoutine);
        assertEquals("Weekend Relaxation", createdRoutine.getName());
        assertEquals(Routine.DayCategory.WEEKEND, createdRoutine.getDayCategory());
        assertTrue(createdRoutine.getActiveDays().contains(Routine.DayType.SATURDAY));
        assertTrue(createdRoutine.getActiveDays().contains(Routine.DayType.SUNDAY));
    }

    @Test
    @DisplayName("Should handle routine with any day category")
    void shouldHandleRoutineWithAnyDayCategory() {
        Routine anyDayRoutine = new Routine();
        anyDayRoutine.setName("Daily Routine");
        anyDayRoutine.setDescription("Routine for any day");
        anyDayRoutine.setStartTime(LocalTime.of(0, 0));
        anyDayRoutine.setEndTime(LocalTime.of(23, 59));
        anyDayRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, Routine.DayType.WEDNESDAY,
            Routine.DayType.THURSDAY, Routine.DayType.FRIDAY, Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        anyDayRoutine.setDayCategory(Routine.DayCategory.ANY);
        anyDayRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING, Routine.ActionType.SHOW_TIME, Routine.ActionType.SHOW_DATE
        ));
        anyDayRoutine.setShowTime(true);
        anyDayRoutine.setShowDate(true);
        anyDayRoutine.setEnabled(true);
        anyDayRoutine.setPriority(1);
        
        Routine createdRoutine = userService.createRoutine(anyDayRoutine);
        
        assertNotNull(createdRoutine);
        assertEquals("Daily Routine", createdRoutine.getName());
        assertEquals(Routine.DayCategory.ANY, createdRoutine.getDayCategory());
        assertEquals(7, createdRoutine.getActiveDays().size());
    }

    @Test
    @DisplayName("Should persist data to database")
    void shouldPersistDataToDatabase() {
        // Update user preference
        UserPreference userPreference = userService.getUserPreference();
        userPreference.setDisplayName("Test User");
        userService.updateUserPreference(userPreference);
        
        // Create a new routine
        Routine newRoutine = new Routine();
        newRoutine.setName("Persistent Test Routine");
        newRoutine.setDescription("Test routine for persistence");
        newRoutine.setStartTime(LocalTime.of(10, 0));
        newRoutine.setEndTime(LocalTime.of(18, 0));
        newRoutine.setActiveDays(Arrays.asList(Routine.DayType.MONDAY));
        newRoutine.setDayCategory(Routine.DayCategory.WORKDAY);
        newRoutine.setActions(Arrays.asList(Routine.ActionType.SHOW_GREETING));
        newRoutine.setEnabled(true);
        newRoutine.setPriority(1);
        
        userService.createRoutine(newRoutine);
        
        // Verify data is persisted by retrieving it
        UserPreference retrievedPreference = userService.getUserPreference();
        assertEquals("Test User", retrievedPreference.getDisplayName());
        
        List<Routine> allRoutines = userService.getAllRoutines();
        boolean foundTestRoutine = allRoutines.stream()
                .anyMatch(r -> r.getName().equals("Persistent Test Routine"));
        assertTrue(foundTestRoutine);
    }
} 