package in.dpk.assistants.smart_screensaver.ui.components;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import java.util.Map;

/**
 * Component responsible for displaying screensaver content.
 * Single Responsibility: Handle content display and styling.
 */
public class ContentDisplay {
    
    private final H1 greetingLabel;
    private final H2 timeLabel;
    private final H3 dateLabel;
    private final Paragraph quoteLabel;
    private final Paragraph weatherLabel;
    private final Paragraph trafficLabel;
    
    public ContentDisplay() {
        this.greetingLabel = createGreetingLabel();
        this.timeLabel = createTimeLabel();
        this.dateLabel = createDateLabel();
        this.quoteLabel = createQuoteLabel();
        this.weatherLabel = createWeatherLabel();
        this.trafficLabel = createTrafficLabel();
    }
    
    private H1 createGreetingLabel() {
        H1 label = new H1();
        label.getStyle().set("font-size", "3rem");
        label.getStyle().set("margin-bottom", "1rem");
        label.getStyle().set("text-shadow", "2px 2px 4px rgba(0,0,0,0.5)");
        return label;
    }
    
    private H2 createTimeLabel() {
        H2 label = new H2();
        label.getStyle().set("font-size", "4rem");
        label.getStyle().set("margin-bottom", "0.5rem");
        label.getStyle().set("text-shadow", "2px 2px 4px rgba(0,0,0,0.5)");
        return label;
    }
    
    private H3 createDateLabel() {
        H3 label = new H3();
        label.getStyle().set("font-size", "1.5rem");
        label.getStyle().set("margin-bottom", "2rem");
        label.getStyle().set("text-shadow", "2px 2px 4px rgba(0,0,0,0.5)");
        return label;
    }
    
    private Paragraph createQuoteLabel() {
        Paragraph label = new Paragraph();
        label.getStyle().set("font-size", "1.2rem");
        label.getStyle().set("margin-bottom", "2rem");
        label.getStyle().set("text-shadow", "1px 1px 2px rgba(0,0,0,0.5)");
        label.getStyle().set("font-style", "italic");
        return label;
    }
    
    private Paragraph createWeatherLabel() {
        Paragraph label = new Paragraph();
        label.getStyle().set("font-size", "1.1rem");
        label.getStyle().set("margin-bottom", "1rem");
        label.getStyle().set("text-shadow", "1px 1px 2px rgba(0,0,0,0.5)");
        return label;
    }
    
    private Paragraph createTrafficLabel() {
        Paragraph label = new Paragraph();
        label.getStyle().set("font-size", "1.1rem");
        label.getStyle().set("text-shadow", "1px 1px 2px rgba(0,0,0,0.5)");
        return label;
    }
    
    public void updateContent(Map<String, Object> content, String userName) {
        // Update greeting with user name
        String greeting = (String) content.get("greeting");
        if (greeting != null) {
            greetingLabel.setText(greeting + ", " + userName + "!");
        } else {
            greetingLabel.setText("Hello, " + userName + "!");
        }
        
        // Update time and date
        timeLabel.setText((String) content.get("time"));
        dateLabel.setText((String) content.get("date"));
        
        // Update quote
        quoteLabel.setText((String) content.get("quote"));
        
        // Update weather
        Map<String, Object> weather = (Map<String, Object>) content.get("weather");
        if (weather != null) {
            String location = weather.get("location") != null ? weather.get("location").toString() : "Unknown";
            String temperature = weather.get("temperature") != null ? weather.get("temperature").toString() : "N/A";
            String condition = weather.get("condition") != null ? weather.get("condition").toString() : "N/A";
            String source = weather.get("source") != null ? weather.get("source").toString() : "unknown";
            
            weatherLabel.setText(String.format("Weather: %s, %s (%s)", temperature, condition, location));
        } else {
            weatherLabel.setText("Weather: ---");
        }
        
        // Update traffic
        Map<String, Object> traffic = (Map<String, Object>) content.get("traffic");
        if (traffic != null) {
            String status = traffic.get("status") != null ? traffic.get("status").toString() : "N/A";
            String travelTime = traffic.get("travelTime") != null ? traffic.get("travelTime").toString() : "N/A";
            String location = traffic.get("location") != null ? traffic.get("location").toString() : "Unknown";
            
            trafficLabel.setText(String.format("Traffic: %s (%s) - %s", status, travelTime, location));
        } else {
            trafficLabel.setText("Traffic: ---");
        }
    }
    
    // Getters for components
    public H1 getGreetingLabel() { return greetingLabel; }
    public H2 getTimeLabel() { return timeLabel; }
    public H3 getDateLabel() { return dateLabel; }
    public Paragraph getQuoteLabel() { return quoteLabel; }
    public Paragraph getWeatherLabel() { return weatherLabel; }
    public Paragraph getTrafficLabel() { return trafficLabel; }
} 