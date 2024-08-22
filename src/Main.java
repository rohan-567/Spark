import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import java.util.*;



import org.json.*;





public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {



        String filePath = "src/apiKeys.json";
        String apiKeys = new String(Files.readAllBytes(Paths.get(filePath)));
        System.out.println(apiKeys);







        JSONObject keys = new JSONObject(apiKeys);







        //sample calendar data: {"01.07.2024":["Sommer","Test","Test2","Test3"],"16.07.2024":["Präsentation"], "16.08.2024": ["LOL"]}
        // sample api data: {"locationKey":"a0b5f058d24b41d3b58d563515c32815","weatherKey":"ClwhuEl1QAvRGyVnOFd1dH63ixsvRisX"}


        Scanner sc = new Scanner(System.in);

        if (keys.getString("locationKey").isEmpty() || keys.getString("weatherKey").isEmpty()) {
            System.out.println("It seems that the app is missing api keys. Please add them.");
            System.out.println("Enter the apikey for the location service:");
            String locationKey = sc.next();
            System.out.println("Enter the apikey for the weather service:");
            String weatherKey = sc.next();
            keys.put("locationKey", locationKey);
            keys.put("weatherKey", "&apikey="  +weatherKey);
            File savedKeys = new File("src/apiKeys.json");
            try (Writer writer = new FileWriter(savedKeys, false)) {
                keys.write(writer);
            } catch (IOException ignored) {

            }


        }










        WebRequest Request = new WebRequest();

        Request.setLocationKey(keys.getString("locationKey"));
        Request.setWeatherKey(keys.getString("weatherKey"));








        calendarYear year = new calendarYear();
        WeatherState weatherState = new WeatherState(new JSONObject(Request.getRealtimeWeather()));












        System.out.println("Launch App? Y/N");
        String answer = sc.next();

        boolean isAppRunning = answer.equals("Y");

        while (isAppRunning) {
            System.out.println("What do you wish to do? Type \"w\" for weather information, \"c\" to show cafes and bakeries in your vicinity, \"d\" to list upcoming events , \"e\" to add new Events and \"a\" to edit the api keys"  );
            String ans = sc.next();
            if (!ans.equals("w") && !ans.equals("c") && !ans.equals("d") && !ans.equals("e")) {
                System.out.println("Illegal input");
            }
            switch (ans) {
                case "a" -> {
                    System.out.println("Enter the apikey for the location service:");
                    String locationKey = sc.next();
                    System.out.println("Enter the apikey for the weather service:");
                    String weatherKey = sc.next();
                    keys.put("locationKey", locationKey);
                    keys.put("weatherKey", weatherKey);
                    File apiKeysFile = new File("src/apiKeys.json");
                    try (Writer writer = new FileWriter(apiKeysFile, false)) {
                keys.write(writer);
            } catch (IOException ignored) {

            }
                }
                case "w" -> System.out.println(weatherState.makeImmediateRecommendation());
                case "c" -> System.out.println(Request.getAmenities());
                case "d" -> {
                    for (calendarDay day : year.getDaysWithEvents()) {
                        System.out.println("Events on " + day.day + "." + day.month + "." + year.currentYear + " :" + "\n");
                        for (String event : day.getEvents()) {
                            System.out.println(event + "\n");
                        }
                        System.out.println("\n");
                    }


                }
                case "e" -> {
                    System.out.println("Type in your desired event name (please use underscores instead of spaces):");
                    String name = sc.next();
                    System.out.println("Type in the date (please type it in this format:  dd.mm.yyyy ):");
                    String date = sc.next();
                    year.addEvent(year.parseInputtedDate(date), name);



                }

            }


            System.out.println("Do you wish to exit the program? Type \"N\" to exit or anything else to return to the start menu.");
            String ans2 = sc.next();
            if ((ans2.equals("N"))) {
                isAppRunning = false;
            }

        }
    }
}

















