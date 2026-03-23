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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import weather.Period;

import java.util.ArrayList;

//SceneTemplate is an abstract class that acts as a blueprint for building the Home, Forecast, and Details scenes.
abstract class SceneTemplate {
	//buildScene method builds and returns the fully constructed scene.
	public Scene buildScene() {
		//Initializes a new BorderPane layout.
		BorderPane root = new BorderPane();

		//Using the subclass implementation createCenter we set the center of the layout.
		root.setCenter(createCenter());
		//Like the above we use the implementation createBottom to set the bottom of the layout.
		root.setBottom(createBottom());

		//This applies the style of the scene. *This is how we can change the mode from light to dark mode.*
		applyStyle(root);

		//Returns the final Scene with a fixed width and height of 1000 x 700
		return new Scene(root, 1000, 700);
	}

	//Abstract method which makes the subclasses define what goes in the center.
	protected abstract javafx.scene.Node createCenter();

	//Abstract method which makes the subclasses define what goes in the bottom.
	protected abstract javafx.scene.Node createBottom();

	//Abstract method which makes the subclassses define how the style is applied.
	protected abstract void applyStyle(BorderPane root);
}

//WeatherAdapter class is used to simplify and format the raw weather data from the Period object.
class WeatherAdapter {

	//Stores the Period object which contains raw weather data.
	private Period period;

	//Constructor initializes the adapter with a specific Period object.
	public WeatherAdapter(Period period) {
		this.period = period;
	}

	//Returns the temperature as a formatted string * 72°F *
	public String getTemperature() {
		return period.temperature + "°" + period.temperatureUnit;
	}

	//Returns the short forecast description * Partly Cloudy *
	public String getShortForecast() {
		return period.shortForecast;
	}

	//Returns the wind speed and direction as a formatted string.
	public String getWind() {
		return period.windSpeed + " " + period.windDirection;
	}

	//Returns the chance of precipitation if available, otherwise returns N/A.
	public String getPrecipitation() {
		if (period.probabilityOfPrecipitation != null) {
			return period.probabilityOfPrecipitation.value + "%";
		}
		return "N/A";
	}

	//Returns the detailed forecast description.
	public String getDetailedForecast() {
		return period.detailedForecast;
	}
}

public class JavaFX extends Application {
	private Stage window;

	private Scene homeScene;
	private Scene forecastScene;
	private Scene detailsScene;

	private TextField cityInput;

	//Home Scene
	private Label homeTitleLabel;
	private Label homeCityLabel;
	private Label homeDayLabel;
	private Label homeTempLabel;
	private Label homeForecastLabel;
	private ImageView homeWeatherImage;
	private Label statusLabel;
	private VBox homeWeatherCard;
	private VBox homeRootContent;

	//Forecast Scene
	private Label forecastTitleLabel;
	private Label forecastCityLabel;
	private HBox day1Card;
	private HBox day2Card;
	private HBox day3Card;
	private HBox day4Card;
	private HBox day5Card;
	private VBox forecastRootContent;

	//Details Scene
	private Label detailsTitleLabel;
	private Label detailsCityLabel;
	private Label detailsTempLabel;
	private Label detailsWindLabel;
	private Label detailsPrecipLabel;
	private Label detailsLongForecastLabel;
	private ImageView detailsWeatherImage;
	private VBox detailsCard;
	private VBox detailsRootContent;

	private boolean darkMode = false;
	private final ArrayList<Button> themeButtons = new ArrayList<>();
	private final ArrayList<Label> themeLabels = new ArrayList<>();

	private ArrayList<Period> currentForecast;
	private String currentCity = "Chicago";

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("Weather App");

		buildHomeScene();
		buildForecastScene();
		buildDetailsScene();

		cityInput.setText("Chicago");
		loadWeather("Chicago");
		applyTheme();

