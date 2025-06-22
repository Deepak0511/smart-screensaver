/**
 * Real-time updater for Smart Screensaver
 * Handles client-side time updates and server-side data fetching
 */
class RealtimeUpdater {
    constructor() {
        this.timeLabel = null;
        this.secondsLabel = null;
        this.dateLabel = null;
        this.greetingLabel = null;
        this.weatherLabel = null;
        this.trafficLabel = null;
        this.quoteLabel = null;
        this.updateInterval = null;
        this.dataFetchInterval = null;
        this.lastDataFetch = 0;
        this.dataCache = null;
        this.userName = 'User';
        this.connectionStatus = null;
        this.loadingIndicator = null;
        
        this.init();
    }
    
    init() {
        // Wait for DOM to be ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.setupElements());
        } else {
            this.setupElements();
        }
    }
    
    setupElements() {
        // Create status indicators
        this.createStatusIndicators();
        
        // Find UI elements by their text content or class
        this.findElements();
        
        if (this.timeLabel && this.dateLabel) {
            // Start real-time updates
            this.startTimeUpdates();
            this.startDataFetching();
        } else {
            // Retry after a short delay if elements aren't found yet
            setTimeout(() => this.setupElements(), 100);
        }
    }
    
    createStatusIndicators() {
        // Create connection status indicator
        this.connectionStatus = document.createElement('div');
        this.connectionStatus.className = 'connection-status';
        this.connectionStatus.title = 'Connection Status';
        document.body.appendChild(this.connectionStatus);
        
        // Create loading indicator
        this.loadingIndicator = document.createElement('div');
        this.loadingIndicator.className = 'loading-indicator';
        this.loadingIndicator.textContent = 'Updating...';
        document.body.appendChild(this.loadingIndicator);
    }
    
    findElements() {
        // Find elements by looking for specific text patterns or classes
        const allElements = document.querySelectorAll('h1, h2, h3, p, span');
        
        allElements.forEach(element => {
            const text = element.textContent || '';
            
            // Find time label (usually contains time format like 14:30)
            if (/\d{1,2}:\d{2}/.test(text) && element.tagName === 'H2') {
                this.timeLabel = element;
                element.classList.add('realtime-update');
            }
            
            // Find seconds label (contains just seconds like :45)
            if (/^:\d{2}$/.test(text) && element.tagName === 'SPAN') {
                this.secondsLabel = element;
                element.classList.add('realtime-update');
            }
            
            // Find date label (contains date information)
            if (/\d{1,2}\/\d{1,2}\/\d{4}|\w+ \d{1,2}, \d{4}/.test(text) && element.tagName === 'H3') {
                this.dateLabel = element;
                element.classList.add('realtime-update');
            }
            
            // Find greeting label (starts with greeting words)
            if (/^(Good|Hello|Hi|Welcome)/i.test(text) && element.tagName === 'H1') {
                this.greetingLabel = element;
                element.classList.add('realtime-update');
            }
            
            // Find weather label
            if (text.includes('Weather:') && element.tagName === 'P') {
                this.weatherLabel = element;
                element.classList.add('realtime-update');
            }
            
            // Find traffic label
            if (text.includes('Traffic:') && element.tagName === 'P') {
                this.trafficLabel = element;
                element.classList.add('realtime-update');
            }
            
            // Find quote label (usually italic and longer text)
            if (element.style.fontStyle === 'italic' && text.length > 20 && element.tagName === 'P') {
                this.quoteLabel = element;
                element.classList.add('realtime-update');
            }
        });
    }
    
    startTimeUpdates() {
        // Update time immediately
        this.updateTime();
        
        // Update every second
        this.updateInterval = setInterval(() => {
            this.updateTime();
        }, 1000);
    }
    
    startDataFetching() {
        // Fetch data immediately
        this.fetchServerData();
        
        // Fetch server data every 30 seconds (weather, traffic, etc.)
        this.dataFetchInterval = setInterval(() => {
            this.fetchServerData();
        }, 30000);
    }
    
    updateTime() {
        const now = new Date();
        
        // Update time (24-hour format)
        const timeString = now.toLocaleTimeString('en-US', {
            hour12: false,
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
        
        // Split time into hours:minutes and seconds
        const timeParts = timeString.split(':');
        const hoursMinutes = timeParts[0] + ':' + timeParts[1];
        const seconds = ':' + timeParts[2];
        
        // Update date
        const dateString = now.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        
        // Update greeting based on time
        const hour = now.getHours();
        let greeting = 'Hello';
        
        if (hour < 12) {
            greeting = 'Good Morning';
        } else if (hour < 17) {
            greeting = 'Good Afternoon';
        } else if (hour < 21) {
            greeting = 'Good Evening';
        } else {
            greeting = 'Good Night';
        }
        
        // Update DOM elements with visual feedback
        if (this.timeLabel) {
            this.timeLabel.classList.add('updating');
            this.timeLabel.textContent = hoursMinutes;
            setTimeout(() => {
                this.timeLabel.classList.remove('updating');
                this.timeLabel.classList.add('updated');
                setTimeout(() => this.timeLabel.classList.remove('updated'), 500);
            }, 100);
        }
        
        if (this.secondsLabel) {
            this.secondsLabel.textContent = seconds;
        }
        
        if (this.dateLabel) {
            this.dateLabel.textContent = dateString;
        }
        
        if (this.greetingLabel) {
            this.greetingLabel.textContent = `${greeting}, ${this.userName}!`;
        }
    }
    
    async fetchServerData() {
        try {
            this.showLoading(true);
            this.updateConnectionStatus(true);
            
            const response = await fetch('/api/realtime-data');
            if (response.ok) {
                const data = await response.json();
                this.updateServerData(data);
                this.lastDataFetch = Date.now();
                this.dataCache = data;
                this.updateConnectionStatus(true);
            } else {
                throw new Error(`HTTP ${response.status}`);
            }
        } catch (error) {
            console.warn('Failed to fetch server data:', error);
            this.updateConnectionStatus(false);
            // Use cached data if available
            if (this.dataCache) {
                this.updateServerData(this.dataCache);
            }
        } finally {
            this.showLoading(false);
        }
    }
    
    updateServerData(data) {
        // Update user name
        if (data.userName) {
            this.userName = data.userName;
        }
        
        // Update weather with visual feedback
        if (data.weather && this.weatherLabel) {
            const weather = data.weather;
            const location = weather.location || 'Unknown';
            const temperature = weather.temperature || 'N/A';
            const condition = weather.condition || 'N/A';
            
            this.weatherLabel.classList.add('updating');
            this.weatherLabel.textContent = `Weather: ${temperature}, ${condition} (${location})`;
            setTimeout(() => {
                this.weatherLabel.classList.remove('updating');
                this.weatherLabel.classList.add('updated');
                setTimeout(() => this.weatherLabel.classList.remove('updated'), 500);
            }, 100);
        }
        
        // Update traffic with visual feedback
        if (data.traffic && this.trafficLabel) {
            const traffic = data.traffic;
            const status = traffic.status || 'N/A';
            const travelTime = traffic.travelTime || 'N/A';
            const location = traffic.location || 'Unknown';
            
            this.trafficLabel.classList.add('updating');
            this.trafficLabel.textContent = `Traffic: ${status} (${travelTime}) - ${location}`;
            setTimeout(() => {
                this.trafficLabel.classList.remove('updating');
                this.trafficLabel.classList.add('updated');
                setTimeout(() => this.trafficLabel.classList.remove('updated'), 500);
            }, 100);
        }
        
        // Update quote with visual feedback
        if (data.quote && this.quoteLabel) {
            this.quoteLabel.classList.add('updating');
            this.quoteLabel.textContent = data.quote;
            setTimeout(() => {
                this.quoteLabel.classList.remove('updating');
                this.quoteLabel.classList.add('updated');
                setTimeout(() => this.quoteLabel.classList.remove('updated'), 500);
            }, 100);
        }
    }
    
    showLoading(show) {
        if (this.loadingIndicator) {
            if (show) {
                this.loadingIndicator.classList.add('show');
            } else {
                this.loadingIndicator.classList.remove('show');
            }
        }
    }
    
    updateConnectionStatus(connected) {
        if (this.connectionStatus) {
            if (connected) {
                this.connectionStatus.classList.remove('disconnected');
            } else {
                this.connectionStatus.classList.add('disconnected');
            }
        }
    }
    
    destroy() {
        if (this.updateInterval) {
            clearInterval(this.updateInterval);
        }
        if (this.dataFetchInterval) {
            clearInterval(this.dataFetchInterval);
        }
        
        // Remove status indicators
        if (this.connectionStatus) {
            this.connectionStatus.remove();
        }
        if (this.loadingIndicator) {
            this.loadingIndicator.remove();
        }
    }
}

// Initialize the real-time updater when the script loads
const realtimeUpdater = new RealtimeUpdater();

// Export for potential use in other scripts
window.RealtimeUpdater = RealtimeUpdater;
window.realtimeUpdater = realtimeUpdater; 