package in.dpk.assistants.smart_screensaver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class GreetingService {
    
    public String getGreeting(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        
        if (hour < 6) {
            return "Good Night";
        } else if (hour < 12) {
            return "Good Morning";
        } else if (hour < 17) {
            return "Good Afternoon";
        } else if (hour < 21) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }
    
    public String getGreetingWithName(LocalDateTime dateTime, String userName) {
        String greeting = getGreeting(dateTime);
        return greeting + ", " + userName + "!";
    }
    
    public String getCurrentGreeting() {
        return getGreeting(LocalDateTime.now());
    }
    
    public String getCurrentGreetingWithName(String userName) {
        return getGreetingWithName(LocalDateTime.now(), userName);
    }
} 