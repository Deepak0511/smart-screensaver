package in.dpk.assistants.smart_screensaver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "routines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Routine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    // Time conditions
    private LocalTime startTime;
    private LocalTime endTime;
    
    // Day conditions
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<DayType> activeDays;
    
    // Context conditions
    @Enumerated(EnumType.STRING)
    private DayCategory dayCategory; // WORKDAY, WEEKEND, HOLIDAY, WFH
    
    // Actions to perform
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<ActionType> actions;
    
    // Custom messages and content
    private String customMessage;
    private String quoteSource;
    private boolean showTraffic = false;
    private boolean showWeather = false;
    private boolean showLocation = false;
    private boolean showTime = true;
    private boolean showDate = true;
    
    private boolean enabled = true;
    private int priority = 0;
    
    public enum DayType {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
    
    public enum DayCategory {
        WORKDAY, WEEKEND, HOLIDAY, WFH, ANY
    }
    
    public enum ActionType {
        SHOW_GREETING, SHOW_QUOTE, SHOW_TRAFFIC, SHOW_WEATHER, 
        SHOW_LOCATION, SHOW_TIME, SHOW_DATE, SHOW_CUSTOM_MESSAGE
    }
} 