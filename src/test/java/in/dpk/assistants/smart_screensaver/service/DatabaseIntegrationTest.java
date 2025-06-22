package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.entity.Routine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DatabaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Should load user preferences from database")
    void shouldLoadUserPreferencesFromDatabase() {
        UserPreference userPreference = userService.getUserPreference();
        
        assertNotNull(userPreference);
        assertEquals("default", userPreference.getUserId());
        assertEquals("User", userPreference.getDisplayName());
        assertEquals("Asia/Kolkata", userPreference.getTimezone());
        assertEquals("dark", userPreference.getTheme());
        assertEquals(30, userPreference.getRefreshInterval());
    }

    @Test
    @DisplayName("Should load routines from database")
    void shouldLoadRoutinesFromDatabase() {
        List<Routine> routines = userService.getAllRoutines();
        
        assertNotNull(routines);
        assertTrue(routines.size() >= 3); // Should have at least 3 default routines
        
        // Check for specific routines
        boolean hasMorningRoutine = routines.stream()
                .anyMatch(r -> r.getName().equals("Morning Routine"));
        boolean hasEveningRoutine = routines.stream()
                .anyMatch(r -> r.getName().equals("Evening Routine"));
        boolean hasWeekendRoutine = routines.stream()
                .anyMatch(r -> r.getName().equals("Weekend Routine"));
        
        assertTrue(hasMorningRoutine);
        assertTrue(hasEveningRoutine);
        assertTrue(hasWeekendRoutine);
    }

    @Test
    @DisplayName("Should get enabled routines from database")
    void shouldGetEnabledRoutinesFromDatabase() {
        List<Routine> enabledRoutines = userService.getEnabledRoutines();
        
        assertNotNull(enabledRoutines);
        assertTrue(enabledRoutines.size() > 0);
        
        // All returned routines should be enabled
        for (Routine routine : enabledRoutines) {
            assertTrue(routine.isEnabled());
        }
    }

    @Test
    @DisplayName("Should update user preferences in database")
    void shouldUpdateUserPreferencesInDatabase() {
        UserPreference originalPreference = userService.getUserPreference();
        
        // Update the preference
        originalPreference.setDisplayName("Updated User");
        originalPreference.setTheme("light");
        originalPreference.setRefreshInterval(60);
        
        UserPreference updatedPreference = userService.updateUserPreference(originalPreference);
        
        assertNotNull(updatedPreference);
        assertEquals("Updated User", updatedPreference.getDisplayName());
        assertEquals("light", updatedPreference.getTheme());
        assertEquals(60, updatedPreference.getRefreshInterval());
        
        // Verify it's persisted by getting it again
        UserPreference retrievedPreference = userService.getUserPreference();
        assertEquals("Updated User", retrievedPreference.getDisplayName());
    }

    @Test
    @DisplayName("Should create new routine in database")
    void shouldCreateNewRoutineInDatabase() {
        Routine newRoutine = new Routine();
        newRoutine.setName("Test Routine");
        newRoutine.setDescription("A test routine");
        newRoutine.setEnabled(true);
        newRoutine.setPriority(5);
        
        Routine createdRoutine = userService.createRoutine(newRoutine);
        
        assertNotNull(createdRoutine);
        assertNotNull(createdRoutine.getId());
        assertEquals("Test Routine", createdRoutine.getName());
        
        // Verify it's in the database
        Routine retrievedRoutine = userService.getRoutineById(createdRoutine.getId());
        assertNotNull(retrievedRoutine);
        assertEquals("Test Routine", retrievedRoutine.getName());
    }
} 