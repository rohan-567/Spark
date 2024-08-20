import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;


public class WebRequest {

    private String weatherKey;
    private String locationKey;






    public String makeRequest(String Address) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Address))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    } //Makes an HTTP Request and expects a JSON format answer

    public String getLocation() throws IOException, InterruptedException{
        return makeRequest("https://api.ipgeolocation.io/ipgeo?apiKey=%s&output=json".formatted(locationKey));



    }

    public String getRealtimeWeather() throws IOException, InterruptedException{

        JSONObject location = new JSONObject(getLocation());
        String city = location.getString("city");
        city = city.substring(0,1).toLowerCase() + city.substring(1);

        return makeRequest("https://api.tomorrow.io/v4/weather/realtime?location=%s&%s".formatted(city,weatherKey));

    }



    public String getAmenities() throws  IOException, InterruptedException{
        JSONObject location = new JSONObject(getLocation());
        Double latitude = location.getDouble("latitude");
        Double longitude = location.getDouble("longitude");
        int radius = 500;

        String query = String.format("""
            [out:json];
            (
              node["amenity"="cafe"](around:500,%.4f,%.4f);
              node["shop"="bakery"](around:500,%.4f,%.4f);
            );
            out;
            """,latitude,longitude,latitude,longitude);

        query = URLEncoder.encode(query,StandardCharsets.UTF_8); //encodes the special commands needed for the overpass api

        String res = makeRequest("https://overpass-api.de/api/interpreter?data=" + query);

        JSONObject amenityLst = new JSONObject(res);

        JSONArray amenities = amenityLst.getJSONArray("elements");

        String result = "";

        for (int i=0; i < amenities.length(); i++){
            JSONObject amenity = amenities.getJSONObject(i).getJSONObject("tags");
            if (amenity.has("amenity")){
                String type = amenity.getString("amenity");
                result += type.substring(0,1).toUpperCase() + type.substring(1);
            }
            if (amenity.has("shop")){
                String type = amenity.getString("shop");
                result += type.substring(0,1).toUpperCase() + type.substring(1);
            }

            try{
            result += " " + amenity.getString("name") +"\n" ;}
            catch (Exception e){
                result += " Unknown name \n";
            }


            String address = "";

            if (amenity.has("opening_hours")){
                address +=   amenity.getString("opening_hours") + "\n";
            }
            address += "Addresse: ";
            if (amenity.has("addr:street")){
                address += amenity.getString("addr:street") + " ";
            }
            if (amenity.has("addr:housenumber")){
                address += amenity.getString("addr:housenumber") + " ";
            }
            if (amenity.has("addr:postcode")){
                address += amenity.getString("addr:postcode") + " ";
            }

            result += address;

            if (address.trim().isEmpty()){
                result += "N/A";
            }





            result += "\n";

            if (amenity.has("payment:contactless")){
                result += "Kartenzahlung:" + amenity.getString("payment:contactless") + "\n";

            }








            result += "\n";






        }





        return  result;




    }









    }



