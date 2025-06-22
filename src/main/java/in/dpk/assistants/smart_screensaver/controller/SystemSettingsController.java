package in.dpk.assistants.smart_screensaver.controller;

import in.dpk.assistants.smart_screensaver.entity.SystemSettings;
import in.dpk.assistants.smart_screensaver.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/system-settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class SystemSettingsController {
    
    private final SystemSettingsService systemSettingsService;
    
    @GetMapping
    public ResponseEntity<List<SystemSettings>> getAllSettings() {
        List<SystemSettings> settings = systemSettingsService.getAllSettings();
        return ResponseEntity.ok(settings);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SystemSettings>> getSettingsByCategory(@PathVariable String category) {
        List<SystemSettings> settings = systemSettingsService.getSettingsByCategory(category);
        return ResponseEntity.ok(settings);
    }
    
    @GetMapping("/enabled")
    public ResponseEntity<List<SystemSettings>> getEnabledSettings() {
        List<SystemSettings> settings = systemSettingsService.getEnabledSettings();
        return ResponseEntity.ok(settings);
    }
    
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, String>> getSettingValue(@PathVariable String key) {
        String value = systemSettingsService.getSettingValue(key, "");
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<SystemSettings> createSetting(@RequestBody SystemSettings setting) {
        systemSettingsService.setSetting(setting.getSettingKey(), setting.getSettingValue(), 
                                       setting.getDescription(), setting.getCategory());
        return ResponseEntity.ok(setting);
    }
    
    @PutMapping("/{key}")
    public ResponseEntity<Map<String, String>> updateSetting(@PathVariable String key, 
                                                           @RequestBody Map<String, String> request) {
        String value = request.get("value");
        String description = request.getOrDefault("description", "");
        String category = request.getOrDefault("category", "system");
        
        systemSettingsService.setSetting(key, value, description, category);
        
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        response.put("message", "Setting updated successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{key}/enable")
    public ResponseEntity<Map<String, String>> enableSetting(@PathVariable String key) {
        systemSettingsService.enableSetting(key);
        
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("message", "Setting enabled successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{key}/disable")
    public ResponseEntity<Map<String, String>> disableSetting(@PathVariable String key) {
        systemSettingsService.disableSetting(key);
        
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("message", "Setting disabled successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeDefaultSettings() {
        systemSettingsService.initializeDefaultSettings();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Default settings initialized successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api-status")
    public ResponseEntity<Map<String, Object>> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("weather", Map.of(
            "enabled", systemSettingsService.isApiEnabled("weather"),
            "url", systemSettingsService.getApiUrl("weather")
        ));
        
        status.put("quote", Map.of(
            "enabled", systemSettingsService.isApiEnabled("quote"),
            "url", systemSettingsService.getApiUrl("quote")
        ));
        
        status.put("location", Map.of(
            "enabled", systemSettingsService.isApiEnabled("location"),
            "url", systemSettingsService.getApiUrl("location")
        ));
        
        status.put("traffic", Map.of(
            "enabled", systemSettingsService.isApiEnabled("traffic"),
            "url", systemSettingsService.getApiUrl("traffic")
        ));
        
        status.put("system", Map.of(
            "fallbackMode", systemSettingsService.isFallbackModeEnabled(),
            "timeout", systemSettingsService.getApiTimeout(),
            "maxRetries", systemSettingsService.getMaxRetries()
        ));
        
        return ResponseEntity.ok(status);
    }
} 