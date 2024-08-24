import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



import org.json.*;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

        calendarYear year = new calendarYear();
        Scanner sc = new Scanner(System.in);

        WebRequest Request = new WebRequest();

        WeatherState weatherState = new WeatherState(new JSONObject(Request.getRealtimeWeather()));



        System.out.println("Launch App? Y/N");
        String answer = sc.next();

        boolean isAppRunning = answer.equals("Y");

        while (isAppRunning) {
            System.out.println("What do you wish to do? Type \"w\" for weather information, \"c\" to show cafes and bakeries in your vicinity, \"d\" to list upcoming events and \"e\" to add new Events.");
            String ans = sc.next();
            if (!ans.equals("w") && !ans.equals("c") && !ans.equals("d") && !ans.equals("e")) {
                System.out.println("Illegal input");
            }
            switch (ans) {
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

    public static String getDataFromJSON(String filename) throws IOException {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(filename);
        assert inputStream != null;
        return new String(inputStream.readAllBytes());
    }



}

















