# Smart Screensaver Application

A browser-based smart screensaver application built with Java Spring Boot and Vaadin Flow that displays relevant information based on time, day type, and customizable routines with real-time external data integration.

## Features

### 🌟 Core Features
- **Dynamic Personalized Greetings**: Time-based greetings with user's name
- **Beautiful Time-Based Backgrounds**: Different backgrounds for morning, afternoon, evening, and night
- **Real-Time External Data Integration**: Weather, location, quotes, and traffic information
- **Backend-Based Location Detection**: Robust location detection with IP-based fallback and reverse geocoding
- **Customizable User Preferences**: Personalized settings and routines
- **Responsive Design**: Modern, clean UI that adapts to different screen sizes

### 🌤️ External Data Services
- **Weather Data**: Real-time weather from Open-Meteo API
- **Location Services**: IP-based location detection with reverse geocoding via Open-Meteo
- **Quote of the Day**: Multiple quote APIs with fallback support
- **Traffic Information**: Time-based traffic estimates
- **Bus Location**: Mock transit data (extensible for real APIs)

### 🔧 Technical Features
- **SSL Certificate Handling**: Robust error handling for expired certificates
- **Multiple API Fallbacks**: Automatic fallback to alternative APIs if one fails
- **Backend Location Management**: Server-side location handling with automatic initialization
- **Reverse Geocoding**: Converts coordinates to city/region/country names using Open-Meteo
- **Modular Architecture**: Clean separation of concerns with dedicated services

## Recent Fixes (Latest Update)

### ✅ Location Detection Completely Rewritten
- **Problem**: Location detection was broken and relied on complex JavaScript
- **Solution**: Moved location logic to Java backend with proper initialization
- **Result**: Location detection now works reliably with IP-based fallback

### ✅ Backend Location Service
- **Problem**: Location detection was unreliable and complex
- **Solution**: Created robust LocationService with automatic IP-based initialization
- **Result**: Application always has location data available on startup

### ✅ Simplified Location Management
- **Problem**: Complex JavaScript location handling with caching issues
- **Solution**: Simplified LocationManager component with backend integration
- **Result**: Clean, reliable location detection with proper error handling

### ✅ SSL Certificate Issues Resolved
- **Problem**: Quotable API had expired SSL certificate causing connection failures
- **Solution**: Implemented multiple quote API fallbacks (quotable.io, zenquotes.io, quotes.rest)
- **Result**: Quote service now works reliably with automatic failover

### ✅ Error Handling Enhanced
- **Problem**: SSL errors caused application crashes
- **Solution**: Added comprehensive error handling and logging
- **Result**: Application gracefully handles API failures and continues working

## Architecture

### Services
- **ScreensaverService**: Main orchestrator for screensaver content
- **ExternalDataService**: Handles all external API calls with fallback support
- **LocationService**: Manages location detection with IP-based fallback and reverse geocoding
- **BackgroundService**: Provides time-based background images
- **GreetingService**: Generates personalized time-based greetings
- **TimeService**: Handles time and date formatting
- **UserService**: Manages user preferences and data persistence

### Data Persistence
- **JSON File Storage**: User preferences and routines stored in JSON files
- **Backend Location Storage**: Location data managed server-side
- **In-Memory Storage**: Temporary data during application runtime

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Gradle 7.0 or higher

### Quick Start
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd smart-screensaver
   ```

2. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

3. **Access the application**
   - Open browser and navigate to `http://localhost:8080`
   - The screensaver will display with default settings
   - Test location service at `http://localhost:8080/test-location.html`

### Configuration
The application uses sensible defaults but can be customized via `application.properties`:

```properties
# External API URLs (optional - defaults provided)
app.external.weather.api-url=https://api.open-meteo.com/v1/forecast
app.external.location.api-url=https://ipapi.co/json/
app.external.quote.api-url=https://api.quotable.io/random
```

## Usage

