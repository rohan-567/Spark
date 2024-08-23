import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class WeatherState {
    double temperature;
    double humidity;

    double precipitationProbability;

    double rainIntensity;

    double snowIntensity;

    double windSpeed;

    double windGust;

    double uvIndex;

    int weatherCode;

    public WeatherState(JSONObject weather){

        JSONObject data = weather.getJSONObject("data");
        JSONObject values = data.getJSONObject("values");


        this.temperature = values.getDouble("temperature");
        this.precipitationProbability = values.getDouble("precipitationProbability");
        this.rainIntensity = values.getDouble("rainIntensity");
        this.snowIntensity = values.getDouble("snowIntensity");
        this.windSpeed = values.getDouble("windSpeed");
        this.windGust = values.getDouble("windGust");
        this.weatherCode = values.getInt("weatherCode");
        this.uvIndex = values.getDouble("uvIndex");
        this.humidity = values.getDouble("humidity");
    }

    public String makeImmediateRecommendation(){
        HashMap<Integer, String> weatherCodes = new HashMap<>();
        weatherCodes.put(0, "Unknown");
        weatherCodes.put(1000, "Clear, Sunny");
        weatherCodes.put(1100, "Mostly Clear");
        weatherCodes.put(1101, "Partly Cloudy");
        weatherCodes.put(1102, "Mostly Cloudy");
        weatherCodes.put(1001, "Cloudy");
        weatherCodes.put(2000, "Fog");
        weatherCodes.put(2100, "Light Fog");
        weatherCodes.put(4000, "Drizzle");
        weatherCodes.put(4001, "Rain");
        weatherCodes.put(4200, "Light Rain");
        weatherCodes.put(4201, "Heavy Rain");
        weatherCodes.put(5000, "Snow");
        weatherCodes.put(5001, "Flurries");
        weatherCodes.put(5100, "Light Snow");
        weatherCodes.put(5101, "Heavy Snow");
        weatherCodes.put(6000, "Freezing Drizzle");
        weatherCodes.put(6001, "Freezing Rain");
        weatherCodes.put(6200, "Light Freezing Rain");
        weatherCodes.put(6201, "Heavy Freezing Rain");
        weatherCodes.put(7000, "Ice Pellets");
        weatherCodes.put(7101, "Heavy Ice Pellets");
        weatherCodes.put(7102, "Light Ice Pellets");
        weatherCodes.put(8000, "Thunderstorm");



        String clothing;


        if (temperature <= 5){
            clothing = "warm and insulating jackets, as well as thermal wear";

        }
        else if (5 < temperature && temperature < 15){
            clothing = "light jackets and sweaters";
        }
        else if (temperature > 15 && temperature < 20){
            clothing = "long sleeved shirts and jeans";
        }
        else{
            clothing = "t-shirts, shorts, sundresses etc.";


        }

        String precipChance = Double.toString(precipitationProbability *100);

        String precipitation ="";
        Double precipitationIntensity = 0.0;

        if (rainIntensity > 0){
            precipitation = "rain";
            precipitationIntensity = rainIntensity;

        }
        if(snowIntensity > 0 ){
            precipitation = "snow";
            precipitationIntensity = snowIntensity;
        }







        String recommendation ="";

        recommendation += String.format("The general weather is: %s.", weatherCodes.get(weatherCode)) +"\n";
        recommendation += String.format("The temperature is: %s",temperature) + String.format("°. So you should wear %s",clothing) +"\n";
        if (!(precipitation.isEmpty())){
            recommendation += String.format("There is a %s", precipitationProbability) + String.format("of %s.", precipitation)+ String.format("with an intensity of %s/1",precipitationIntensity) + String.format("Make sure your clothing is %s-proof",precipitation) +"\n";
        }
        if (uvIndex > 3){
            recommendation += String.format("Currently there is a UV-index of %s.",uvIndex) + "Please use protection against the sun like sunscreen to avoid damage to skin." +"\n";
        }
        if (humidity > 0.5){
            recommendation += String.format("The humidity sits at %s percent.",humidity) + "Therefore, make sure the clothing you are wearing is breathable" +"\n";
        }
        if (windSpeed > 45){
            recommendation += String.format("Right now there is strong wind with a speed of %s",windSpeed) + "km/h. Please be careful when going outside" +"\n";
        }






        return  recommendation;

    }








}
