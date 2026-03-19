import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import weather.Period;
import weather.WeatherAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JavaFX extends Application {
	private Stage window;

	private Scene homeScene;
	private Scene forecastScene;
	private Scene detailsScene;

	private TextField cityInput;

	//Home Scene
	private Label homeCityLabel;
	private Label homeDayLabel;
	private Label homeTempLabel;
	private Label homeForecastLabel;
	private ImageView homeWeatherImage;
	private Label statusLabel;

	//Forecast Scene
	private Label forecastCityLabel;
	private HBox day1Card;
	private HBox day2Card;
	private HBox day3Card;
	private HBox day4Card;
	private HBox day5Card;

	//Details Scene
	private Label detailsCityLabel;
	private Label detailsTempLabel;
	private Label detailsWindLabel;
	private Label detailsPrecipLabel;
	private Label detailsLongForecastLabel;
	private ImageView detailsWeatherImage;


	private final Map<String, LocationInfo> cityMap = new HashMap<>();
	private ArrayList<Period> currentForecast;
	private String currentCity = "Chicago";

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("Weather App");

		initializeCityMap();

		buildHomeScene();
		buildForecastScene();
		buildDetailsScene();

		loadWeather("Chicago");

		window.setScene(homeScene);
		window.show();
		window.setMaximized(true);
	}

	private void initializeCityMap() {
		cityMap.put("chicago", new LocationInfo("Chicago", "LOT", 77, 70));
		cityMap.put("new york", new LocationInfo("New York", "OKX", 33, 35));
		cityMap.put("los angeles", new LocationInfo("Los Angeles", "LOX", 154, 44));
		cityMap.put("houston", new LocationInfo("Houston", "HGX", 52, 88));
		cityMap.put("atlanta", new LocationInfo("Atlanta", "FFC", 52, 88));
		cityMap.put("miami", new LocationInfo("Miami", "MFL", 110, 50));
		cityMap.put("seattle", new LocationInfo("Seattle", "SEW", 125, 67));
	}

	private void buildHomeScene() {
		Label titleLabel = new Label("Weather App");
		titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");

		cityInput = new TextField();
		cityInput.setPromptText("Search city");
		cityInput.setPrefWidth(220);
		cityInput.setOnAction(e -> searchCity());

		Button searchButton = new Button("Search");
		searchButton.setStyle(buttonStyle());
		searchButton.setOnAction(e -> searchCity());

		HBox searchBar = new HBox(10, cityInput, searchButton);
		searchBar.setAlignment(Pos.CENTER);

		homeCityLabel = new Label();
		homeDayLabel = new Label();
		homeDayLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #4a6572;");
		homeCityLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

		homeWeatherImage = new ImageView();
		homeWeatherImage.setFitWidth(140);
		homeWeatherImage.setFitHeight(140);
		homeWeatherImage.setPreserveRatio(true);

		homeTempLabel = new Label();
		homeTempLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		homeForecastLabel = new Label();
		homeForecastLabel.setStyle("-fx-font-size: 18px;");
		homeForecastLabel.setWrapText(true);
		homeForecastLabel.setMaxWidth(380);
		homeForecastLabel.setAlignment(Pos.CENTER);

		Button viewForecastButton = new Button("View Forecast");
		viewForecastButton.setStyle(buttonStyle());
		viewForecastButton.setOnAction(e -> switchScene(forecastScene));

		Button viewDetailsButton = new Button("View Details");
		viewDetailsButton.setStyle(buttonStyle());
		viewDetailsButton.setOnAction(e -> switchScene(detailsScene));

		HBox navButtons = new HBox(12, viewForecastButton, viewDetailsButton);
		navButtons.setAlignment(Pos.CENTER);

		statusLabel = new Label();
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

		VBox weatherCard = new VBox(15,
				homeCityLabel,
				homeDayLabel,
				homeWeatherImage,
				homeTempLabel,
				homeForecastLabel
		);
		weatherCard.setAlignment(Pos.CENTER);
		weatherCard.setPadding(new Insets(25));
		weatherCard.setMaxWidth(430);
		weatherCard.setStyle(
				"-fx-background-color: white;" +
						"-fx-background-radius: 20;" +
						"-fx-border-color: #d9e6f2;" +
						"-fx-border-radius: 20;"
		);

		VBox root = new VBox(20, titleLabel, searchBar, weatherCard, navButtons, statusLabel);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(30));
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #87ceeb, #dff4ff);");

		homeScene = new Scene(root, 700, 650);
	}


	private void buildForecastScene() {
		Label titleLabel = new Label("Forecast");
		titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");

		forecastCityLabel = new Label();
		forecastCityLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

		day1Card = createForecastCard();
		day2Card = createForecastCard();
		day3Card = createForecastCard();
		day4Card = createForecastCard();
		day5Card = createForecastCard();

		VBox forecastBox = new VBox(18, day1Card, day2Card, day3Card, day4Card, day5Card);
		forecastBox.setAlignment(Pos.CENTER);

		Button backButton = new Button("Back Home");
		backButton.setStyle(buttonStyle());
		backButton.setOnAction(e -> switchScene(homeScene));

		Button detailsButton = new Button("Go To Details");
		detailsButton.setStyle(buttonStyle());
		detailsButton.setOnAction(e -> switchScene(detailsScene));

		HBox navButtons = new HBox(12, backButton, detailsButton);
		navButtons.setAlignment(Pos.CENTER);

		VBox root = new VBox(20, titleLabel, forecastCityLabel, forecastBox, navButtons);
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(30));
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");

		forecastScene = new Scene(root, 700, 850);
	}

	private void buildDetailsScene() {
		Label titleLabel = new Label("Weather Details");
		titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");

		detailsCityLabel = new Label();
		detailsCityLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		detailsWeatherImage = new ImageView();
		detailsWeatherImage.setFitWidth(120);
		detailsWeatherImage.setFitHeight(120);
		detailsWeatherImage.setPreserveRatio(true);

		detailsTempLabel = new Label();
		detailsTempLabel.setStyle("-fx-font-size: 18px;");

		detailsWindLabel = new Label();
		detailsWindLabel.setStyle("-fx-font-size: 18px;");

		detailsPrecipLabel = new Label();
		detailsPrecipLabel.setStyle("-fx-font-size: 18px;");

		detailsLongForecastLabel = new Label();
		detailsLongForecastLabel.setWrapText(true);
		detailsLongForecastLabel.setMaxWidth(500);
		detailsLongForecastLabel.setStyle("-fx-font-size: 15px;");

		VBox detailsCard = new VBox(14,
				detailsCityLabel,
				detailsWeatherImage,
				detailsTempLabel,
				detailsWindLabel,
				detailsPrecipLabel,
				detailsLongForecastLabel
		);
		detailsCard.setAlignment(Pos.CENTER);
		detailsCard.setPadding(new Insets(25));
		detailsCard.setMaxWidth(560);
		detailsCard.setStyle(
				"-fx-background-color: white;" +
						"-fx-background-radius: 20;" +
						"-fx-border-color: #d9e6f2;" +
						"-fx-border-radius: 20;"
		);

		Button backHomeButton = new Button("Back Home");
		backHomeButton.setStyle(buttonStyle());
		backHomeButton.setOnAction(e -> switchScene(homeScene));

		Button forecastButton = new Button("Forecast Scene");
		forecastButton.setStyle(buttonStyle());
		forecastButton.setOnAction(e -> switchScene(forecastScene));

		HBox navButtons = new HBox(12, backHomeButton, forecastButton);
		navButtons.setAlignment(Pos.CENTER);

		VBox root = new VBox(20, titleLabel, detailsCard, navButtons);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(30));
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");

		detailsScene = new Scene(root, 700, 650);
	}

	private HBox createForecastCard() {
		HBox card = new HBox();
		card.setAlignment(Pos.CENTER_LEFT);
		card.setSpacing(25);
		card.setPadding(new Insets(20));
		card.setPrefWidth(520);
		card.setMinHeight(140);
		card.setMaxHeight(140);

		card.setStyle(
				"-fx-background-color: white;" +
						"-fx-background-radius: 16;" +
						"-fx-border-color: #d9e6f2;" +
						"-fx-border-radius: 16;"
		);

		return card;
	}

	private void searchCity() {
		String city = cityInput.getText().trim();
		if (!city.isEmpty()) {
			loadWeather(city);
		}
	}

	private void loadWeather(String cityName) {
		statusLabel.setText("");

		LocationInfo location = cityMap.get(cityName.toLowerCase());

		if (location == null) {
			statusLabel.setText("City not available. Try Chicago, New York, Los Angeles, Houston, Atlanta, Miami, or Seattle.");
			return;
		}

		ArrayList<Period> forecast = WeatherAPI.getForecast(location.region, location.gridX, location.gridY);

		if (forecast == null || forecast.isEmpty()) {
			statusLabel.setText("Forecast did not load.");
			return;
		}

		currentForecast = forecast;
		currentCity = location.displayName;

		updateScenes();
	}

	private void updateScenes() {
		if (currentForecast == null || currentForecast.isEmpty()) {
			return;
		}

		Period current = currentForecast.get(0);

		// Home scene
		homeCityLabel.setText(currentCity);
		String day = current.startTime.toInstant().atZone(java.time.ZoneId.systemDefault()).getDayOfWeek().toString();
		homeDayLabel.setText(day.substring(0, 1) + day.substring(1).toLowerCase());
		homeTempLabel.setText(current.temperature + "°" + current.temperatureUnit);
		homeForecastLabel.setText(current.shortForecast);
		homeWeatherImage.setImage(getWeatherImage(current.shortForecast));

		// Forecast scene
		forecastCityLabel.setText(currentCity + " 5-Day Forecast");
		updateFiveDayForecastCards();

		// Details scene
		detailsCityLabel.setText(currentCity);
		detailsWeatherImage.setImage(getWeatherImage(current.shortForecast));
		detailsTempLabel.setText("Temperature: " + current.temperature + "°" + current.temperatureUnit);
		detailsWindLabel.setText("Wind: " + current.windSpeed + " " + current.windDirection);

		if (current.probabilityOfPrecipitation != null) {
			detailsPrecipLabel.setText("Chance of precipitation: " + current.probabilityOfPrecipitation.value + "%");
		} else {
			detailsPrecipLabel.setText("Chance of precipitation: N/A");
		}

		detailsLongForecastLabel.setText("Details: " + current.detailedForecast);
	}

	private void updateFiveDayForecastCards() {
		HBox[] cards = {day1Card, day2Card, day3Card, day4Card, day5Card};

		ArrayList<DayForecast> dailyForecasts = buildDailyForecasts();

		for (int x = 0; x < cards.length; x++) {
			cards[x].getChildren().clear();

			if (x < dailyForecasts.size()) {
				DayForecast df = dailyForecasts.get(x);

				Label dayNameLabel = new Label(df.dayName);
				dayNameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
				dayNameLabel.setMinWidth(170);
				dayNameLabel.setPrefWidth(170);
				dayNameLabel.setMaxWidth(170);
				dayNameLabel.setWrapText(true);

				VBox detailsBox = new VBox(8);
				detailsBox.setAlignment(Pos.CENTER_LEFT);
				detailsBox.setFillWidth(true);
				detailsBox.setPrefWidth(360);

				String dayForecast = "N/A";
				String dayTemp = "N/A";
				String dayWind = "N/A";

				String nightForecast = "N/A";
				String nightTemp = "N/A";
				String nightWind = "N/A";

				if (df.dayPeriod != null) {
					dayForecast = df.dayPeriod.shortForecast;
					dayTemp = df.dayPeriod.temperature + "°" + df.dayPeriod.temperatureUnit;
					dayWind = df.dayPeriod.windSpeed + " " + df.dayPeriod.windDirection;
				}

				if (df.nightPeriod != null) {
					nightForecast = df.nightPeriod.shortForecast;
					nightTemp = df.nightPeriod.temperature + "°" + df.nightPeriod.temperatureUnit;
					nightWind = df.nightPeriod.windSpeed + " " + df.nightPeriod.windDirection;
				}

				Label dayDetails = new Label("Forecast: " + dayForecast + "\n" + "Temp: " + dayTemp + "\n" + "Wind: " + dayWind);
				dayDetails.setStyle("-fx-font-size: 15px;");
				dayDetails.setMaxWidth(360);
				dayDetails.setWrapText(true);

				Label nightDetails = new Label("Night Forecast: " + nightForecast + "\n" + "Temp: " + nightTemp + "\n" + "Wind: " + nightWind);
				nightDetails.setStyle("-fx-font-size: 15px;");
				nightDetails.setMaxWidth(360);
				nightDetails.setWrapText(true);

				detailsBox.getChildren().addAll(dayDetails, nightDetails);

				cards[x].getChildren().addAll(dayNameLabel, detailsBox);
			} else {
				Label noDataLabel = new Label("No data available");
				noDataLabel.setStyle("-fx-font-size: 16px;");
				cards[x].getChildren().add(noDataLabel);
			}
		}
	}

	private ArrayList<DayForecast> buildDailyForecasts() {
		ArrayList<DayForecast> dailyForecasts = new ArrayList<>();

		for (int x = 0; x < currentForecast.size() && dailyForecasts.size() < 5; x++) {
			Period current = currentForecast.get(x);

			if (current.isDaytime) {
				DayForecast df = new DayForecast();
				df.dayName = current.name;
				df.dayPeriod = current;

				if (x + 1 < currentForecast.size() && !currentForecast.get(x + 1).isDaytime) {
					df.nightPeriod = currentForecast.get(x + 1);
				}

				dailyForecasts.add(df);
			}
		}

		return dailyForecasts;
	}

	private Image getWeatherImage(String shortForecast) {
		String forecastText = shortForecast.toLowerCase();
		String imagePath;

		if (forecastText.contains("partly")) {
			imagePath = "/images/partly cloudy.png";
		} else if (forecastText.contains("sun") || forecastText.contains("clear")) {
			imagePath = "/images/sunny.png";
		} else if (forecastText.contains("storm") || forecastText.contains("thunder")) {
			imagePath = "/images/storm.png";
		} else if (forecastText.contains("snow")) {
			imagePath = "/images/snow.png";
		} else if (forecastText.contains("rain") || forecastText.contains("shower")) {
			imagePath = "/images/rain.png";
		} else {
			imagePath = "/images/cloudy.png";
		}

		try {
			return new Image(getClass().getResourceAsStream(imagePath));
		} catch (Exception e) {
			System.out.println("Could not load image: " + imagePath);
			return null;
		}
	}

	private void switchScene(Scene scene) {
		boolean wasMaximized = window.isMaximized();
		boolean wasFullScreen = window.isFullScreen();

		window.setScene(scene);
		window.setMaximized(wasMaximized);
		window.setFullScreen(wasFullScreen);
	}
	private String buttonStyle() {
		return "-fx-background-color: #2d89ef;" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 14px;" +
				"-fx-font-weight: bold;" +
				"-fx-background-radius: 12;" +
				"-fx-padding: 10 18 10 18;";
	}

	private static class DayForecast {
		String dayName;
		Period dayPeriod;
		Period nightPeriod;
	}

	public static void main(String[] args) {
		launch(args);
	}

	private static class LocationInfo {
		String displayName;
		String region;
		int gridX;
		int gridY;

		LocationInfo(String displayName, String region, int gridX, int gridY) {
			this.displayName = displayName;
			this.region = region;
			this.gridX = gridX;
			this.gridY = gridY;
		}
	}
}
