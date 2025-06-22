package in.dpk.assistants.smart_screensaver.ui.components;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import java.util.Map;

/**
 * Component responsible for displaying screensaver content.
 * Single Responsibility: Handle content display and styling.
 */
public class ContentDisplay {
    
    private final H1 greetingLabel;
    private final Div clockContainer;
    private final H2 timeLabel;
    private final Span secondsLabel;
    private final H3 dateLabel;
    private final Paragraph quoteLabel;
    private final Paragraph weatherLabel;
    private final Paragraph trafficLabel;
    
    public ContentDisplay() {
        this.greetingLabel = createGreetingLabel();
        this.clockContainer = createClockContainer();
        this.timeLabel = createTimeLabel();
        this.secondsLabel = createSecondsLabel();
        this.dateLabel = createDateLabel();
        this.quoteLabel = createQuoteLabel();
        this.weatherLabel = createWeatherLabel();
        this.trafficLabel = createTrafficLabel();
        
        // Add time and seconds to clock container
        this.clockContainer.add(this.timeLabel, this.secondsLabel);
    }
    
    private H1 createGreetingLabel() {
        H1 label = new H1();
        label.getStyle().set("font-size", "3rem");
        label.getStyle().set("margin-bottom", "1rem");
        label.getStyle().set("text-shadow", "0 2px 8px rgba(0,0,0,0.4)");
        label.getStyle().set("color", "#ffffff");
        label.getStyle().set("font-family", "'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");
        label.getStyle().set("font-weight", "300");
        label.getStyle().set("letter-spacing", "-0.02em");
        return label;
    }
    
    private Div createClockContainer() {
        Div container = new Div();
        container.getStyle().set("display", "flex");
        container.getStyle().set("align-items", "baseline");
        container.getStyle().set("justify-content", "center");
        container.getStyle().set("gap", "8px");
        container.getStyle().set("margin-bottom", "0.5rem");
        return container;
    }
    
    private H2 createTimeLabel() {
        H2 label = new H2();
        label.getStyle().set("font-size", "4rem");
        label.getStyle().set("margin", "0");
        label.getStyle().set("text-shadow", "0 2px 8px rgba(0,0,0,0.3)");
        label.getStyle().set("color", "#ffffff");
        label.getStyle().set("font-family", "'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");
        label.getStyle().set("font-weight", "300");
        label.getStyle().set("letter-spacing", "-0.02em");
        return label;
    }
    
    private Span createSecondsLabel() {
        Span label = new Span();
        label.getStyle().set("font-size", "1.5rem");
        label.getStyle().set("margin", "0");
        label.getStyle().set("text-shadow", "0 1px 4px rgba(0,0,0,0.3)");
        label.getStyle().set("color", "rgba(255, 255, 255, 0.7)");
        label.getStyle().set("font-family", "'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");
        label.getStyle().set("font-weight", "300");
        label.getStyle().set("letter-spacing", "-0.01em");
        label.getStyle().set("align-self", "flex-end");
        label.getStyle().set("margin-bottom", "0.5rem");
        return label;
    }
    
    private H3 createDateLabel() {
        H3 label = new H3();
        label.getStyle().set("font-size", "1.5rem");
        label.getStyle().set("margin-bottom", "2rem");
        label.getStyle().set("text-shadow", "0 1px 6px rgba(0,0,0,0.4)");
        label.getStyle().set("color", "#ffffff");
        label.getStyle().set("font-family", "'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");
        label.getStyle().set("font-weight", "300");
        label.getStyle().set("letter-spacing", "-0.02em");
        return label;
    }
    
