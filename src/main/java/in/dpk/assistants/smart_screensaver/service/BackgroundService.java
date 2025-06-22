package in.dpk.assistants.smart_screensaver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalTime;

@Service
@Slf4j
public class BackgroundService {
    
    private static final String SUNRISE_IMAGE = "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?w=1920&h=1080&fit=crop&crop=center";
    private static final String MORNING_IMAGE = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&crop=center";
    private static final String AFTERNOON_IMAGE = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1920&h=1080&fit=crop&crop=center";
    private static final String EVENING_IMAGE = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&crop=center";
    private static final String NIGHT_IMAGE = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&crop=center";
    
    public String getBackgroundImageForTime(LocalTime time) {
        int hour = time.getHour();
        
        if (hour >= 5 && hour < 8) {
            log.debug("Using sunrise background for hour: {}", hour);
            return SUNRISE_IMAGE;
        } else if (hour >= 8 && hour < 12) {
            log.debug("Using morning background for hour: {}", hour);
            return MORNING_IMAGE;
        } else if (hour >= 12 && hour < 17) {
            log.debug("Using afternoon background for hour: {}", hour);
            return AFTERNOON_IMAGE;
        } else if (hour >= 17 && hour < 20) {
            log.debug("Using evening background for hour: {}", hour);
            return EVENING_IMAGE;
        } else {
            log.debug("Using night background for hour: {}", hour);
            return NIGHT_IMAGE;
        }
    }
    
    public String getBackgroundImageForCurrentTime() {
        return getBackgroundImageForTime(LocalTime.now());
    }
} 