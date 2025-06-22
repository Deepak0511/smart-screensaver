package in.dpk.assistants.smart_screensaver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.dpk.assistants.smart_screensaver.service.ScreensaverService;
import in.dpk.assistants.smart_screensaver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class WebSocketController {

    @Autowired
    private ScreensaverService screensaverService;
    
    @Autowired
    private UserService userService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Scheduled task to prepare real-time data
     * Note: WebSocket push is handled by client-side polling for better reliability
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    public void prepareRealtimeData() {
        try {
            Map<String, Object> content = screensaverService.getScreensaverContent();
            
            // Remove time and date since they're handled client-side
            content.remove("time");
            content.remove("date");
            
            // Add user preference info
            if (userService.getUserPreference() != null) {
                content.put("userName", userService.getUserPreference().getDisplayName());
            } else {
                content.put("userName", "User");
            }
            
            log.debug("Prepared real-time data for client polling: {}", content.keySet());
            
        } catch (Exception e) {
            log.error("Error preparing real-time data: {}", e.getMessage(), e);
        }
    }
} 