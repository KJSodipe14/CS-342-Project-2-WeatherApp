import weather.Period;
import weather.WeatherAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyWeatherAPI {

    public static class GridResult {
        public String cityName;
        public String region;
        public int gridX;
        public int gridY;

        public GridResult(String cityName, String region, int gridX, int gridY) {
            this.cityName = cityName;
            this.region = region;
            this.gridX = gridX;
            this.gridY = gridY;
        }
    }

    public static GridResult getGridInfoFromCity(String cityName) {
        try {
            double[] coordinates = getCoordinatesFromCity(cityName);

            if (coordinates == null) {
                return null;
            }

            return getGridInfoFromCoordinates(cityName, coordinates[0], coordinates[1]);
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Period> getForecastForCity(String cityName) {
        try {
            GridResult grid = getGridInfoFromCity(cityName);

            if (grid == null) {
                return null;
            }

            return WeatherAPI.getForecast(grid.region, grid.gridX, grid.gridY);
        } catch (Exception e) {
            return null;
        }
    }

    private static double[] getCoordinatesFromCity(String cityName) {
        try {
            String fullQuery = cityName + ", USA";
            String encodedQuery = URLEncoder.encode(fullQuery, StandardCharsets.UTF_8);
            String urlString = "https://nominatim.openstreetmap.org/search?q=" + encodedQuery + "&format=jsonv2&limit=1";

            String json = sendGetRequest(urlString);

            if (json == null || json.isEmpty() || json.equals("[]")) {
                return null;
            }

            String latText = extractValue(json, "\"lat\"\\s*:\\s*\"([^\"]+)\"");
            String lonText = extractValue(json, "\"lon\"\\s*:\\s*\"([^\"]+)\"");

            if (latText == null || lonText == null) {
                return null;
            }

            double lat = Double.parseDouble(latText);
            double lon = Double.parseDouble(lonText);

            return new double[]{lat, lon};
        } catch (Exception e) {
            return null;
        }
    }

    private static GridResult getGridInfoFromCoordinates(String cityName, double lat, double lon) {
        try {
            String urlString = "https://api.weather.gov/points/" + lat + "," + lon;
            String json = sendGetRequest(urlString);

            if (json == null || json.isEmpty()) {
                return null;
            }

            String region = extractValue(json, "\"gridId\"\\s*:\\s*\"([^\"]+)\"");
            String gridXText = extractValue(json, "\"gridX\"\\s*:\\s*(\\d+)");
            String gridYText = extractValue(json, "\"gridY\"\\s*:\\s*(\\d+)");

            if (region == null || gridXText == null || gridYText == null) {
                return null;
            }

            int gridX = Integer.parseInt(gridXText);
            int gridY = Integer.parseInt(gridYText);

            return new GridResult(cityName, region, gridX, gridY);
        } catch (Exception e) {
            return null;
        }
    }

    private static String sendGetRequest(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "WeatherApp/1.0 (student project)");
            connection.setRequestProperty("Accept", "application/geo+json, application/json");

            int status = connection.getResponseCode();

            if (status != 200) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorText = new StringBuilder();
                String line;

                while ((line = errorReader.readLine()) != null) {
                    errorText.append(line);
                }

                errorReader.close();
                System.out.println("HTTP Error " + status + " for URL: " + urlString);
                System.out.println(errorText);
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        } catch (Exception e) {
            System.out.println("Request failed for URL: " + urlString);
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String extractValue(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}