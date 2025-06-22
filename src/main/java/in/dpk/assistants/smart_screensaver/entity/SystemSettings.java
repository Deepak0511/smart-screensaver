package in.dpk.assistants.smart_screensaver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String settingKey;
    
    @Column(columnDefinition = "TEXT")
    private String settingValue;
    
    private String description;
    private String category; // "api", "ui", "system", etc.
    private boolean enabled = true;
    
    // Predefined setting keys
    public static final String WEATHER_API_URL = "weather.api.url";
    public static final String QUOTE_API_URL = "quote.api.url";
    public static final String LOCATION_API_URL = "location.api.url";
    public static final String TRAFFIC_API_URL = "traffic.api.url";
    public static final String WEATHER_API_ENABLED = "weather.api.enabled";
    public static final String QUOTE_API_ENABLED = "quote.api.enabled";
    public static final String LOCATION_API_ENABLED = "location.api.enabled";
    public static final String TRAFFIC_API_ENABLED = "traffic.api.enabled";
    public static final String FALLBACK_MODE = "fallback.mode";
    public static final String API_TIMEOUT = "api.timeout";
    public static final String MAX_RETRIES = "api.max.retries";
    
    // Constructor for easy creation
    public SystemSettings(String key, String value, String description, String category) {
        this.settingKey = key;
        this.settingValue = value;
        this.description = description;
        this.category = category;
    }
} 