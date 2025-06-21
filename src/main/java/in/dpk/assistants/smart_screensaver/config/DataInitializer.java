package in.dpk.assistants.smart_screensaver.config;

import in.dpk.assistants.smart_screensaver.entity.Routine;
import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.repository.RoutineRepository;
import in.dpk.assistants.smart_screensaver.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserPreferenceRepository userPreferenceRepository;
    private final RoutineRepository routineRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");
        
        // Create default user preferences
        if (userPreferenceRepository.findByUserId("default").isEmpty()) {
            UserPreference userPref = new UserPreference();
            userPref.setUserId("default");
            userPref.setDisplayName("User");
            userPref.setTimezone("Asia/Kolkata");
            userPref.setTheme("dark");
            userPref.setRefreshInterval(30);
            userPref.setCommuteMode("bus");
            userPref.setWorkAddress("Bangalore, India");
            userPref.setHomeAddress("Bangalore, India");
            
            userPreferenceRepository.save(userPref);
            log.info("Created default user preferences");
        }
        
        // Create sample routines
        if (routineRepository.count() == 0) {
            createSampleRoutines();
            log.info("Created sample routines");
        }
    }
    
    private void createSampleRoutines() {
        // Morning routine (6 AM - 9 AM)
        Routine morningRoutine = new Routine();
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
        
        // Afternoon routine (12 PM - 2 PM)
        Routine afternoonRoutine = new Routine();
        afternoonRoutine.setName("Afternoon Routine");
        afternoonRoutine.setDescription("Lunch break routine");
        afternoonRoutine.setStartTime(LocalTime.of(12, 0));
        afternoonRoutine.setEndTime(LocalTime.of(14, 0));
        afternoonRoutine.setActiveDays(Arrays.asList(
            Routine.DayType.MONDAY, Routine.DayType.TUESDAY, 
            Routine.DayType.WEDNESDAY, Routine.DayType.THURSDAY, 
            Routine.DayType.FRIDAY
        ));
        afternoonRoutine.setDayCategory(Routine.DayCategory.WORKDAY);
        afternoonRoutine.setActions(Arrays.asList(
            Routine.ActionType.SHOW_GREETING,
            Routine.ActionType.SHOW_TIME,
            Routine.ActionType.SHOW_QUOTE
        ));
        afternoonRoutine.setShowTime(true);
        afternoonRoutine.setEnabled(true);
        afternoonRoutine.setPriority(2);
        
        // Evening routine (5 PM - 8 PM)
        Routine eveningRoutine = new Routine();
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
        
        // Save all routines
        routineRepository.saveAll(Arrays.asList(
            morningRoutine, afternoonRoutine, eveningRoutine, weekendRoutine
        ));
    }
} 