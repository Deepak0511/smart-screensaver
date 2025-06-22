package in.dpk.assistants.smart_screensaver.ui;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import in.dpk.assistants.smart_screensaver.entity.SystemSettings;
import in.dpk.assistants.smart_screensaver.service.SystemSettingsService;

import java.util.List;

@Route("system-settings")
@PageTitle("System Settings - Smart Screensaver")
public class SystemSettingsView extends VerticalLayout {
    
    private final SystemSettingsService systemSettingsService;
    
    // API Settings
    private TextField weatherApiUrlField;
    private Checkbox weatherApiEnabledCheckbox;
    private TextField quoteApiUrlField;
    private Checkbox quoteApiEnabledCheckbox;
    private TextField locationApiUrlField;
    private Checkbox locationApiEnabledCheckbox;
    private TextField trafficApiUrlField;
    private Checkbox trafficApiEnabledCheckbox;
    
    // System Settings
    private Checkbox fallbackModeCheckbox;
    private IntegerField apiTimeoutField;
    private IntegerField maxRetriesField;
    
    public SystemSettingsView(SystemSettingsService systemSettingsService) {
        this.systemSettingsService = systemSettingsService;
        initUI();
        loadSettings();
    }
    
    private void initUI() {
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);
        
        // Header
        H1 header = new H1("System Settings");
        header.addClassName(LumoUtility.FontSize.XXXLARGE);
        header.addClassName(LumoUtility.FontWeight.BOLD);
        header.addClassName(LumoUtility.Margin.Bottom.LARGE);
        
