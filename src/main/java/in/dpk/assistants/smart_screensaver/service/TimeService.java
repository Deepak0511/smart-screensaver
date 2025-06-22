package in.dpk.assistants.smart_screensaver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TimeService {
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMMM d");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public String formatTime(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMATTER);
    }
    
    public String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }
    
    public String formatTimestamp(LocalDateTime dateTime) {
        return dateTime.format(TIMESTAMP_FORMATTER);
    }
    
    public String getCurrentTime() {
        return formatTime(LocalDateTime.now());
    }
    
    public String getCurrentDate() {
        return formatDate(LocalDateTime.now());
    }
    
    public String getCurrentTimestamp() {
        return formatTimestamp(LocalDateTime.now());
    }
    
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
} 