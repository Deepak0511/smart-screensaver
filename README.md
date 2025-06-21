# Smart Screensaver

A browser-based smart screensaver application that displays relevant and useful information based on the time of day, weekday/weekend, and customizable routines.

## Features

- **Time-based Content**: Displays different information based on time of day (morning, afternoon, evening)
- **Context-aware**: Adapts to workdays, weekends, holidays, and work-from-home days
- **Customizable Routines**: Create Alexa-like routines or iPhone shortcuts-style automation
- **Information Display**:
  - Good morning/afternoon/evening messages
  - Quote of the day
  - Current time and date
  - Weather information
  - Traffic information
  - GPS location and bus tracking
  - Custom messages

## Technology Stack

- **Backend**: Java 21, Spring Boot 3.5.3
- **Frontend**: Vaadin Flow 24.5.0
- **Database**: H2 (in-memory for development)
- **Build Tool**: Gradle

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle (or use the included Gradle wrapper)

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd smart-screensaver
   ```

2. **Run the application**:
   ```bash
   # Using Gradle wrapper
   ./gradlew bootRun
   
   # Or using Gradle directly
   gradle bootRun
   ```

3. **Access the application**:
   - Main application: http://localhost:8080
   - H2 Database Console: http://localhost:8080/h2-console
   - API Endpoints: http://localhost:8080/api/screensaver

### API Endpoints

- `GET /api/screensaver/content` - Get current screensaver content
- `GET /api/screensaver/health` - Health check endpoint

## Application Structure

```
src/main/java/in/dpk/assistants/smart_screensaver/
├── SmartScreensaverApplication.java    # Main application class
├── config/
│   └── DataInitializer.java           # Sample data initialization
├── controller/
│   └── ScreensaverController.java     # REST API endpoints
├── entity/
│   ├── Routine.java                   # Routine entity
│   └── UserPreference.java            # User preferences entity
├── repository/
│   ├── RoutineRepository.java         # Data access for routines
│   └── UserPreferenceRepository.java  # Data access for preferences
├── service/
│   ├── ExternalDataService.java       # External API integrations
│   └── ScreensaverService.java        # Business logic
└── ui/
    └── ScreensaverView.java           # Vaadin UI component
```

## Sample Routines

The application comes with pre-configured sample routines:

### Morning Routine (6 AM - 9 AM, Weekdays)
- Shows greeting, time, date, quote, weather, and traffic

### Afternoon Routine (12 PM - 2 PM, Weekdays)
- Shows greeting, time, and quote

### Evening Routine (5 PM - 8 PM, Weekdays)
- Shows greeting, time, traffic, and weather

### Weekend Routine (8 AM - 10 PM, Weekends)
- Shows greeting, time, date, quote, and weather

## Customization

### Creating Custom Routines

Routines can be customized with:
- **Time windows**: Set start and end times
- **Day conditions**: Choose specific days of the week
- **Context**: Workday, weekend, holiday, work-from-home
- **Actions**: Choose what information to display
- **Priority**: Set routine priority for conflicts

### Available Actions

- `SHOW_GREETING` - Display time-appropriate greeting
- `SHOW_QUOTE` - Show quote of the day
- `SHOW_TRAFFIC` - Display traffic information
- `SHOW_WEATHER` - Show weather conditions
- `SHOW_LOCATION` - Display GPS location
- `SHOW_TIME` - Show current time
- `SHOW_DATE` - Show current date
- `SHOW_CUSTOM_MESSAGE` - Display custom message

## External Integrations

The application is designed to integrate with external services:

- **Weather APIs**: OpenWeatherMap, WeatherAPI
- **Traffic APIs**: Google Maps, Waze
- **Location Services**: GPS, IP geolocation
- **Public Transport**: Bus tracking APIs
- **Quote APIs**: Quote of the day services

Currently, the application uses mock data for these services. To integrate with real APIs:

1. Update `ExternalDataService.java`
2. Add API keys to `application.properties`
3. Implement proper error handling

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# Server
server.port=8080

# Vaadin
vaadin.productionMode=false

# Security (disabled for development)
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

## Development

### Building the Application

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Database Management

- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## Future Enhancements

- [ ] Real-time API integrations
- [ ] Advanced routine editor UI
- [ ] Multiple user support
- [ ] Mobile-responsive design
- [ ] Push notifications
- [ ] Calendar integration
- [ ] Smart home device integration
- [ ] Voice commands
- [ ] Machine learning for content personalization

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0.

## Support

For questions or issues, please create an issue in the repository. 