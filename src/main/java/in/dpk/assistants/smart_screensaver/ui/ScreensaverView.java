package in.dpk.assistants.smart_screensaver.ui;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Map;
import java.util.HashMap;

@Route("")
@PageTitle("Smart Screensaver")
public class ScreensaverView extends VerticalLayout {
    
    private H1 greetingLabel;
    private H2 timeLabel;
    private H3 dateLabel;
    private Paragraph quoteLabel;
    private Paragraph weatherLabel;
    private Paragraph trafficLabel;
    private Button settingsButton;
    
    public ScreensaverView() {
        initUI();
        loadContent();
    }
    
    private void initUI() {
        setSizeFull();
        addClassName(LumoUtility.Background.CONTRAST_5);
        addClassName(LumoUtility.Padding.LARGE);
        
        // Create main content layout
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSizeFull();
        mainContent.addClassName(LumoUtility.Display.FLEX);
        mainContent.addClassName(LumoUtility.FlexDirection.COLUMN);
        mainContent.addClassName(LumoUtility.JustifyContent.CENTER);
        mainContent.addClassName(LumoUtility.AlignItems.CENTER);
        
        // Create content labels
        greetingLabel = new H1();
        greetingLabel.addClassName(LumoUtility.FontSize.XXXLARGE);
        greetingLabel.addClassName(LumoUtility.FontWeight.BOLD);
        
        timeLabel = new H2();
        timeLabel.addClassName(LumoUtility.FontSize.XXLARGE);
        
        dateLabel = new H3();
        dateLabel.addClassName(LumoUtility.FontSize.LARGE);
        
        quoteLabel = new Paragraph();
        quoteLabel.addClassName(LumoUtility.FontSize.LARGE);
        
        weatherLabel = new Paragraph();
        weatherLabel.addClassName(LumoUtility.FontSize.MEDIUM);
        
        trafficLabel = new Paragraph();
        trafficLabel.addClassName(LumoUtility.FontSize.MEDIUM);
        
        // Settings button
        settingsButton = new Button(VaadinIcon.COG.create());
        settingsButton.addClickListener(e -> Notification.show("Settings coming soon!"));
        
        // Add components
        mainContent.add(greetingLabel, timeLabel, dateLabel, quoteLabel, 
                       weatherLabel, trafficLabel);
        
        add(mainContent, settingsButton);
    }
    
    private void loadContent() {
        Map<String, Object> content = getMockContent();
        updateUI(content);
    }
    
    private Map<String, Object> getMockContent() {
        Map<String, Object> content = new HashMap<>();
        content.put("greeting", "Good Morning");
        content.put("time", "09:30");
        content.put("date", "Monday, January 15");
        content.put("quote", "The only way to do great work is to love what you do. - Steve Jobs");
        content.put("weather", Map.of("temperature", "22°C", "condition", "Partly Cloudy"));
        content.put("traffic", Map.of("status", "Moderate", "travelTime", "25 min"));
        return content;
    }
    
    private void updateUI(Map<String, Object> content) {
        greetingLabel.setText((String) content.get("greeting"));
        timeLabel.setText((String) content.get("time"));
        dateLabel.setText((String) content.get("date"));
        quoteLabel.setText((String) content.get("quote"));
        
        Map<String, Object> weather = (Map<String, Object>) content.get("weather");
        weatherLabel.setText(String.format("Weather: %s, %s", 
            weather.get("temperature"), weather.get("condition")));
        
        Map<String, Object> traffic = (Map<String, Object>) content.get("traffic");
        trafficLabel.setText(String.format("Traffic: %s (%s)", 
            traffic.get("status"), traffic.get("travelTime")));
    }
} 