        // Back button
        Button backButton = new Button("Back to Settings", VaadinIcon.ARROW_LEFT.create());
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("settings")));
        backButton.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
        
        // Tabs
        Tabs tabs = new Tabs();
        Tab apiTab = new Tab("API Configuration");
        Tab systemTab = new Tab("System Configuration");
        tabs.add(apiTab, systemTab);
        
        // Tab content
        VerticalLayout apiContent = createApiSettingsPanel();
        VerticalLayout systemContent = createSystemSettingsPanel();
        
        // Tab sheets
        Div apiSheet = new Div(apiContent);
        Div systemSheet = new Div(systemContent);
        
        // Show/hide content based on selected tab
        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == apiTab) {
                apiSheet.setVisible(true);
                systemSheet.setVisible(false);
            } else {
                apiSheet.setVisible(false);
                systemSheet.setVisible(true);
            }
        });
        
        // Initially show API tab
        apiSheet.setVisible(true);
        systemSheet.setVisible(false);
        
        // Save button
        Button saveButton = new Button("Save All Settings", VaadinIcon.CHECK.create());
        saveButton.addClickListener(e -> saveAllSettings());
        saveButton.addClassName(LumoUtility.Margin.Top.LARGE);
        saveButton.addClassName(LumoUtility.Background.PRIMARY);
        saveButton.addClassName(LumoUtility.TextColor.PRIMARY_CONTRAST);
        
        // Reset to defaults button
        Button resetButton = new Button("Reset to Defaults", VaadinIcon.REFRESH.create());
        resetButton.addClickListener(e -> resetToDefaults());
        resetButton.addClassName(LumoUtility.Margin.Top.MEDIUM);
        resetButton.addClassName(LumoUtility.Margin.Bottom.LARGE);
        
        add(backButton, header, tabs, apiSheet, systemSheet, saveButton, resetButton);
    }
    
    private VerticalLayout createApiSettingsPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.addClassName(LumoUtility.Background.CONTRAST_5);
        panel.addClassName(LumoUtility.Padding.LARGE);
        panel.addClassName(LumoUtility.BorderRadius.MEDIUM);
        panel.setSpacing(true);
        
        H2 panelHeader = new H2("External API Configuration");
        panelHeader.addClassName(LumoUtility.FontSize.XLARGE);
        panelHeader.addClassName(LumoUtility.FontWeight.BOLD);
        panelHeader.addClassName(LumoUtility.Margin.Bottom.LARGE);
        
        // Weather API
        H3 weatherHeader = new H3("Weather API");
        weatherHeader.addClassName(LumoUtility.FontSize.LARGE);
        weatherHeader.addClassName(LumoUtility.FontWeight.BOLD);
        
        weatherApiUrlField = new TextField("Weather API URL");
        weatherApiUrlField.setWidthFull();
        weatherApiUrlField.setPlaceholder("https://api.open-meteo.com/v1/forecast");
        
        weatherApiEnabledCheckbox = new Checkbox("Enable Weather API", true);
        
        // Quote API
        H3 quoteHeader = new H3("Quote API");
        quoteHeader.addClassName(LumoUtility.FontSize.LARGE);
        quoteHeader.addClassName(LumoUtility.FontWeight.BOLD);
        
        quoteApiUrlField = new TextField("Quote API URL");
        quoteApiUrlField.setWidthFull();
        quoteApiUrlField.setPlaceholder("https://api.quotable.io/random");
        
        quoteApiEnabledCheckbox = new Checkbox("Enable Quote API", true);
        
        // Location API
        H3 locationHeader = new H3("Location API");
        locationHeader.addClassName(LumoUtility.FontSize.LARGE);
        locationHeader.addClassName(LumoUtility.FontWeight.BOLD);
        
        locationApiUrlField = new TextField("Location API URL");
        locationApiUrlField.setWidthFull();
        locationApiUrlField.setPlaceholder("https://ipapi.co/json/");
        
        locationApiEnabledCheckbox = new Checkbox("Enable Location API", true);
        
        // Traffic API
        H3 trafficHeader = new H3("Traffic API");
        trafficHeader.addClassName(LumoUtility.FontSize.LARGE);
        trafficHeader.addClassName(LumoUtility.FontWeight.BOLD);
        
        trafficApiUrlField = new TextField("Traffic API URL (Optional)");
        trafficApiUrlField.setWidthFull();
        trafficApiUrlField.setPlaceholder("Leave empty to use fallback data");
        
        trafficApiEnabledCheckbox = new Checkbox("Enable Traffic API", false);
        
        // Help text
        Paragraph helpText = new Paragraph(
            "Configure external API endpoints for weather, quotes, location, and traffic data. " +
            "If an API is disabled or unavailable, the system will use fallback data. " +
            "Make sure to use reliable and accessible API endpoints."
        );
        helpText.addClassName(LumoUtility.TextColor.SECONDARY);
        helpText.addClassName(LumoUtility.FontSize.SMALL);
        
        panel.add(panelHeader, 
                 weatherHeader, weatherApiUrlField, weatherApiEnabledCheckbox,
                 quoteHeader, quoteApiUrlField, quoteApiEnabledCheckbox,
                 locationHeader, locationApiUrlField, locationApiEnabledCheckbox,
                 trafficHeader, trafficApiUrlField, trafficApiEnabledCheckbox,
                 helpText);
        
        return panel;
    }
    
    private VerticalLayout createSystemSettingsPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.addClassName(LumoUtility.Background.CONTRAST_5);
        panel.addClassName(LumoUtility.Padding.LARGE);
        panel.addClassName(LumoUtility.BorderRadius.MEDIUM);
        panel.setSpacing(true);
        
        H2 panelHeader = new H2("System Configuration");
        panelHeader.addClassName(LumoUtility.FontSize.XLARGE);
        panelHeader.addClassName(LumoUtility.FontWeight.BOLD);
        panelHeader.addClassName(LumoUtility.Margin.Bottom.LARGE);
        
        // Fallback Mode
        fallbackModeCheckbox = new Checkbox("Enable Fallback Mode", true);
        fallbackModeCheckbox.setHelperText("Show fallback data when APIs are unavailable");
        
        // API Timeout
        apiTimeoutField = new IntegerField("API Timeout (seconds)");
        apiTimeoutField.setMin(1);
        apiTimeoutField.setMax(60);
        apiTimeoutField.setValue(10);
        apiTimeoutField.setWidthFull();
        apiTimeoutField.setHelperText("Maximum time to wait for API responses");
        
        // Max Retries
        maxRetriesField = new IntegerField("Maximum Retries");
        maxRetriesField.setMin(0);
        maxRetriesField.setMax(10);
        maxRetriesField.setValue(3);
        maxRetriesField.setWidthFull();
        maxRetriesField.setHelperText("Number of retry attempts for failed API calls");
        
        // Help text
        Paragraph helpText = new Paragraph(
            "System configuration affects how the application handles API calls and fallback behavior. " +
            "Adjust these settings based on your network conditions and API reliability requirements."
        );
        helpText.addClassName(LumoUtility.TextColor.SECONDARY);
        helpText.addClassName(LumoUtility.FontSize.SMALL);
        
        panel.add(panelHeader, fallbackModeCheckbox, apiTimeoutField, maxRetriesField, helpText);
        
        return panel;
    }
    
    private void loadSettings() {
        // Load API settings
        weatherApiUrlField.setValue(systemSettingsService.getApiUrl("weather"));
        weatherApiEnabledCheckbox.setValue(systemSettingsService.isApiEnabled("weather"));
        
        quoteApiUrlField.setValue(systemSettingsService.getApiUrl("quote"));
        quoteApiEnabledCheckbox.setValue(systemSettingsService.isApiEnabled("quote"));
        
        locationApiUrlField.setValue(systemSettingsService.getApiUrl("location"));
        locationApiEnabledCheckbox.setValue(systemSettingsService.isApiEnabled("location"));
        
        trafficApiUrlField.setValue(systemSettingsService.getApiUrl("traffic"));
        trafficApiEnabledCheckbox.setValue(systemSettingsService.isApiEnabled("traffic"));
        
        // Load system settings
        fallbackModeCheckbox.setValue(systemSettingsService.isFallbackModeEnabled());
        apiTimeoutField.setValue(systemSettingsService.getApiTimeout());
        maxRetriesField.setValue(systemSettingsService.getMaxRetries());
    }
    
    private void saveAllSettings() {
        try {
            // Save API settings
            systemSettingsService.setSetting(SystemSettings.WEATHER_API_URL, 
                weatherApiUrlField.getValue(), "Weather API endpoint URL", "api");
            if (weatherApiEnabledCheckbox.getValue()) {
                systemSettingsService.enableSetting(SystemSettings.WEATHER_API_ENABLED);
            } else {
                systemSettingsService.disableSetting(SystemSettings.WEATHER_API_ENABLED);
            }
            
            systemSettingsService.setSetting(SystemSettings.QUOTE_API_URL, 
                quoteApiUrlField.getValue(), "Quote API endpoint URL", "api");
            if (quoteApiEnabledCheckbox.getValue()) {
                systemSettingsService.enableSetting(SystemSettings.QUOTE_API_ENABLED);
            } else {
                systemSettingsService.disableSetting(SystemSettings.QUOTE_API_ENABLED);
            }
            
            systemSettingsService.setSetting(SystemSettings.LOCATION_API_URL, 
                locationApiUrlField.getValue(), "Location API endpoint URL", "api");
            if (locationApiEnabledCheckbox.getValue()) {
                systemSettingsService.enableSetting(SystemSettings.LOCATION_API_ENABLED);
            } else {
                systemSettingsService.disableSetting(SystemSettings.LOCATION_API_ENABLED);
            }
            
            systemSettingsService.setSetting(SystemSettings.TRAFFIC_API_URL, 
                trafficApiUrlField.getValue(), "Traffic API endpoint URL", "api");
            if (trafficApiEnabledCheckbox.getValue()) {
                systemSettingsService.enableSetting(SystemSettings.TRAFFIC_API_ENABLED);
            } else {
                systemSettingsService.disableSetting(SystemSettings.TRAFFIC_API_ENABLED);
            }
            
            // Save system settings
            systemSettingsService.setSetting(SystemSettings.FALLBACK_MODE, 
                String.valueOf(fallbackModeCheckbox.getValue()), "Enable fallback mode", "system");
            systemSettingsService.setSetting(SystemSettings.API_TIMEOUT, 
                String.valueOf(apiTimeoutField.getValue()), "API timeout in seconds", "system");
            systemSettingsService.setSetting(SystemSettings.MAX_RETRIES, 
                String.valueOf(maxRetriesField.getValue()), "Maximum API retry attempts", "system");
            
            Notification.show("System settings saved successfully!", 3000, Notification.Position.TOP_CENTER);
            
        } catch (Exception e) {
            Notification.show("Error saving settings: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }
    
    private void resetToDefaults() {
        try {
            systemSettingsService.initializeDefaultSettings();
            loadSettings();
            Notification.show("Settings reset to defaults!", 3000, Notification.Position.TOP_CENTER);
        } catch (Exception e) {
            Notification.show("Error resetting settings: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }
} 