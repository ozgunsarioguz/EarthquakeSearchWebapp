package ozgun.springframework.springwebapp.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import ozgun.springframework.springwebapp.Model.Earthquake;

@Service
public class EarthquakeService {
    
    public static List<Earthquake> getEarthquakes(String country, int days){
        List<Earthquake> queryResult = new ArrayList<Earthquake>();

        LocalDate startDate = LocalDate.now().minusDays(days);
        String startTime = startDate.getYear() + "-" + startDate.getMonthValue() + "-" + startDate.getDayOfMonth();
        String earthquakeApiUrl = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime="+ startTime;

        String apiResponse = CallAPI(earthquakeApiUrl);
        
        if (apiResponse.toLowerCase().contains(country)){
            queryResult = ApiResultParser(apiResponse, country);
        }
        return queryResult;
    }
    
    public static String CallAPI(String strRequestURL) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(strRequestURL)).build();
        var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .join();

        return response;

    }
    
    public static List<Earthquake> ApiResultParser (String responseString, String country) {

        List<Earthquake> earthquakeList = new ArrayList<Earthquake>();
        System.out.println("ApiResultParser runs...");

        JSONObject responseJson = new JSONObject(responseString);

        String earthquakeFeaturesJson = responseJson.get("features").toString();

        JSONArray arrayEarthquakes = new JSONArray(earthquakeFeaturesJson);

        for (int i=0; i < arrayEarthquakes.length(); i++){
            JSONObject arrayItem = arrayEarthquakes.getJSONObject(i);
            
            JSONObject properties = arrayItem.getJSONObject("properties");
            String place = properties.get("place").toString();

            if (place.toLowerCase().contains(country)){

                Long stampDate = properties.getLong("time");

                Timestamp stamp = new Timestamp(stampDate);
                Date date = new Date(stamp.getTime());
                String stringDate = date.toLocalDate().getMonth() + " " + date.toLocalDate().getDayOfMonth() + "," + date.toLocalDate().getYear();

                LocalDateTime dateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(stampDate), TimeZone.getDefault().toZoneId());   
                String stringTime = dateTime.getHour() + ":" + dateTime.getMinute() + ":" + dateTime.getSecond();

                earthquakeList.add(new Earthquake(country, place, properties.get("mag").toString(), stringDate, stringTime));
            }
        }
        return earthquakeList;
    }
}