    private Paragraph createQuoteLabel() {
        Paragraph label = new Paragraph();
        label.getStyle().set("font-size", "1.2rem");
        label.getStyle().set("margin-bottom", "2rem");
        label.getStyle().set("text-shadow", "1px 1px 2px rgba(0,0,0,0.4)");
        label.getStyle().set("font-style", "italic");
        label.getStyle().set("color", "#ffffff");
        label.getStyle().set("font-family", "'SF Pro Text', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");
        label.getStyle().set("font-weight", "300");
        label.getStyle().set("background", "rgba(0, 0, 0, 0.3)");
        label.getStyle().set("padding", "12px 20px");
        label.getStyle().set("border-radius", "12px");
        label.getStyle().set("backdrop-filter", "blur(10px)");
        label.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.1)");
        return label;
    }
    
    private Paragraph createWeatherLabel() {
        Paragraph label = new Paragraph();
        label.getStyle().set("font-size", "1.1rem");
        label.getStyle().set("margin-bottom", "1rem");
        label.getStyle().set("text-shadow", "1px 1px 2px rgba(0,0,0,0.4)");
        label.getStyle().set("color", "#ffffff");
        label.getStyle().set("font-family", "'SF Pro Text', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");
        label.getStyle().set("font-weight", "400");
        label.getStyle().set("background", "rgba(0, 0, 0, 0.3)");
        label.getStyle().set("padding", "12px 20px");
        label.getStyle().set("border-radius", "12px");
        label.getStyle().set("backdrop-filter", "blur(10px)");
        label.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.1)");
        return label;
    }
    
    private Paragraph createTrafficLabel() {
        Paragraph label = new Paragraph();
        label.getStyle().set("font-size", "1.1rem");
        label.getStyle().set("text-shadow", "1px 1px 2px rgba(0,0,0,0.4)");
        label.getStyle().set("color", "#ffffff");
        label.getStyle().set("font-family", "'SF Pro Text', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");
        label.getStyle().set("font-weight", "400");
        label.getStyle().set("background", "rgba(0, 0, 0, 0.3)");
        label.getStyle().set("padding", "12px 20px");
        label.getStyle().set("border-radius", "12px");
        label.getStyle().set("backdrop-filter", "blur(10px)");
        label.getStyle().set("border", "1px solid rgba(255, 255, 255, 0.1)");
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
        String timeStr = (String) content.get("time");
        if (timeStr != null) {
            // Split time to separate seconds
            String[] timeParts = timeStr.split(":");
            if (timeParts.length >= 3) {
                timeLabel.setText(timeParts[0] + ":" + timeParts[1]);
                secondsLabel.setText(":" + timeParts[2]);
            } else {
                timeLabel.setText(timeStr);
                secondsLabel.setText("");
            }
        }
        
        dateLabel.setText((String) content.get("date"));
        
        // Update quote
        String quote = (String) content.get("quote");
        if (quote != null && !quote.isEmpty()) {
            quoteLabel.setText(quote);
        } else {
            quoteLabel.setText("Quote service not available");
            quoteLabel.getStyle().set("opacity", "0.7");
        }
        
        // Update weather
        Map<String, Object> weather = (Map<String, Object>) content.get("weather");
        if (weather != null && !weather.isEmpty()) {
            String location = weather.get("location") != null ? weather.get("location").toString() : "Unknown";
            String temperature = weather.get("temperature") != null ? weather.get("temperature").toString() : "N/A";
            String condition = weather.get("condition") != null ? weather.get("condition").toString() : "N/A";
            String source = weather.get("source") != null ? weather.get("source").toString() : "unknown";
            
            if (source.equals("Fallback")) {
                weatherLabel.setText(String.format("Weather: %s, %s (%s) [Fallback]", temperature, condition, location));
                weatherLabel.getStyle().set("opacity", "0.8");
            } else {
                weatherLabel.setText(String.format("Weather: %s, %s (%s)", temperature, condition, location));
                weatherLabel.getStyle().set("opacity", "1.0");
            }
        } else {
            weatherLabel.setText("Weather service not available");
            weatherLabel.getStyle().set("opacity", "0.7");
        }
        
        // Update traffic
        Map<String, Object> traffic = (Map<String, Object>) content.get("traffic");
        if (traffic != null && !traffic.isEmpty()) {
            String status = traffic.get("status") != null ? traffic.get("status").toString() : "N/A";
            String travelTime = traffic.get("travelTime") != null ? traffic.get("travelTime").toString() : "N/A";
            String location = traffic.get("location") != null ? traffic.get("location").toString() : "Unknown";
            String source = traffic.get("source") != null ? traffic.get("source").toString() : "unknown";
            
            if (source.equals("Fallback")) {
                trafficLabel.setText(String.format("Traffic: %s (%s) - %s [Fallback]", status, travelTime, location));
                trafficLabel.getStyle().set("opacity", "0.8");
            } else {
                trafficLabel.setText(String.format("Traffic: %s (%s) - %s", status, travelTime, location));
                trafficLabel.getStyle().set("opacity", "1.0");
            }
        } else {
            trafficLabel.setText("Traffic service not available");
            trafficLabel.getStyle().set("opacity", "0.7");
        }
    }
    
    // Getters for components
    public H1 getGreetingLabel() { return greetingLabel; }
    public Div getClockContainer() { return clockContainer; }
    public H2 getTimeLabel() { return timeLabel; }
    public Span getSecondsLabel() { return secondsLabel; }
    public H3 getDateLabel() { return dateLabel; }
    public Paragraph getQuoteLabel() { return quoteLabel; }
    public Paragraph getWeatherLabel() { return weatherLabel; }
    public Paragraph getTrafficLabel() { return trafficLabel; }
} 