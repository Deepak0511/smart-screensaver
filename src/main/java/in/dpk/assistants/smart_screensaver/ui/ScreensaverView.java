package in.dpk.assistants.smart_screensaver.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import in.dpk.assistants.smart_screensaver.service.ScreensaverService;
import in.dpk.assistants.smart_screensaver.service.UserService;
import in.dpk.assistants.smart_screensaver.service.BackgroundService;
import in.dpk.assistants.smart_screensaver.service.GreetingService;
import in.dpk.assistants.smart_screensaver.service.LocationService;
import in.dpk.assistants.smart_screensaver.ui.components.BackgroundContainer;
import in.dpk.assistants.smart_screensaver.ui.components.ContentDisplay;
import in.dpk.assistants.smart_screensaver.ui.components.ControlButtons;
import in.dpk.assistants.smart_screensaver.ui.components.LocationManager;

import java.time.LocalTime;
import java.util.Map;

/**
 * Main screensaver view that orchestrates all components.
 * Single Responsibility: Coordinate between different UI components and services.
 */
@Route("")
@PageTitle("Smart Screensaver")
public class ScreensaverView extends VerticalLayout {
    
    // Services
    private final ScreensaverService screensaverService;
    private final UserService userService;
    private final BackgroundService backgroundService;
    private final GreetingService greetingService;
    private final LocationService locationService;
    
    // UI Components
    private final BackgroundContainer backgroundContainer;
    private final ContentDisplay contentDisplay;
    private final ControlButtons controlButtons;
    private final LocationManager locationManager;
    
    public ScreensaverView(ScreensaverService screensaverService, UserService userService, 
                          BackgroundService backgroundService, GreetingService greetingService,
                          LocationService locationService) {
        this.screensaverService = screensaverService;
        this.userService = userService;
        this.backgroundService = backgroundService;
        this.greetingService = greetingService;
        this.locationService = locationService;
        
        // Initialize components
        this.backgroundContainer = new BackgroundContainer();
        this.contentDisplay = new ContentDisplay();
        this.controlButtons = new ControlButtons(v -> requestLocationPermission());
        this.locationManager = new LocationManager(this);
        
        initUI();
        loadContent();
        checkLocationStatus();
    }
    
    private void initUI() {
        // Set up the main layout
        setSizeFull();
        setPadding(false);
        setMargin(false);
        setSpacing(false);
        
        // Add content to background container
        backgroundContainer.addContent(
            contentDisplay.getGreetingLabel(),
            contentDisplay.getTimeLabel(),
            contentDisplay.getDateLabel(),
            contentDisplay.getQuoteLabel(),
            contentDisplay.getWeatherLabel(),
            contentDisplay.getTrafficLabel()
        );
        
        // Add all components to the main layout
        add(backgroundContainer, controlButtons.getSettingsButton(), controlButtons.getLocationButton());
    }
    
    private void loadContent() {
        Map<String, Object> content = screensaverService.getScreensaverContent();
        updateUI(content);
        updateBackground();
    }
    
    private void updateBackground() {
        LocalTime now = LocalTime.now();
        String backgroundImage = backgroundService.getBackgroundImageForTime(now);
        backgroundContainer.setBackgroundImage(backgroundImage);
    }
    
    private void updateUI(Map<String, Object> content) {
        // Get user name for personalized greeting
        String userName = "User";
        if (userService.getUserPreference() != null) {
            userName = userService.getUserPreference().getDisplayName();
        }
        
        // Update content display
        contentDisplay.updateContent(content, userName);
    }
    
    private void requestLocationPermission() {
        locationManager.requestLocationPermission();
    }
    
    private void checkLocationStatus() {
        locationManager.checkLocationStatus();
    }
    
    public void refreshLocationStatus() {
        locationManager.refreshLocationStatus();
    }
} 