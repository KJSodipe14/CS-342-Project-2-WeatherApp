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
	private Label homeTempLabel;
	private Label homeForecastLabel;
	private ImageView homeWeatherImage;
	private Label statusLabel;

	//Forecast Scene
	private Label forecastCityLabel;
	private Label day1Label;
	private Label day2Label;
	private Label day3Label;
	private Label day4Label;
	private Label day5Label;

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

		Button viewForecastButton = new Button("View Forecast");
		viewForecastButton.setStyle(buttonStyle());
		viewForecastButton.setOnAction(e -> window.setScene(forecastScene));

		Button viewDetailsButton = new Button("View Details");
		viewDetailsButton.setStyle(buttonStyle());
		viewDetailsButton.setOnAction(e -> window.setScene(detailsScene));

		HBox navButtons = new HBox(12, viewForecastButton, viewDetailsButton);
		navButtons.setAlignment(Pos.CENTER);

		statusLabel = new Label();
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

		VBox weatherCard = new VBox(15,
				homeCityLabel,
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

		day1Label = createForecastCard();
		day2Label = createForecastCard();
		day3Label = createForecastCard();
		day4Label = createForecastCard();
		day5Label = createForecastCard();

		VBox forecastBox = new VBox(12, day1Label, day2Label, day3Label, day4Label, day5Label);
		forecastBox.setAlignment(Pos.CENTER);

		Button backButton = new Button("Back Home");
		backButton.setStyle(buttonStyle());
		backButton.setOnAction(e -> window.setScene(homeScene));

		Button detailsButton = new Button("Go To Details");
		detailsButton.setStyle(buttonStyle());
		detailsButton.setOnAction(e -> window.setScene(detailsScene));

		HBox navButtons = new HBox(12, backButton, detailsButton);
		navButtons.setAlignment(Pos.CENTER);

		VBox root = new VBox(20, titleLabel, forecastCityLabel, forecastBox, navButtons);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(30));
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");

		forecastScene = new Scene(root, 700, 650);
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
		backHomeButton.setOnAction(e -> window.setScene(homeScene));

		Button forecastButton = new Button("Forecast Scene");
		forecastButton.setStyle(buttonStyle());
		forecastButton.setOnAction(e -> window.setScene(forecastScene));

		HBox navButtons = new HBox(12, backHomeButton, forecastButton);
		navButtons.setAlignment(Pos.CENTER);

		VBox root = new VBox(20, titleLabel, detailsCard, navButtons);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(30));
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");

		detailsScene = new Scene(root, 700, 650);
	}

	private Label createForecastCard() {
		Label label = new Label();
		label.setMinHeight(70);
		label.setPrefWidth(500);
		label.setWrapText(true);
		label.setPadding(new Insets(15));
		label.setStyle(
				"-fx-background-color: white;" +
						"-fx-background-radius: 16;" +
						"-fx-border-color: #d9e6f2;" +
						"-fx-border-radius: 16;" +
						"-fx-font-size: 15px;"
		);
		return label;
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
		homeTempLabel.setText(current.temperature + "°" + current.temperatureUnit);
		homeForecastLabel.setText(current.shortForecast);
		homeWeatherImage.setImage(getWeatherImage(current.shortForecast));

		// Forecast scene
		forecastCityLabel.setText(currentCity + " Forecast");
		setForecastLabel(day1Label, 0);
		setForecastLabel(day2Label, 1);
		setForecastLabel(day3Label, 2);
		setForecastLabel(day4Label, 3);
		setForecastLabel(day5Label, 4);

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

	private void setForecastLabel(Label label, int index) {
		if (currentForecast.size() > index) {
			Period p = currentForecast.get(index);
			label.setText(
					p.name + "\n" +
							"Temp: " + p.temperature + "°" + p.temperatureUnit + "\n" +
							"Forecast: " + p.shortForecast
			);
		} else {
			label.setText("No data available");
		}
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

	private String buttonStyle() {
		return "-fx-background-color: #2d89ef;" +
				"-fx-text-fill: white;" +
				"-fx-font-size: 14px;" +
				"-fx-font-weight: bold;" +
				"-fx-background-radius: 12;" +
				"-fx-padding: 10 18 10 18;";
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
