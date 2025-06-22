package in.dpk.assistants.smart_screensaver.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.ClientCallable;

/**
 * Component responsible for managing location detection and caching.
 * Single Responsibility: Handle location-related JavaScript and logic.
 */
public class LocationManager {
    
    private final Component parentComponent;
    
    public LocationManager(Component parentComponent) {
        this.parentComponent = parentComponent;
        initLocationDetection();
    }
    
    public void checkLocationStatus() {
        parentComponent.getElement().executeJs("""
            // Simple check: if we have location data, hide the button
            fetch('/api/screensaver/browser-location')
                .then(response => response.json())
                .then(data => {
                    console.log('Location status check:', data);
                    const locationButton = document.querySelector('vaadin-button[title*="Enable Location"]');
                    if (locationButton) {
                        if (data.status === 'valid' && data.city && data.city !== 'Unknown') {
                            locationButton.style.display = 'none';
                            console.log('Location button hidden - valid location found');
                        } else {
                            locationButton.style.display = 'block';
                            console.log('Location button shown - no valid location');
                        }
                    }
                })
                .catch(error => {
                    console.log('Error checking location status:', error);
                    // Show button if check fails
                    const locationButton = document.querySelector('vaadin-button[title*="Enable Location"]');
                    if (locationButton) {
                        locationButton.style.display = 'block';
                    }
                });
        """);
    }
    
    private void initLocationDetection() {
        parentComponent.getElement().executeJs("""
            window.requestLocationPermission = function() {
                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(
                        function(position) {
                            console.log('Location permission granted, coordinates received');
                            
                            // Send coordinates to server for processing
                            fetch('/api/screensaver/browser-location', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json',
                                },
                                body: JSON.stringify({
                                    latitude: position.coords.latitude,
                                    longitude: position.coords.longitude,
                                    city: 'Unknown',  // Server will resolve this
                                    region: 'Unknown',
                                    country: 'Unknown'
                                })
                            }).then(response => response.json())
                            .then(data => {
                                console.log('Location sent to server:', data);
                                // Hide the location button
                                hideLocationButton();
                                // Reload the page to update content
                                setTimeout(() => {
                                    window.location.reload();
                                }, 1000);
                            }).catch(error => {
                                console.error('Error sending location to server:', error);
                                Notification.show('Failed to save location', 3000, 'middle');
                            });
                        },
                        function(error) {
                            console.error('Location permission denied or error:', error);
                            Notification.show('Location permission denied', 3000, 'middle');
                        },
                        {
                            enableHighAccuracy: true,
                            timeout: 10000,
                            maximumAge: 300000
                        }
                    );
                } else {
                    console.log('Geolocation is not supported by this browser.');
                    Notification.show('Geolocation not supported', 3000, 'middle');
                }
            };
            
            // Function to hide location button
            window.hideLocationButton = function() {
                const locationButton = document.querySelector('vaadin-button[title*="Enable Location"]');
                if (locationButton) {
                    locationButton.style.display = 'none';
                    console.log('Location button hidden');
                }
            };
            
            // Function to show location button
            window.showLocationButton = function() {
                const locationButton = document.querySelector('vaadin-button[title*="Enable Location"]');
                if (locationButton) {
                    locationButton.style.display = 'block';
                    console.log('Location button shown');
                }
            };
        """);
    }
    
    public void requestLocationPermission() {
        parentComponent.getElement().executeJs("window.requestLocationPermission();");
        Notification.show("Requesting location permission...", 3000, Notification.Position.TOP_CENTER);
    }
    
    @ClientCallable
    private void onLocationReceived(double latitude, double longitude) {
        Notification.show("Location received: " + latitude + ", " + longitude, 3000, Notification.Position.TOP_CENTER);
    }
    
    @ClientCallable
    private void checkLocationButtonVisibility() {
        checkLocationStatus();
    }
    
    public void refreshLocationStatus() {
        checkLocationStatus();
    }
} 