### Initial Setup
1. **Access Settings**: Click the settings button (⚙️) in the top-right corner
2. **Set Display Name**: Enter your preferred display name for personalized greetings
3. **Configure Routines**: Add custom routines for different times of the day
4. **Enable Location**: Click the location button (📍) to enable browser location for accurate weather data

### Features Overview
- **Personalized Greetings**: Shows "Good Morning/Afternoon/Evening, [Your Name]!"
- **Dynamic Backgrounds**: Changes based on time of day
- **Weather Information**: Real-time weather for your location
- **Quote of the Day**: Inspirational quotes that change daily
- **Traffic Updates**: Time-based traffic information
- **Location Detection**: Automatic location detection with manual override

### Location Services
- **Automatic Initialization**: Application starts with IP-based location
- **Browser Location**: Click location button to get precise coordinates
- **Reverse Geocoding**: Coordinates automatically converted to city names
- **Fallback System**: Always has location data available

## API Endpoints

### Screensaver Content
- `GET /api/screensaver/content` - Complete screensaver data
- `GET /api/screensaver/weather` - Weather information
- `GET /api/screensaver/location` - Location data
- `GET /api/screensaver/quote` - Quote of the day
- `GET /api/screensaver/traffic` - Traffic information
- `GET /api/screensaver/bus` - Bus location data

### Location Management
- `GET /api/screensaver/browser-location` - Get current location status
- `POST /api/screensaver/browser-location` - Set browser location
- `GET /api/screensaver/test-location` - Test location service functionality
- `DELETE /api/screensaver/browser-location` - Clear location data

### User Management
- `GET /api/settings/user` - Get user preferences
- `POST /api/settings/user` - Update user preferences
- `GET /api/settings/routines` - Get user routines
- `POST /api/settings/routines` - Update user routines

### Health Check
- `GET /api/screensaver/health` - Application health status

## Technical Details

### External APIs Used
1. **Open-Meteo** (Weather & Geocoding): Free, reliable weather and location services
2. **IP-API** (Location): IP-based location detection
3. **Multiple Quote APIs**: Automatic fallback between different quote services
4. **Custom Backgrounds**: Local time-based background images

### Error Handling
- **SSL Certificate Issues**: Automatic fallback to alternative APIs
- **Network Failures**: Graceful degradation with mock data
- **Location Permission Denied**: Falls back to IP-based location
- **API Rate Limits**: Implemented caching to reduce API calls

### Performance Optimizations
- **Backend Location Management**: Server-side location handling
- **API Response Caching**: Reduces external API calls
- **Lazy Loading**: Content loaded on demand
- **Efficient UI Updates**: Minimal DOM manipulation

## Testing

### Location Service Testing
Access the test page at `http://localhost:8080/test-location.html` to:
- Test location service functionality
- Verify IP-based location detection
- Test browser location setting
- Check location status and data

## Troubleshooting

### Common Issues

1. **Location Not Working**
   - Application automatically initializes with IP-based location
   - Click location button for precise browser location
   - Check test page for detailed location status

2. **Weather Data Unavailable**
   - Check internet connection
   - Weather service may be temporarily unavailable
   - Application will show mock weather data

3. **Quote Not Loading**
   - Multiple quote APIs are tried automatically
   - If all fail, a default quote is shown
   - Check application logs for specific errors

4. **SSL Certificate Errors**
   - Fixed in latest version with multiple API fallbacks
   - Application automatically tries alternative APIs
   - No user action required

### Logs
Application logs provide detailed information about:
- API calls and responses
- Location detection status
- Error handling and fallbacks
- User interactions

## Development

### Project Structure
```
src/main/java/in/dpk/assistants/smart_screensaver/
├── config/          # Configuration classes
├── controller/      # REST API controllers
├── entity/          # Data entities
├── repository/      # Data access layer
├── service/         # Business logic services
└── ui/              # Vaadin UI components
    └── components/  # Modular UI components
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review application logs
3. Test API endpoints directly
4. Create an issue with detailed error information

---

**Smart Screensaver** - Making your idle time more informative and beautiful! 🌟 