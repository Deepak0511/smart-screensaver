package in.dpk.assistants.smart_screensaver.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.Component;
import java.util.List;

/**
 * Component responsible for managing the background container with overlay.
 * Single Responsibility: Handle background styling and overlay.
 */
public class BackgroundContainer extends Div {
    
    private final Div overlay;
    private final Div contentContainer;
    
    public BackgroundContainer() {
        initContainer();
        this.overlay = createOverlay();
        this.contentContainer = createContentContainer();
        
        add(overlay, contentContainer);
    }
    
    private void initContainer() {
        setSizeFull();
        getStyle().set("background-size", "cover");
        getStyle().set("background-position", "center");
        getStyle().set("background-repeat", "no-repeat");
        getStyle().set("position", "relative");
    }
    
    private Div createOverlay() {
        Div overlay = new Div();
        overlay.setSizeFull();
        overlay.getStyle().set("background", "rgba(0, 0, 0, 0.3)");
        overlay.getStyle().set("position", "absolute");
        overlay.getStyle().set("top", "0");
        overlay.getStyle().set("left", "0");
        overlay.getStyle().set("z-index", "1");
        return overlay;
    }
    
    private Div createContentContainer() {
        Div contentContainer = new Div();
        contentContainer.getStyle().set("position", "absolute");
        contentContainer.getStyle().set("top", "50%");
        contentContainer.getStyle().set("left", "50%");
        contentContainer.getStyle().set("transform", "translate(-50%, -50%)");
        contentContainer.getStyle().set("text-align", "center");
        contentContainer.getStyle().set("color", "white");
        contentContainer.getStyle().set("z-index", "2");
        contentContainer.getStyle().set("width", "80%");
        contentContainer.getStyle().set("max-width", "600px");
        return contentContainer;
    }
    
    public void setBackgroundImage(String imageUrl) {
        getStyle().set("background-image", "url('" + imageUrl + "')");
    }
    
    public void addContent(Component... components) {
        contentContainer.add(components);
    }
    
    public void addContent(List<Component> components) {
        contentContainer.add(components.toArray(new Component[0]));
    }
    
    public void clearContent() {
        contentContainer.removeAll();
    }
} 