		window.setScene(homeScene);
		window.setMinWidth(900);
		window.setMinHeight(650);
		window.show();
	}

	private void buildHomeScene() {
		homeTitleLabel = new Label("Weather App");
		homeTitleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");

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

		homeWeatherCard = new VBox(15,
				homeCityLabel,
				homeDayLabel,
				homeWeatherImage,
				homeTempLabel,
				homeForecastLabel
		);
		homeWeatherCard.setAlignment(Pos.CENTER);
		homeWeatherCard.setPadding(new Insets(25));
		homeWeatherCard.setMaxWidth(430);
		homeWeatherCard.setStyle(
				"-fx-background-color: white;" +
						"-fx-background-radius: 20;" +
						"-fx-border-color: #d9e6f2;" +
						"-fx-border-radius: 20;"
		);

		homeRootContent = new VBox(16, homeTitleLabel, searchBar, homeWeatherCard, navButtons);
		homeRootContent.setAlignment(Pos.CENTER);
		homeRootContent.setPadding(new Insets(25));

		statusLabel = new Label();
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px; -fx-font-weight: bold;");
		statusLabel.setVisible(false);
		statusLabel.setManaged(true);

		StackPane centerPane = new StackPane(homeRootContent, statusLabel);
		centerPane.setPadding(new Insets(0, 0, 25, 0));

		StackPane.setAlignment(statusLabel, Pos.BOTTOM_CENTER);
		StackPane.setMargin(statusLabel, new Insets(0, 0, 5, 0));

		//Creating a SceneTemplate object for the Home Scene.
		SceneTemplate homeTemplate = new SceneTemplate() {

			//Defines what will be placed in the center of the Home scene.
			@Override
			protected javafx.scene.Node createCenter() {
				return centerPane;
			}

			//Defines what will be placed at the bottom of the home scene.
			@Override
			protected javafx.scene.Node createBottom() {
				return createBottomBar();
			}

			//Applies the light blue gradient background style to the Home scene.
			@Override
			protected void applyStyle(BorderPane root) {
				root.setStyle("-fx-background-color: linear-gradient(to bottom, #87ceeb, #dff4ff);");
			}
		};

		//Builds the Home scene using the template structure defined in SceneTemplate.
		homeScene = homeTemplate.buildScene();
	}

	private void buildForecastScene() {
		forecastTitleLabel = new Label("Forecast");
		forecastTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");

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

		forecastRootContent = new VBox(20, forecastTitleLabel, forecastCityLabel, scrollPane, navButtons);
		forecastRootContent.setAlignment(Pos.TOP_CENTER);
		forecastRootContent.setPadding(new Insets(25));
		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		//Creating a SceneTemplate object for the Forecast scene.
		SceneTemplate forecastTemplate = new SceneTemplate() {

			//Defines what will be placed in the center of the Forecast scene.
			@Override
			protected javafx.scene.Node createCenter() {
				return forecastRootContent;
			}

			//Defines what will be placed at the bottom of the Forecast scene.
			@Override
			protected javafx.scene.Node createBottom() {
				return createBottomBar();
			}

			//Applies a slightly different blue gradient style to the Forecast scene.
			@Override
			protected void applyStyle(BorderPane root) {
				root.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");
			}
		};

		//Builds the Forecast scene using the template structure.
		forecastScene = forecastTemplate.buildScene();
	}

	private void buildDetailsScene() {
		detailsTitleLabel = new Label("Weather Details");
		detailsTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");

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
		detailsLongForecastLabel.setAlignment(Pos.CENTER);
		detailsLongForecastLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

		detailsCard = new VBox(14,
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

		detailsRootContent = new VBox(20, detailsTitleLabel, detailsCard, navButtons);
		detailsRootContent.setAlignment(Pos.TOP_CENTER);
		detailsRootContent.setPadding(new Insets(30));

		ScrollPane scrollPane = new ScrollPane(detailsRootContent);
		scrollPane.setFitToWidth(true);
		scrollPane.setPannable(true);
		scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

		//Creating a SceneTemplate object for the Details scene.
		SceneTemplate detailsTemplate = new SceneTemplate() {

			//Defines what will be placed in the center of the Scrollable etails scene.
			@Override
			protected javafx.scene.Node createCenter() {
				return scrollPane;
			}

			//Defines what will be placed at the bottom of the Details scene.
			@Override
			protected javafx.scene.Node createBottom() {
				return createBottomBar();
			}

			//Applies the same gradient as the Forecast scene to the Details scene.
			@Override
			protected void applyStyle(BorderPane root) {
				root.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");
			}
		};

		//Builds the Details scene using the template structure.
		detailsScene = detailsTemplate.buildScene();
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

	private VBox createThemeToggle() {
		Button moonButton = new Button("☾");
		moonButton.setMinSize(55, 55);
		moonButton.setMaxSize(55, 55);
		moonButton.setOnAction(e -> toggleDarkMode());

		Label modeLabel = new Label("Dark Mode");
		modeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

		themeButtons.add(moonButton);
		themeLabels.add(modeLabel);

		updateThemeButtonStyle(moonButton, modeLabel);

		VBox toggleBox = new VBox(6, moonButton, modeLabel);
		toggleBox.setAlignment(Pos.CENTER);
		return toggleBox;
	}

	private void toggleDarkMode() {
		darkMode = !darkMode;
		applyTheme();
	}

	private void updateThemeButtonStyle(Button button, Label label) {
		if (darkMode) {
			button.setStyle(
					"-fx-background-color: white;" +
							"-fx-text-fill: black;" +
							"-fx-font-size: 22px;" +
							"-fx-font-weight: bold;" +
							"-fx-background-radius: 30;" +
							"-fx-border-radius: 30;" +
							"-fx-border-color: #cccccc;"
			);
			label.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
		} else {
			button.setStyle(
					"-fx-background-color: black;" +
							"-fx-text-fill: white;" +
							"-fx-font-size: 22px;" +
							"-fx-font-weight: bold;" +
							"-fx-background-radius: 30;" +
							"-fx-border-radius: 30;" +
							"-fx-border-color: #333333;"
			);
			label.setStyle("-fx-text-fill: #1c3d5a; -fx-font-size: 12px; -fx-font-weight: bold;");
		}
	}

	private void applyTheme() {
		for (int x = 0; x < themeButtons.size(); x++) {
			updateThemeButtonStyle(themeButtons.get(x), themeLabels.get(x));
		}

		BorderPane homeRoot = (BorderPane) homeScene.getRoot();
		BorderPane forecastRoot = (BorderPane) forecastScene.getRoot();
		BorderPane detailsRoot = (BorderPane) detailsScene.getRoot();

		if (darkMode) {
			homeRoot.setStyle("-fx-background-color: linear-gradient(to bottom, #121212, #2b2b2b);");
			forecastRoot.setStyle("-fx-background-color: linear-gradient(to bottom, #121212, #2b2b2b);");
			detailsRoot.setStyle("-fx-background-color: linear-gradient(to bottom, #121212, #2b2b2b);");

			homeWeatherCard.setStyle(
					"-fx-background-color: #2f2f2f;" +
							"-fx-background-radius: 20;" +
							"-fx-border-color: #555555;" +
							"-fx-border-radius: 20;"
			);

			detailsCard.setStyle(
					"-fx-background-color: #2f2f2f;" +
							"-fx-background-radius: 20;" +
							"-fx-border-color: #555555;" +
							"-fx-border-radius: 20;"
			);

			homeTitleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: white;");
			forecastTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
			detailsTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

			homeCityLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
			homeDayLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #d0d0d0;");
			homeTempLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
			homeForecastLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
			statusLabel.setStyle("-fx-text-fill: #ff7b7b; -fx-font-size: 13px;");

			forecastCityLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

			detailsCityLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
			detailsTempLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
			detailsWindLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
			detailsPrecipLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
			detailsLongForecastLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");

			cityInput.setStyle(
					"-fx-background-color: #2f2f2f;" +
							"-fx-text-fill: white;" +
							"-fx-prompt-text-fill: #bbbbbb;" +
							"-fx-background-radius: 10;" +
							"-fx-border-radius: 10;" +
							"-fx-border-color: #555555;"
			);

		} else {
			homeRoot.setStyle("-fx-background-color: linear-gradient(to bottom, #87ceeb, #dff4ff);");
			forecastRoot.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");
			detailsRoot.setStyle("-fx-background-color: linear-gradient(to bottom, #9ed8ff, #eef9ff);");

			homeWeatherCard.setStyle(
					"-fx-background-color: white;" +
							"-fx-background-radius: 20;" +
							"-fx-border-color: #d9e6f2;" +
							"-fx-border-radius: 20;"
			);

			detailsCard.setStyle(
					"-fx-background-color: white;" +
							"-fx-background-radius: 20;" +
							"-fx-border-color: #d9e6f2;" +
							"-fx-border-radius: 20;"
			);

			homeTitleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");
			forecastTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");
			detailsTitleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1c3d5a;");

			homeCityLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
			homeDayLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #4a6572;");
			homeTempLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
			homeForecastLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
			statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

			forecastCityLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: black;");

			detailsCityLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
			detailsTempLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
			detailsWindLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
			detailsPrecipLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
			detailsLongForecastLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: black;");

			cityInput.setStyle("");
		}

		updateForecastCardTheme();
	}

	private HBox createBottomBar() {
		VBox themeToggle = createThemeToggle();

		HBox bottomBar = new HBox(themeToggle);
		bottomBar.setAlignment(Pos.CENTER_RIGHT);
		bottomBar.setPadding(new Insets(0, 20, 20, 0));
		bottomBar.setMinHeight(135);
		bottomBar.setPrefHeight(135);
		bottomBar.setMaxHeight(135);

		return bottomBar;
	}

	private void updateForecastCardTheme() {
		HBox[] cards = {day1Card, day2Card, day3Card, day4Card, day5Card};

		for (HBox card : cards) {
			if (darkMode) {
				card.setStyle(
						"-fx-background-color: #2f2f2f;" +
								"-fx-background-radius: 16;" +
								"-fx-border-color: #555555;" +
								"-fx-border-radius: 16;"
				);
			} else {
				card.setStyle(
						"-fx-background-color: white;" +
								"-fx-background-radius: 16;" +
								"-fx-border-color: #d9e6f2;" +
								"-fx-border-radius: 16;"
				);
			}
		}

		if (currentForecast != null) {
			updateFiveDayForecastCards();
		}
	}

	private void searchCity() {
		String city = cityInput.getText().trim();
		if (!city.isEmpty()) {
			loadWeather(city);
		}
	}

	private void loadWeather(String cityName) {
		statusLabel.setText("");
		statusLabel.setVisible(false);

		if (cityName == null || cityName.trim().isEmpty()) {
			statusLabel.setText("Please enter a city name.");
			statusLabel.setVisible(true);
			return;
		}

		String cleanCity = cityName.trim();
		ArrayList<Period> forecast = MyWeatherAPI.getForecastForCity(cleanCity);

		if ((forecast == null || forecast.isEmpty()) && cleanCity.equalsIgnoreCase("Chicago")) {
			forecast = weather.WeatherAPI.getForecast("LOT", 77, 70);
		}

		if (forecast == null || forecast.isEmpty()) {
			statusLabel.setText("City not found or NWS data unavailable.");
			statusLabel.setVisible(true);
			return;
		}

		currentForecast = forecast;
		currentCity = formatCityName(cleanCity);

		updateScenes();
	}

	//updateScenes method updates all scenes with current weather data.
	private void updateScenes() {
		//If there is no forecast data available, exit the method.
		if (currentForecast == null || currentForecast.isEmpty()) {
			return;
		}

		//Creates a WeatherAdapter object for the current forecast period.
		WeatherAdapter current = new WeatherAdapter(currentForecast.get(0));

		//Home scene
		//Sets the city name on the home screen.
		homeCityLabel.setText(currentCity);
		//Gets the raw Period object to extract and format the day of the week.
		Period raw = currentForecast.get(0);
		String day = raw.startTime.toInstant().atZone(java.time.ZoneId.systemDefault()).getDayOfWeek().toString();
		//Formats the day string from MONDAY to Monday and sets it.
		homeDayLabel.setText(day.substring(0, 1) + day.substring(1).toLowerCase());
		//Sets the temperature using the adapter.
		homeTempLabel.setText(current.getTemperature());
		//Sets the short forecast description.
		homeForecastLabel.setText(current.getShortForecast());
		//Sets the weather image based on the forecast.
		homeWeatherImage.setImage(getWeatherImage(current.getShortForecast()));

		// Forecast scene
		//Sets the forecast title with the city name.
		forecastCityLabel.setText(currentCity + " 5-Day Forecast");
		//Updates the 5-day forecast cards.
		updateFiveDayForecastCards();

		// Details scene
		//Sets the city name on the Details screen.
		detailsCityLabel.setText(currentCity);
		//Sets the weather image for the Details screen.
		detailsWeatherImage.setImage(getWeatherImage(current.getShortForecast()));
		//Sets the temperature with a label.
		detailsTempLabel.setText("Temperature: " + current.getTemperature());
		//Sets the wind information.
		detailsWindLabel.setText("Wind: " + current.getWind());
		//Sets the precipitation chance.
		detailsPrecipLabel.setText("Chance of precipitation: " + current.getPrecipitation());
		//Sets the detailed forecast description.
		detailsLongForecastLabel.setText("Details: " + current.getDetailedForecast());

		//Applies the current theme either light or dark mode to all scenes.
		applyTheme();
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
				periodLabel.setStyle(cardTitleStyle());
				periodLabel.setMinWidth(140);
				periodLabel.setPrefWidth(140);
				periodLabel.setMaxWidth(140);
				periodLabel.setWrapText(true);

				VBox dayColumn = new VBox(6);
				dayColumn.setAlignment(Pos.CENTER_LEFT);
				dayColumn.setPrefWidth(210);

				Label dayHeader = new Label("Day");
				dayHeader.setStyle(cardHeaderStyle());

				Label dayInfo = new Label(
						"Forecast: " + dayForecast + "\n" +
								"Temp: " + dayTemp
				);
				dayInfo.setStyle(cardBodyStyle());
				dayInfo.setWrapText(true);
				dayInfo.setMaxWidth(200);

				dayColumn.getChildren().addAll(dayHeader, dayInfo);

				VBox nightColumn = new VBox(6);
				nightColumn.setAlignment(Pos.CENTER_LEFT);
				nightColumn.setPrefWidth(250);

				Label nightHeader = new Label("Night");
				nightHeader.setStyle(cardHeaderStyle());

				Label nightInfo = new Label(
						"Forecast: " + nightForecast + "\n" +
								"Temp: " + nightTemp
				);
				nightInfo.setStyle(cardBodyStyle());
				nightInfo.setWrapText(true);
				nightInfo.setMaxWidth(240);

				nightColumn.getChildren().addAll(nightHeader, nightInfo);

				VBox windColumn = new VBox(6);
				windColumn.setAlignment(Pos.CENTER_LEFT);
				windColumn.setPrefWidth(220);

				Label windHeader = new Label("Wind");
				windHeader.setStyle(cardHeaderStyle());

				Label windInfo = new Label(
						"Day: " + dayWind + "\n" +
								"Night: " + nightWind
				);
				windInfo.setStyle(cardBodyStyle());
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
				noDataLabel.setStyle(cardBodyStyle());
				cards[x].getChildren().add(noDataLabel);
			}
		}
	}

	private String cardTitleStyle() {
		if (darkMode) {
			return "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;";
		}
		return "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1c1c1c;";
	}

	private String cardHeaderStyle() {
		if (darkMode) {
			return "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;";
		}
		return "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1c1c1c;";
	}

	private String cardBodyStyle() {
		if (darkMode) {
			return "-fx-font-size: 15px; -fx-text-fill: #e6e6e6;";
		}
		return "-fx-font-size: 15px; -fx-text-fill: #1c1c1c;";
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

	private String formatCityName(String cityName) {
		String[] parts = cityName.trim().toLowerCase().split("\\s+");
		StringBuilder formatted = new StringBuilder();

		for (int x = 0; x < parts.length; x++) {
			if (!parts[x].isEmpty()) {
				formatted.append(parts[x].substring(0, 1).toUpperCase());
				if (parts[x].length() > 1) {
					formatted.append(parts[x].substring(1));
				}
				if (x < parts.length - 1) {
					formatted.append(" ");
				}
			}
		}

		return formatted.toString();
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
}