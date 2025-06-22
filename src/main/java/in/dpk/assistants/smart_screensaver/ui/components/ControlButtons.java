package in.dpk.assistants.smart_screensaver.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.UI;
import java.util.function.Consumer;

/**
 * Component responsible for managing control buttons (settings, location).
 * Single Responsibility: Handle control button creation and styling.
 */
public class ControlButtons {
    
    private final Button settingsButton;
    private final Button locationButton;
    
    public ControlButtons(Consumer<Void> onLocationRequest) {
        this.settingsButton = createSettingsButton();
        this.locationButton = createLocationButton(onLocationRequest);
    }
    
    private Button createSettingsButton() {
        Button button = new Button("Settings", VaadinIcon.COG.create());
        button.addClassName(LumoUtility.Position.ABSOLUTE);
        button.addClassName("top-4");
        button.addClassName("right-4");
        button.getStyle().set("z-index", "3");
        button.getStyle().set("background", "rgba(255, 255, 255, 0.9)");
        button.getStyle().set("border", "none");
        button.getStyle().set("border-radius", "8px");
        button.getStyle().set("padding", "12px 16px");
        button.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.2)");
        button.addClickListener(e -> UI.getCurrent().navigate("settings"));
        return button;
    }
    
    private Button createLocationButton(Consumer<Void> onLocationRequest) {
        Button button = new Button("Enable Location", VaadinIcon.LOCATION_ARROW.create());
        button.addClassName(LumoUtility.Position.ABSOLUTE);
        button.getStyle().set("position", "fixed");
        button.getStyle().set("bottom", "20px");
        button.getStyle().set("right", "20px");
        button.getStyle().set("z-index", "1000");
        button.getStyle().set("background", "rgba(255, 255, 255, 0.9)");
        button.getStyle().set("border", "none");
        button.getStyle().set("border-radius", "8px");
        button.getStyle().set("padding", "12px 16px");
        button.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.2)");
        button.getStyle().set("font-weight", "bold");
        button.addClickListener(e -> onLocationRequest.accept(null));
        return button;
    }
    
    public void hideLocationButton() {
        locationButton.getStyle().set("display", "none");
    }
    
    public void showLocationButton() {
        locationButton.getStyle().set("display", "block");
    }
    
    public void setLocationButtonVisible(boolean visible) {
        locationButton.getStyle().set("display", visible ? "block" : "none");
    }
    
    // Getters
    public Button getSettingsButton() { return settingsButton; }
    public Button getLocationButton() { return locationButton; }
} 