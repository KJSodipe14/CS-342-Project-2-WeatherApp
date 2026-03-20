import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
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
		window.setMinWidth(900);
		window.setMinHeight(650);
		window.show();
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

		homeScene = new Scene(root, 1000, 700);
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

		VBox forecastCards = new VBox(18, day1Card, day2Card, day3Card, day4Card, day5Card);
		forecastCards.setFillWidth(true);
		forecastCards.setAlignment(Pos.TOP_CENTER);
		forecastCards.setPadding(new Insets(10));

		ScrollPane scrollPane = new ScrollPane(forecastCards);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefViewportWidth(1000);
		scrollPane.setPannable(true);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

		Button backButton = new Button("Back Home");
		backButton.setStyle(buttonStyle());
		backButton.setOnAction(e -> switchScene(homeScene));

		Button detailsButton = new Button("Go To Details");
		detailsButton.setStyle(buttonStyle());
		detailsButton.setOnAction(e -> switchScene(detailsScene));

		HBox navButtons = new HBox(12, backButton, detailsButton);
		navButtons.setAlignment(Pos.CENTER);

		VBox root = new VBox(20, titleLabel, forecastCityLabel, scrollPane, navButtons);
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(25));
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");

		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		forecastScene = new Scene(root, 1000, 700);
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

		detailsScene = new Scene(root, 1000, 700);
	}

	private HBox createForecastCard() {
		HBox card = new HBox();
		card.setAlignment(Pos.CENTER_LEFT);
		card.setSpacing(30);
		card.setPadding(new Insets(18));
		card.setMaxWidth(Double.MAX_VALUE);
		card.setPrefHeight(120);

		card.setStyle("-fx-background-color: white;" + "-fx-background-radius: 16;" + "-fx-border-color: #d9e6f2;" + "-fx-border-radius: 16;");

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

				String dayForecast = "N/A";
				String dayTemp = "N/A";
				String nightForecast = "N/A";
				String nightTemp = "N/A";
				String dayWind = "N/A";
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

				Label periodLabel = new Label(df.dayName);
				periodLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1c1c1c;");
				periodLabel.setMinWidth(140);
				periodLabel.setPrefWidth(140);
				periodLabel.setMaxWidth(140);
				periodLabel.setWrapText(true);

				VBox dayColumn = new VBox(6);
				dayColumn.setAlignment(Pos.CENTER_LEFT);
				dayColumn.setPrefWidth(210);

				Label dayHeader = new Label("Day");
				dayHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1c1c1c;");

				Label dayInfo = new Label(
						"Forecast: " + dayForecast + "\n" +
								"Temp: " + dayTemp
				);
				dayInfo.setStyle("-fx-font-size: 15px; -fx-text-fill: #1c1c1c;");
				dayInfo.setWrapText(true);
				dayInfo.setMaxWidth(200);

				dayColumn.getChildren().addAll(dayHeader, dayInfo);

				VBox nightColumn = new VBox(6);
				nightColumn.setAlignment(Pos.CENTER_LEFT);
				nightColumn.setPrefWidth(250);

				Label nightHeader = new Label("Night");
				nightHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1c1c1c;");

				Label nightInfo = new Label(
						"Forecast: " + nightForecast + "\n" +
								"Temp: " + nightTemp
				);
				nightInfo.setStyle("-fx-font-size: 15px; -fx-text-fill: #1c1c1c;");
				nightInfo.setWrapText(true);
				nightInfo.setMaxWidth(240);

				nightColumn.getChildren().addAll(nightHeader, nightInfo);

				VBox windColumn = new VBox(6);
				windColumn.setAlignment(Pos.CENTER_LEFT);
				windColumn.setPrefWidth(220);

				Label windHeader = new Label("Wind");
				windHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1c1c1c;");

				Label windInfo = new Label(
						"Day: " + dayWind + "\n" +
								"Night: " + nightWind
				);
				windInfo.setStyle("-fx-font-size: 15px; -fx-text-fill: #1c1c1c;");
				windInfo.setWrapText(true);
				windInfo.setMaxWidth(210);

				windColumn.getChildren().addAll(windHeader, windInfo);

				cards[x].getChildren().addAll(periodLabel, dayColumn, nightColumn, windColumn);

				HBox.setHgrow(dayColumn, Priority.ALWAYS);
				HBox.setHgrow(nightColumn, Priority.ALWAYS);
				HBox.setHgrow(windColumn, Priority.ALWAYS);
				dayColumn.setMaxWidth(Double.MAX_VALUE);
				nightColumn.setMaxWidth(Double.MAX_VALUE);
				windColumn.setMaxWidth(Double.MAX_VALUE);
			} else {
				Label noDataLabel = new Label("No data available");
				noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #1c1c1c;");
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

		Platform.runLater(() -> {
			window.setFullScreen(wasFullScreen);
			window.setMaximized(wasMaximized);
		});
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
