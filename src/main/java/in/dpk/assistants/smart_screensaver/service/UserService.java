package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.entity.Routine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.time.LocalTime;

@Service
@Slf4j
public class UserService {
    
    private static final String DATA_FILE = "screensaver_data.json";
    private static final String ROUTINES_FILE = "routines_data.json";
    
    private UserPreference userPreference;
    private List<Routine> routines = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public UserService() {
        // Configure ObjectMapper for Java 8 time types
        objectMapper.registerModule(new JavaTimeModule());
        loadData();
        if (userPreference == null) {
            initializeDefaultData();
        }
    }
    
    private void loadData() {
        try {
            // Load user preferences
            File userFile = new File(DATA_FILE);
            if (userFile.exists()) {
                userPreference = objectMapper.readValue(userFile, UserPreference.class);
                log.info("Loaded user preferences from file");
            }
            
            // Load routines
            File routinesFile = new File(ROUTINES_FILE);
            if (routinesFile.exists()) {
                CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, Routine.class);
                routines = objectMapper.readValue(routinesFile, listType);
                log.info("Loaded {} routines from file", routines.size());
            }
        } catch (IOException e) {
            log.error("Error loading data from file", e);
        }
    }
    
    private void saveData() {
        try {
            // Save user preferences
            if (userPreference != null) {
                objectMapper.writeValue(new File(DATA_FILE), userPreference);
            }
            
            // Save routines
            objectMapper.writeValue(new File(ROUTINES_FILE), routines);
            
            log.info("Data saved to file successfully");
        } catch (IOException e) {
            log.error("Error saving data to file", e);
        }
    }
    
    private void initializeDefaultData() {
        // Initialize default user preferences
        userPreference = new UserPreference();
        userPreference.setUserId("default");
        userPreference.setDisplayName("User");
        userPreference.setTimezone("Asia/Kolkata");
        userPreference.setTheme("dark");
        userPreference.setRefreshInterval(30);
        userPreference.setCommuteMode("bus");
        userPreference.setWorkAddress("Bangalore, India");
        userPreference.setHomeAddress("Bangalore, India");
        userPreference.setEnableNotifications(true);
        userPreference.setEnableLocationServices(false);
        
        // Initialize sample routines
        createSampleRoutines();
        
        // Save default data
        saveData();
    }
    
    private void createSampleRoutines() {
        // Morning routine (6 AM - 9 AM)
        Routine morningRoutine = new Routine();
        morningRoutine.setId(1L);
        morningRoutine.setName("Morning Routine");
        morningRoutine.setDescription("Good morning routine for weekdays");
        morningRoutine.setStartTime(LocalTime.of(6, 0));
        morningRoutine.setEndTime(LocalTime.of(9, 0));
        morningRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, 
            Routine.DayType.WEDNESDAY, Routine.DayType.THURSDAY, 
            Routine.DayType.FRIDAY
        ));
        morningRoutine.setDayCategory(Routine.DayCategory.WORKDAY);
        morningRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_DATE,
            Routine.ActionType.SHOW_QUOTE,
            Routine.ActionType.SHOW_WEATHER,
            Routine.ActionType.SHOW_TRAFFIC
        ));
        morningRoutine.setShowWeather(true);
        morningRoutine.setShowTraffic(true);
        morningRoutine.setShowTime(true);
        morningRoutine.setShowDate(true);
        morningRoutine.setEnabled(true);
        morningRoutine.setPriority(1);
        
        // Evening routine (5 PM - 8 PM)
        Routine eveningRoutine = new Routine();
        eveningRoutine.setId(2L);
        eveningRoutine.setName("Evening Routine");
        eveningRoutine.setDescription("Evening routine with traffic info");
        eveningRoutine.setStartTime(LocalTime.of(17, 0));
        eveningRoutine.setEndTime(LocalTime.of(20, 0));
        eveningRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, 
            Routine.DayType.WEDNESDAY, Routine.DayType.THURSDAY, 
            Routine.DayType.FRIDAY
        ));
        eveningRoutine.setDayCategory(Routine.DayCategory.WORKDAY);
        eveningRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_TRAFFIC,
            Routine.ActionType.SHOW_WEATHER
        ));
        eveningRoutine.setShowTraffic(true);
        eveningRoutine.setShowWeather(true);
        eveningRoutine.setShowTime(true);
        eveningRoutine.setEnabled(true);
        eveningRoutine.setPriority(1);
        
        // Weekend routine
        Routine weekendRoutine = new Routine();
        weekendRoutine.setId(3L);
        weekendRoutine.setName("Weekend Routine");
        weekendRoutine.setDescription("Relaxed weekend routine");
        weekendRoutine.setStartTime(LocalTime.of(8, 0));
        weekendRoutine.setEndTime(LocalTime.of(22, 0));
        weekendRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        weekendRoutine.setDayCategory(Routine.DayCategory.WEEKEND);
        weekendRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_DATE,
            Routine.ActionType.SHOW_QUOTE,
            Routine.ActionType.SHOW_WEATHER
        ));
        weekendRoutine.setShowWeather(true);
        weekendRoutine.setShowTime(true);
        weekendRoutine.setShowDate(true);
        weekendRoutine.setEnabled(true);
        weekendRoutine.setPriority(3);
        
        routines.addAll(Arrays.asList(morningRoutine, eveningRoutine, weekendRoutine));
    }
    
    // User Preference methods
    public UserPreference getUserPreference() {
        return userPreference;
    }
    
    public UserPreference updateUserPreference(UserPreference updatedPreference) {
        this.userPreference = updatedPreference;
        saveData();
        log.info("User preferences updated: {}", updatedPreference.getDisplayName());
        return userPreference;
    }
    
    // Routine methods
    public List<Routine> getAllRoutines() {
        return new ArrayList<>(routines);
    }
    
    public List<Routine> getEnabledRoutines() {
        return routines.stream()
                .filter(Routine::isEnabled)
                .sorted((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()))
                .toList();
    }
    
    public Routine getRoutineById(Long id) {
        return routines.stream()
                .filter(routine -> routine.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public Routine createRoutine(Routine routine) {
        routine.setId(generateNextId());
        routines.add(routine);
        saveData();
        log.info("Created new routine: {}", routine.getName());
        return routine;
    }
    
    public Routine updateRoutine(Long id, Routine updatedRoutine) {
        Routine existingRoutine = getRoutineById(id);
        if (existingRoutine != null) {
            updatedRoutine.setId(id);
            routines.remove(existingRoutine);
            routines.add(updatedRoutine);
            saveData();
            log.info("Updated routine: {}", updatedRoutine.getName());
            return updatedRoutine;
        }
        return null;
    }
    
    public boolean deleteRoutine(Long id) {
        Routine routine = getRoutineById(id);
        if (routine != null) {
            routines.remove(routine);
            saveData();
            log.info("Deleted routine: {}", routine.getName());
            return true;
        }
        return false;
    }
    
    private Long generateNextId() {
        return routines.stream()
                .mapToLong(Routine::getId)
                .max()
                .orElse(0) + 1;
    }
} 