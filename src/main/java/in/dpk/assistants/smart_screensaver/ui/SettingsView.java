package in.dpk.assistants.smart_screensaver.ui;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import in.dpk.assistants.smart_screensaver.entity.UserPreference;
import in.dpk.assistants.smart_screensaver.entity.Routine;
import in.dpk.assistants.smart_screensaver.service.UserService;
import in.dpk.assistants.smart_screensaver.entity.Routine.DayType;
import in.dpk.assistants.smart_screensaver.entity.Routine.ActionType;

import java.time.LocalTime;
import java.util.*;

@Route("settings")
@PageTitle("Settings - Smart Screensaver")
public class SettingsView extends VerticalLayout {
    
    private UserService userService;
    
    private TextField displayNameField;
    private Select<String> timezoneSelect;
    private Select<String> themeSelect;
    private Select<Integer> refreshIntervalSelect;
    private Checkbox enableNotificationsCheckbox;
    private Checkbox enableLocationServicesCheckbox;
    private TextField workAddressField;
    private TextField homeAddressField;
    private Select<String> commuteModeSelect;
    
    private Grid<Routine> routinesGrid;
    
    public SettingsView(UserService userService) {
        this.userService = userService;
        initUI();
        loadData();
    }
    
    private void initUI() {
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);
        
        // Header
        H1 header = new H1("Settings");
        header.addClassName(LumoUtility.FontSize.XXXLARGE);
        header.addClassName(LumoUtility.FontWeight.BOLD);
        header.addClassName(LumoUtility.Margin.Bottom.LARGE);
        
        // Back button
        Button backButton = new Button("Back to Screensaver", VaadinIcon.ARROW_LEFT.create());
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        backButton.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
        
