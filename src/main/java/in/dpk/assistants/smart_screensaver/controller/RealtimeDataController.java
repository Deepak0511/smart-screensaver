package in.dpk.assistants.smart_screensaver.controller;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.communication.PushMode;
import in.dpk.assistants.smart_screensaver.service.ScreensaverService;
import in.dpk.assistants.smart_screensaver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@Slf4j
public class RealtimeDataController {

    @Autowired
    private ScreensaverService screensaverService;
    
    @Autowired
    private UserService userService;

    /**
     * Endpoint to get real-time data (weather, traffic, etc.)
     * This will be called by client-side JavaScript periodically
     */
    @GetMapping("/api/realtime-data")
    @ResponseBody
    public Map<String, Object> getRealtimeData() {
        try {
            Map<String, Object> content = screensaverService.getScreensaverContent();
            
            // Remove time and date from server response since they'll be handled client-side
            content.remove("time");
            content.remove("date");
            
            // Add user preference info
            if (userService.getUserPreference() != null) {
                content.put("userName", userService.getUserPreference().getDisplayName());
            } else {
                content.put("userName", "User");
            }
            
            return content;
        } catch (Exception e) {
            log.error("Error getting real-time data: {}", e.getMessage(), e);
            return Map.of("error", "Failed to load data");
        }
    }
} 