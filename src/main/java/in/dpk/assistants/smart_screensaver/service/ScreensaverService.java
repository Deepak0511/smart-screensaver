package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.entity.Routine;
import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreensaverService {
    
    private final UserService userService;
    private final ExternalDataService externalDataService;
    private final LocationService locationService;
    private final GreetingService greetingService;
    private final TimeService timeService;
    
    public Map<String, Object> getScreensaverContent() {
        Map<String, Object> content = new HashMap<>();
        LocalDateTime now = timeService.getCurrentDateTime();
        
        // Get user preferences
        UserPreference preferences = userService.getUserPreference();
        
        // Get applicable routines
        List<Routine> activeRoutines = userService.getEnabledRoutines();
        
        // Determine day category
        String dayCategory = determineDayCategory(now);
        
        // Build content based on routines
        content.put("timestamp", timeService.formatTimestamp(now));
        content.put("dayCategory", dayCategory);
        content.put("displayName", preferences.getDisplayName());
        
        // Apply routine actions
        for (Routine routine : activeRoutines) {
            applyRoutineActions(routine, content, now);
        }
        
        // Add default content if no routines match
        if (content.size() <= 3) {
            addDefaultContent(content, now);
        }
        
        return content;
    }
    
    private String determineDayCategory(LocalDateTime now) {
        int dayOfWeek = now.getDayOfWeek().getValue();
        
        if (dayOfWeek >= 6) { // Saturday, Sunday
            return "WEEKEND";
        } else {
            return "WORKDAY";
        }
    }
    
    private void applyRoutineActions(Routine routine, Map<String, Object> content, LocalDateTime now) {
        LocalTime currentTime = now.toLocalTime();
        
        // Check if routine is applicable for current time
        if (routine.getStartTime() != null && routine.getEndTime() != null) {
            if (currentTime.isBefore(routine.getStartTime()) || currentTime.isAfter(routine.getEndTime())) {
                return;
            }
        }
        
        // Check day category
        if (routine.getDayCategory() != Routine.DayCategory.ANY) {
            String currentDayCategory = determineDayCategory(now);
            if (!routine.getDayCategory().name().equals(currentDayCategory)) {
                return;
            }
        }
        
        // Apply actions
        for (Routine.ActionType action : routine.getActions()) {
            switch (action) {
                case SHOW_GREETING:
                    content.put("greeting", greetingService.getGreeting(now));
                    break;
                case SHOW_QUOTE:
                    content.put("quote", getQuoteOfTheDay());
                    break;
                case SHOW_TRAFFIC:
                    if (routine.isShowTraffic()) {
                        content.put("traffic", externalDataService.getTrafficInfo());
                    }
                    break;
                case SHOW_WEATHER:
                    if (routine.isShowWeather()) {
                        content.put("weather", externalDataService.getWeatherInfo());
                    }
                    break;
                case SHOW_LOCATION:
                    if (routine.isShowLocation()) {
                        content.put("location", locationService.getLocationInfo());
                    }
                    break;
                case SHOW_TIME:
                    if (routine.isShowTime()) {
                        content.put("time", timeService.formatTime(now));
                    }
                    break;
                case SHOW_DATE:
                    if (routine.isShowDate()) {
                        content.put("date", timeService.formatDate(now));
                    }
                    break;
                case SHOW_CUSTOM_MESSAGE:
                    if (routine.getCustomMessage() != null && !routine.getCustomMessage().isEmpty()) {
                        content.put("customMessage", routine.getCustomMessage());
                    }
                    break;
            }
        }
    }
    
    private void addDefaultContent(Map<String, Object> content, LocalDateTime now) {
        if (!content.containsKey("greeting")) {
            content.put("greeting", greetingService.getGreeting(now));
        }
        if (!content.containsKey("time")) {
            content.put("time", timeService.formatTime(now));
        }
        if (!content.containsKey("date")) {
            content.put("date", timeService.formatDate(now));
        }
    }
    
    private String getQuoteOfTheDay() {
        try {
            Map<String, Object> quoteData = externalDataService.getQuoteOfTheDay();
            String quote = quoteData.get("text").toString();
            String author = quoteData.get("author").toString();
            return quote + " - " + author;
        } catch (Exception e) {
            log.error("Error getting quote: {}", e.getMessage());
            // Fallback to static quotes
            String[] quotes = {
                "The only way to do great work is to love what you do. - Steve Jobs",
                "Life is what happens when you're busy making other plans. - John Lennon",
                "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
                "Success is not final, failure is not fatal: it is the courage to continue that counts. - Winston Churchill",
                "The journey of a thousand miles begins with one step. - Lao Tzu"
            };
            return quotes[new Random().nextInt(quotes.length)];
        }
    }
} 