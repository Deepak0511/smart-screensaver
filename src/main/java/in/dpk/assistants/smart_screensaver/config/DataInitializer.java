package in.dpk.assistants.smart_screensaver.config;

import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.entity.Routine;
import in.dpk.assistants.smart_screensaver.repository.UserPreferenceRepository;
import in.dpk.assistants.smart_screensaver.repository.RoutineRepository;
import in.dpk.assistants.smart_screensaver.service.SystemSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private RoutineRepository routineRepository;
    
    @Autowired
    private SystemSettingsService systemSettingsService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Initialize system settings
        log.info("Initializing system settings...");
        systemSettingsService.initializeDefaultSettings();
        
        // Initialize user preferences if not exists
        if (userPreferenceRepository.count() == 0) {
            log.info("No user preferences found, creating default user");
            createDefaultUserPreference();
        } else {
            log.info("User preferences already exist in database");
        }
        
        // Initialize routines if not exists
        if (routineRepository.count() == 0) {
            log.info("No routines found, creating default routines");
            createDefaultRoutines();
        } else {
            log.info("Routines already exist in database");
        }
        
        log.info("Data initialization completed successfully");
    }
    
    private void createDefaultUserPreference() {
        UserPreference preference = new UserPreference();
        preference.setUserId("default");
        preference.setDisplayName("User");
        preference.setTimezone("Asia/Kolkata");
        preference.setTheme("dark");
        preference.setRefreshInterval(30);
        preference.setCommuteMode("bus");
        preference.setWorkAddress("Bangalore, India");
        preference.setHomeAddress("Bangalore, India");
        preference.setEnableNotifications(true);
        preference.setEnableLocationServices(false);
        
        UserPreference savedPreference = userPreferenceRepository.save(preference);
        log.info("Created default user preference: {}", savedPreference.getDisplayName());
    }
    
    private void createDefaultRoutines() {
        List<Routine> routines = Arrays.asList(
            createMorningRoutine(),
            createEveningRoutine(),
            createWeekendRoutine()
        );
        
        List<Routine> savedRoutines = routineRepository.saveAll(routines);
        log.info("Created {} default routines", savedRoutines.size());
    }
    
    private Routine createMorningRoutine() {
        Routine routine = new Routine();
        routine.setName("Morning Routine");
        routine.setDescription("Good morning routine for weekdays");
        routine.setStartTime(LocalTime.of(6, 0));
        routine.setEndTime(LocalTime.of(9, 0));
        routine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, 
            Routine.DayType.WEDNESDAY, Routine.DayType.THURSDAY, 
            Routine.DayType.FRIDAY
        ));
        routine.setDayCategory(Routine.DayCategory.WORKDAY);
        routine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_DATE,
            Routine.ActionType.SHOW_QUOTE,
            Routine.ActionType.SHOW_WEATHER,
            Routine.ActionType.SHOW_TRAFFIC
        ));
        routine.setShowWeather(true);
        routine.setShowTraffic(true);
        routine.setShowTime(true);
        routine.setShowDate(true);
        routine.setEnabled(true);
        routine.setPriority(1);
        
        return routine;
    }
    
    private Routine createEveningRoutine() {
        Routine routine = new Routine();
        routine.setName("Evening Routine");
        routine.setDescription("Evening routine with traffic info");
        routine.setStartTime(LocalTime.of(17, 0));
        routine.setEndTime(LocalTime.of(20, 0));
        routine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, 
            Routine.DayType.WEDNESDAY, Routine.DayType.THURSDAY, 
            Routine.DayType.FRIDAY
        ));
        routine.setDayCategory(Routine.DayCategory.WORKDAY);
        routine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_TRAFFIC,
            Routine.ActionType.SHOW_WEATHER
        ));
        routine.setShowTraffic(true);
        routine.setShowWeather(true);
        routine.setShowTime(true);
        routine.setEnabled(true);
        routine.setPriority(1);
        
        return routine;
    }
    
    private Routine createWeekendRoutine() {
        Routine routine = new Routine();
        routine.setName("Weekend Routine");
        routine.setDescription("Relaxed weekend routine");
        routine.setStartTime(LocalTime.of(8, 0));
        routine.setEndTime(LocalTime.of(22, 0));
        routine.setActiveDays(Arrays.asList(
            Routine.DayType.SATURDAY, Routine.DayType.SUNDAY
        ));
        routine.setDayCategory(Routine.DayCategory.WEEKEND);
        routine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_DATE,
            Routine.ActionType.SHOW_QUOTE,
            Routine.ActionType.SHOW_WEATHER
        ));
        routine.setShowWeather(true);
        routine.setShowTime(true);
        routine.setShowDate(true);
        routine.setEnabled(true);
        routine.setPriority(3);
        
        return routine;
    }
} 