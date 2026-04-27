# CS-342-Project-2-WeatherApp
This project is a desktop weather application built using JavaFX. It allows users to search for a city and view current conditions along with a five-day forecast. The application uses data from the National Weather Service (NWS) API and presents it through a multi-scene JavaFX interface.

## Features
- Search for weather by city name
- Preloaded support for select cities (e.g., Chicago, New York, Los Angeles)
- Five-day forecast with both daytime and nighttime periods
- Detailed weather view including temperature, wind, and precipitation probability
- Dark mode toggle that persists across scenes
- Dynamic weather icons based on forecast conditions
- Error handling for failed API requests or invalid input

## Technologies Used
- Java
- JavaFX
- FXML
- National Weather Service (NWS) API

## How It Works
The application maps a user-entered city to a predefined set of grid coordinates required by the NWS API. Using this information, it sends a request to retrieve forecast data. The response is parsed into structured objects, which are then displayed across different JavaFX scenes.

The interface is organized into three main views:
- Home: allows the user to enter or select a city
- Forecast: displays a five-day overview of weather conditions
- Details: shows more specific information for a selected forecast period

## Project Structure
src/
├── weather/ # Handles API requests and data parsing
├── ui/ # JavaFX scene implementations
├── adapters/ # Wrapper classes for formatting weather data
├── resources/
│ ├── images/ # Weather icons
│ └── styles/ # CSS files for UI styling

## How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/weather-app.git
2. Open the project in IntelliJ IDEA
3. Configure the JavaFX SDK in project settings
4. Run the main application class

## Configuration Notes
- JavaFX must be properly installed and configured in IntelliJ
- An internet connection is required to fetch weather data
- If the API request fails, the application will display an error message

## Future Improvements
- Add automatic location detection using a geolocation API
- Improve city search by integrating a live geocoding service
- Add hourly forecast support
- Enhance UI responsiveness for different screen sizes

## Creators
Olukolajo Sodipe
Joseph Labib