        // System Settings button
        Button systemSettingsButton = new Button("System Settings", VaadinIcon.COG.create());
        systemSettingsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("system-settings")));
        systemSettingsButton.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
        systemSettingsButton.addClassName(LumoUtility.Margin.Left.MEDIUM);
        systemSettingsButton.addClassName(LumoUtility.Background.CONTRAST_10);
        
        // Button layout
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(backButton, systemSettingsButton);
        buttonLayout.setSpacing(true);
        
        // Main content layout
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        
        // Left panel - User Preferences
        VerticalLayout preferencesPanel = createPreferencesPanel();
        preferencesPanel.addClassName(LumoUtility.Flex.GROW);
        
        // Right panel - Routines Management
        VerticalLayout routinesPanel = createRoutinesPanel();
        routinesPanel.addClassName(LumoUtility.Flex.GROW);
        
        mainLayout.add(preferencesPanel, routinesPanel);
        
        add(buttonLayout, header, mainLayout);
    }
    
    private VerticalLayout createPreferencesPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.addClassName(LumoUtility.Background.CONTRAST_5);
        panel.addClassName(LumoUtility.Padding.LARGE);
        panel.addClassName(LumoUtility.BorderRadius.MEDIUM);
        
        H2 panelHeader = new H2("User Preferences");
        panelHeader.addClassName(LumoUtility.FontSize.XLARGE);
        panelHeader.addClassName(LumoUtility.FontWeight.BOLD);
        panelHeader.addClassName(LumoUtility.Margin.Bottom.LARGE);
        
        // Display Name
        displayNameField = new TextField("Display Name");
        displayNameField.setValue("User");
        displayNameField.setWidthFull();
        
        // Timezone
        timezoneSelect = new Select<>();
        timezoneSelect.setLabel("Timezone");
        timezoneSelect.setItems("UTC", "Asia/Kolkata", "America/New_York", "Europe/London", "Asia/Tokyo");
        timezoneSelect.setValue("Asia/Kolkata");
        timezoneSelect.setWidthFull();
        
        // Theme
        themeSelect = new Select<>();
        themeSelect.setLabel("Theme");
        themeSelect.setItems("Dark", "Light", "Auto");
        themeSelect.setValue("Dark");
        themeSelect.setWidthFull();
        
        // Refresh Interval
        refreshIntervalSelect = new Select<>();
        refreshIntervalSelect.setLabel("Refresh Interval (seconds)");
        refreshIntervalSelect.setItems(10, 30, 60, 120, 300);
        refreshIntervalSelect.setValue(30);
        refreshIntervalSelect.setWidthFull();
        
        // Notifications
        enableNotificationsCheckbox = new Checkbox("Enable Notifications", true);
        
        // Location Services
        enableLocationServicesCheckbox = new Checkbox("Enable Location Services", false);
        
        // Addresses
        workAddressField = new TextField("Work Address");
        workAddressField.setValue("Bangalore, India");
        workAddressField.setWidthFull();
        
        homeAddressField = new TextField("Home Address");
        homeAddressField.setValue("Bangalore, India");
        homeAddressField.setWidthFull();
        
        // Commute Mode
        commuteModeSelect = new Select<>();
        commuteModeSelect.setLabel("Commute Mode");
        commuteModeSelect.setItems("Bus", "Car", "Walk", "Bike", "Train", "Metro");
        commuteModeSelect.setValue("Bus");
        commuteModeSelect.setWidthFull();
        
        // Save button
        Button saveButton = new Button("Save Preferences", VaadinIcon.CHECK.create());
        saveButton.addClickListener(e -> savePreferences());
        saveButton.addClassName(LumoUtility.Margin.Top.MEDIUM);
        
        panel.add(panelHeader, displayNameField, timezoneSelect, themeSelect, 
                 refreshIntervalSelect, enableNotificationsCheckbox, enableLocationServicesCheckbox,
                 workAddressField, homeAddressField, commuteModeSelect, saveButton);
        
        return panel;
    }
    
    private VerticalLayout createRoutinesPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.addClassName(LumoUtility.Background.CONTRAST_5);
        panel.addClassName(LumoUtility.Padding.LARGE);
        panel.addClassName(LumoUtility.BorderRadius.MEDIUM);
        
        H2 panelHeader = new H2("Routines Management");
        panelHeader.addClassName(LumoUtility.FontSize.XLARGE);
        panelHeader.addClassName(LumoUtility.FontWeight.BOLD);
        panelHeader.addClassName(LumoUtility.Margin.Bottom.LARGE);
        
        // Add new routine button
        Button addRoutineButton = new Button("Add New Routine", VaadinIcon.PLUS.create());
        addRoutineButton.addClickListener(e -> showRoutineDialog(null));
        addRoutineButton.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
        
        // Routines grid
        routinesGrid = new Grid<>(Routine.class);
        routinesGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        routinesGrid.setHeight("400px");
        routinesGrid.setWidthFull();
        
        // Configure grid columns
        routinesGrid.removeAllColumns();
        routinesGrid.addColumn(Routine::getName).setHeader("Name").setWidth("150px");
        routinesGrid.addColumn(Routine::getDescription).setHeader("Description").setWidth("200px");
        routinesGrid.addColumn(routine -> routine.getStartTime() + " - " + routine.getEndTime())
                   .setHeader("Time").setWidth("120px");
        routinesGrid.addColumn(Routine::getDayCategory).setHeader("Category").setWidth("100px");
        routinesGrid.addColumn(Routine::isEnabled).setHeader("Enabled").setWidth("80px");
        
        // Action column
        routinesGrid.addComponentColumn(routine -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> showRoutineDialog(routine));
            
            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addClickListener(e -> deleteRoutine(routine));
            
            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Actions").setWidth("100px");
        
        panel.add(panelHeader, addRoutineButton, routinesGrid);
        
        return panel;
    }
    
    private void loadData() {
        // Load user preferences
        UserPreference prefs = userService.getUserPreference();
        if (prefs != null) {
            displayNameField.setValue(prefs.getDisplayName());
            timezoneSelect.setValue(prefs.getTimezone());
            themeSelect.setValue(prefs.getTheme());
            refreshIntervalSelect.setValue(prefs.getRefreshInterval());
            commuteModeSelect.setValue(prefs.getCommuteMode());
            workAddressField.setValue(prefs.getWorkAddress());
            homeAddressField.setValue(prefs.getHomeAddress());
            enableNotificationsCheckbox.setValue(prefs.isEnableNotifications());
            enableLocationServicesCheckbox.setValue(prefs.isEnableLocationServices());
        }
        
        // Load routines
        refreshRoutinesGrid();
    }
    
    private void refreshRoutinesGrid() {
        List<Routine> routines = userService.getAllRoutines();
        routinesGrid.setItems(routines);
    }
    
    private void showRoutineDialog(Routine routine) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(routine == null ? "Add New Routine" : "Edit Routine");
        dialog.setWidth("600px");
        
        VerticalLayout dialogContent = new VerticalLayout();
        dialogContent.setSpacing(true);
        dialogContent.setPadding(true);
        
        // Basic info
        TextField nameField = new TextField("Routine Name");
        nameField.setWidthFull();
        if (routine != null) {
            nameField.setValue(routine.getName());
        }
        
        TextField descriptionField = new TextField("Description");
        descriptionField.setWidthFull();
        if (routine != null) {
            descriptionField.setValue(routine.getDescription());
        }
        
        // Time settings
        TimePicker startTimePicker = new TimePicker("Start Time");
        startTimePicker.setWidthFull();
        if (routine != null) {
            startTimePicker.setValue(routine.getStartTime());
        }
        
        TimePicker endTimePicker = new TimePicker("End Time");
        endTimePicker.setWidthFull();
        if (routine != null) {
            endTimePicker.setValue(routine.getEndTime());
        }
        
        // Day category
        Select<Routine.DayCategory> dayCategorySelect = new Select<>();
        dayCategorySelect.setLabel("Day Category");
        dayCategorySelect.setItems(Routine.DayCategory.values());
        dayCategorySelect.setValue(routine != null ? routine.getDayCategory() : Routine.DayCategory.WORKDAY);
        dayCategorySelect.setWidthFull();
        
        // Active days
        HorizontalLayout daysLayout = new HorizontalLayout();
        daysLayout.setSpacing(true);
        daysLayout.addClassName(LumoUtility.Margin.Top.SMALL);
        
        Span daysLabel = new Span("Active Days:");
        Checkbox mondayCheckbox = new Checkbox("Mon");
        Checkbox tuesdayCheckbox = new Checkbox("Tue");
        Checkbox wednesdayCheckbox = new Checkbox("Wed");
        Checkbox thursdayCheckbox = new Checkbox("Thu");
        Checkbox fridayCheckbox = new Checkbox("Fri");
        Checkbox saturdayCheckbox = new Checkbox("Sat");
        Checkbox sundayCheckbox = new Checkbox("Sun");
        
        // Set default weekdays
        mondayCheckbox.setValue(true);
        tuesdayCheckbox.setValue(true);
        wednesdayCheckbox.setValue(true);
        thursdayCheckbox.setValue(true);
        fridayCheckbox.setValue(true);
        
        daysLayout.add(daysLabel, mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox,
                      thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox);
        
        // Actions
        VerticalLayout actionsLayout = new VerticalLayout();
        actionsLayout.setSpacing(true);
        actionsLayout.addClassName(LumoUtility.Margin.Top.MEDIUM);
        
        Span actionsLabel = new Span("Actions to Perform:");
        Checkbox showGreetingCheckbox = new Checkbox("Show Greeting", true);
        Checkbox showTimeCheckbox = new Checkbox("Show Time", true);
        Checkbox showDateCheckbox = new Checkbox("Show Date", true);
        Checkbox showQuoteCheckbox = new Checkbox("Show Quote", true);
        Checkbox showWeatherCheckbox = new Checkbox("Show Weather", false);
        Checkbox showTrafficCheckbox = new Checkbox("Show Traffic", false);
        Checkbox showLocationCheckbox = new Checkbox("Show Location", false);
        
        actionsLayout.add(actionsLabel, showGreetingCheckbox, showTimeCheckbox, showDateCheckbox,
                         showQuoteCheckbox, showWeatherCheckbox, showTrafficCheckbox, showLocationCheckbox);
        
        // Priority
        Select<Integer> prioritySelect = new Select<>();
        prioritySelect.setLabel("Priority");
        prioritySelect.setItems(1, 2, 3, 4, 5);
        prioritySelect.setValue(routine != null ? routine.getPriority() : 1);
        prioritySelect.setWidthFull();
        
        // Enabled
        Checkbox enabledCheckbox = new Checkbox("Enabled", routine == null || routine.isEnabled());
        
        // Buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.addClassName(LumoUtility.Margin.Top.LARGE);
        
        Button saveButton = new Button("Save", VaadinIcon.CHECK.create());
        saveButton.addClickListener(e -> {
            saveRoutine(nameField.getValue(), descriptionField.getValue(),
                       startTimePicker.getValue(), endTimePicker.getValue(),
                       dayCategorySelect.getValue(), prioritySelect.getValue(),
                       enabledCheckbox.getValue(), routine);
            dialog.close();
        });
        
        Button cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());
        cancelButton.addClickListener(e -> dialog.close());
        
        buttonLayout.add(saveButton, cancelButton);
        
        dialogContent.add(nameField, descriptionField, startTimePicker, endTimePicker,
                         dayCategorySelect, daysLayout, actionsLayout, prioritySelect,
                         enabledCheckbox, buttonLayout);
        
        dialog.add(dialogContent);
        dialog.open();
    }
    
    private void savePreferences() {
        UserPreference prefs = userService.getUserPreference();
        if (prefs == null) {
            prefs = new UserPreference();
            prefs.setUserId("default");
        }
        
        prefs.setDisplayName(displayNameField.getValue());
        prefs.setTimezone(timezoneSelect.getValue());
        prefs.setTheme(themeSelect.getValue());
        prefs.setRefreshInterval(refreshIntervalSelect.getValue());
        prefs.setCommuteMode(commuteModeSelect.getValue());
        prefs.setWorkAddress(workAddressField.getValue());
        prefs.setHomeAddress(homeAddressField.getValue());
        prefs.setEnableNotifications(enableNotificationsCheckbox.getValue());
        prefs.setEnableLocationServices(enableLocationServicesCheckbox.getValue());
        
        userService.updateUserPreference(prefs);
        Notification.show("Preferences saved successfully!", 3000, Notification.Position.TOP_CENTER);
    }
    
    private void saveRoutine(String name, String description, LocalTime startTime, 
                           LocalTime endTime, Routine.DayCategory dayCategory, 
                           Integer priority, Boolean enabled, Routine existingRoutine) {
        if (existingRoutine == null) {
            // Create new routine
            Routine newRoutine = new Routine();
            newRoutine.setName(name);
            newRoutine.setDescription(description);
            newRoutine.setStartTime(startTime);
            newRoutine.setEndTime(endTime);
            newRoutine.setDayCategory(dayCategory);
            newRoutine.setPriority(priority);
            newRoutine.setEnabled(enabled);
            
            // Set default actions
            newRoutine.setActions(Arrays.asList(
                ActionType.SHOW_GREETING,
                ActionType.SHOW_TIME,
                ActionType.SHOW_DATE
            ));
            
            // Set default active days
            newRoutine.setActiveDays(Arrays.asList(
                DayType.MONDAY, DayType.TUESDAY, DayType.WEDNESDAY, 
                DayType.THURSDAY, DayType.FRIDAY
            ));
            
            userService.createRoutine(newRoutine);
        } else {
            // Update existing routine
            existingRoutine.setName(name);
            existingRoutine.setDescription(description);
            existingRoutine.setStartTime(startTime);
            existingRoutine.setEndTime(endTime);
            existingRoutine.setDayCategory(dayCategory);
            existingRoutine.setPriority(priority);
            existingRoutine.setEnabled(enabled);
            
            userService.updateRoutine(existingRoutine.getId(), existingRoutine);
        }
        
        refreshRoutinesGrid();
        Notification.show("Routine saved successfully!", 3000, Notification.Position.TOP_CENTER);
    }
    
    private void deleteRoutine(Routine routine) {
        if (routine != null && routine.getId() != null) {
            userService.deleteRoutine(routine.getId());
            refreshRoutinesGrid();
            Notification.show("Routine deleted successfully!", 3000, Notification.Position.TOP_CENTER);
        }
    }
} 