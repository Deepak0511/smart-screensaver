package in.dpk.assistants.smart_screensaver.service;

import in.dpk.assistants.smart_screensaver.entity.SystemSettings;
import in.dpk.assistants.smart_screensaver.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemSettingsService {
    
    private final SystemSettingsRepository systemSettingsRepository;
    
    public String getSettingValue(String key, String defaultValue) {
        Optional<String> value = systemSettingsRepository.findValueByKey(key);
        return value.orElse(defaultValue);
    }
    
    public boolean getBooleanSetting(String key, boolean defaultValue) {
        String value = getSettingValue(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }
    
    public int getIntSetting(String key, int defaultValue) {
        String value = getSettingValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for setting {}: {}", key, value);
            return defaultValue;
        }
    }
    
    public void setSetting(String key, String value, String description, String category) {
        Optional<SystemSettings> existing = systemSettingsRepository.findBySettingKey(key);
        
        if (existing.isPresent()) {
            SystemSettings setting = existing.get();
            setting.setSettingValue(value);
            setting.setDescription(description);
            setting.setCategory(category);
            systemSettingsRepository.save(setting);
            log.info("Updated system setting: {} = {}", key, value);
        } else {
            SystemSettings setting = new SystemSettings(key, value, description, category);
            systemSettingsRepository.save(setting);
            log.info("Created system setting: {} = {}", key, value);
        }
    }
    
    public void setSetting(String key, String value) {
        setSetting(key, value, "", "system");
    }
    
    public void enableSetting(String key) {
        Optional<SystemSettings> setting = systemSettingsRepository.findBySettingKey(key);
        if (setting.isPresent()) {
            setting.get().setEnabled(true);
            systemSettingsRepository.save(setting.get());
            log.info("Enabled system setting: {}", key);
        }
    }
    
    public void disableSetting(String key) {
        Optional<SystemSettings> setting = systemSettingsRepository.findBySettingKey(key);
        if (setting.isPresent()) {
            setting.get().setEnabled(false);
            systemSettingsRepository.save(setting.get());
            log.info("Disabled system setting: {}", key);
        }
    }
    
    public List<SystemSettings> getAllSettings() {
        return systemSettingsRepository.findAll();
    }
    
    public List<SystemSettings> getSettingsByCategory(String category) {
        return systemSettingsRepository.findByCategory(category);
    }
    
    public List<SystemSettings> getEnabledSettings() {
        return systemSettingsRepository.findByEnabled(true);
    }
    
    public void initializeDefaultSettings() {
        // Weather API settings
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.WEATHER_API_URL)) {
            setSetting(SystemSettings.WEATHER_API_URL, 
                      "https://api.open-meteo.com/v1/forecast",
                      "Weather API endpoint URL",
                      "api");
        }
        
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.WEATHER_API_ENABLED)) {
            setSetting(SystemSettings.WEATHER_API_ENABLED, 
                      "true",
                      "Enable weather API calls",
                      "api");
        }
        
        // Quote API settings
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.QUOTE_API_URL)) {
            setSetting(SystemSettings.QUOTE_API_URL, 
                      "https://api.quotable.io/random",
                      "Quote API endpoint URL",
                      "api");
        }
        
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.QUOTE_API_ENABLED)) {
            setSetting(SystemSettings.QUOTE_API_ENABLED, 
                      "true",
                      "Enable quote API calls",
                      "api");
        }
        
        // Location API settings
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.LOCATION_API_URL)) {
            setSetting(SystemSettings.LOCATION_API_URL, 
                      "https://ipapi.co/json/",
                      "Location API endpoint URL",
                      "api");
        }
        
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.LOCATION_API_ENABLED)) {
            setSetting(SystemSettings.LOCATION_API_ENABLED, 
                      "true",
                      "Enable location API calls",
                      "api");
        }
        
        // Traffic API settings
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.TRAFFIC_API_URL)) {
            setSetting(SystemSettings.TRAFFIC_API_URL, 
                      "",
                      "Traffic API endpoint URL (optional)",
                      "api");
        }
        
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.TRAFFIC_API_ENABLED)) {
            setSetting(SystemSettings.TRAFFIC_API_ENABLED, 
                      "false",
                      "Enable traffic API calls",
                      "api");
        }
        
        // System settings
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.FALLBACK_MODE)) {
            setSetting(SystemSettings.FALLBACK_MODE, 
                      "true",
                      "Enable fallback mode when APIs are unavailable",
                      "system");
        }
        
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.API_TIMEOUT)) {
            setSetting(SystemSettings.API_TIMEOUT, 
                      "10",
                      "API timeout in seconds",
                      "system");
        }
        
        if (!systemSettingsRepository.existsBySettingKey(SystemSettings.MAX_RETRIES)) {
            setSetting(SystemSettings.MAX_RETRIES, 
                      "3",
                      "Maximum API retry attempts",
                      "system");
        }
        
        log.info("Initialized default system settings");
    }
    
    public boolean isApiEnabled(String apiType) {
        String key = apiType + ".api.enabled";
        return getBooleanSetting(key, true);
    }
    
    public String getApiUrl(String apiType) {
        String key = apiType + ".api.url";
        return getSettingValue(key, "");
    }
    
    public int getApiTimeout() {
        return getIntSetting(SystemSettings.API_TIMEOUT, 10);
    }
    
    public int getMaxRetries() {
        return getIntSetting(SystemSettings.MAX_RETRIES, 3);
    }
    
    public boolean isFallbackModeEnabled() {
        return getBooleanSetting(SystemSettings.FALLBACK_MODE, true);
    }
} 