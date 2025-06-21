package in.dpk.assistants.smart_screensaver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String userId = "default"; // Single user application
    
    private String displayName;
    private String timezone = "UTC";
    private boolean enableNotifications = true;
    private String theme = "dark";
    private int refreshInterval = 30; // seconds
    private boolean enableLocationServices = false;
    private String defaultLocation;
    private String commuteMode = "bus"; // bus, car, walk, etc.
    private String workAddress;
    private String homeAddress;
} 