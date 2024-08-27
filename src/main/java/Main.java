import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;


import org.json.JSONObject;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParseException, URISyntaxException {

        if (System.console() == null) {
            String filename  = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getName();

            File file = new File(Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().toString(), filename);
            String filepath = file.getAbsolutePath();


            ProcessBuilder processBuilder = new ProcessBuilder("osascript", "-e", "tell application \"Terminal\" to do script \"java -jar " + filepath + "\"");


            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            processBuilder.start();
        }

        else{
            System.out.println("Welcome to Spark!");













            ApplicationFilesHandler applicationFilesHandler = new ApplicationFilesHandler();



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
                        for (main.java.calendarDay day : year.getDaysWithEvents()) {
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
                        boolean validDate = false;
                        String date;
                        do {
                            System.out.println("Type in the date (please type it in this format:  dd.mm.yyyy, if you type in an invalid date, you will be prompted again!) :");
                            date = sc.next();
                            validDate = year.isValidDate(date);
                        } while (!validDate);

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





}

















