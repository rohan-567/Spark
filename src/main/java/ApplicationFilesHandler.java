

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Scanner;

public class ApplicationFilesHandler {

    public static  File applicationFolder;
    public static File savedCalendar;
    public static File apiKeys;

    public ApplicationFilesHandler() throws IOException, InterruptedException {
        applicationFolder = new File(System.getProperty("user.home") + File.separator +"Spark");
        savedCalendar = new File(applicationFolder, "savedCalendar.json");
        apiKeys = new File(applicationFolder, "apiKeys.json");

        Scanner sc = new Scanner(System.in);

        if (!applicationFolder.exists()){
            try{
                applicationFolder.mkdir();
                System.out.println("Folder created: " + applicationFolder.getAbsolutePath());
            }
            catch (Exception e){System.out.println("Error: " + e.getMessage()); }
        }



        if (!savedCalendar.exists()){
            try{
                Files.writeString(savedCalendar.toPath(), "{}");
                System.out.println("File created at: " + savedCalendar.getAbsolutePath());
            }
            catch (Exception e){System.out.println("Error: " + e.getMessage()); }
        }
        if (!apiKeys.exists()){
            try{
                Files.writeString(apiKeys.toPath(), "{}");
                System.out.println("File created at: " + apiKeys.getAbsolutePath());
            }
            catch (Exception e){System.out.println("Error: " + e.getMessage()); }
        }


        String apiKeysContent = Files.readString(apiKeys.toPath());


        JSONObject apiKeysObject = new JSONObject();
        try{apiKeysObject = new JSONObject(apiKeysContent);}
        catch (Exception e){System.out.println("Error: " + e.getMessage());}

        if (!apiKeysObject.has("locationKey")){
            System.out.println("It seems the API key for the location API is missing. Please enter it here: ");
            String locationKey = sc.next();
            boolean valid = validateAPIkey("location", locationKey);
            while (!valid){
                System.out.println("It seems the API key for the location API is not valid. Please enter a valid key: ");
                locationKey = sc.next();
                valid = validateAPIkey("location", locationKey);
            }



            apiKeysObject.put("locationKey", locationKey);
            try (Writer writer = new FileWriter(apiKeys)) {
                apiKeysObject.write(writer);
                System.out.println("The API key for the location service has been successfully added.");
            }
            catch (Exception e){System.out.println("Error: " + e.getMessage()); }

        }

        if (!apiKeysObject.has("weatherKey")){
            System.out.println("It seems the API key for the weather API is missing. Please enter it here: ");
            String weatherKey = sc.next();
            boolean valid = validateAPIkey("weather", weatherKey);
            while (!valid){
                System.out.println("It seems the API key for the weather API is not valid. Please enter a valid key: ");
                weatherKey = sc.next();
                valid = validateAPIkey("weather", weatherKey);
            }



            apiKeysObject.put("weatherKey", weatherKey);
            try (Writer writer = new FileWriter(apiKeys)) {
                apiKeysObject.write(writer);
                System.out.println("The API key for the weather service has been successfully added.");
            }
            catch (Exception e){System.out.println("Error: " + e.getMessage()); }

        }

    }



    public static JSONObject getDataFromJSON(String type) throws IOException {
        if (type.equals("calendar")){
            return new JSONObject(Files.readString(savedCalendar.toPath()));
        }
        if (type.equals("api")){
            return new JSONObject(Files.readString(apiKeys.toPath()));
        } else {
            return null;
        }
    }

    public static boolean validateAPIkey(String type, String key) throws IOException, InterruptedException {
        WebRequest webRequest = new WebRequest("");
        if (type.equals("weather")){
            String result = webRequest.makeRequest("https://api.tomorrow.io/v4/weather/realtime?location=toronto&apikey=%s".formatted(key));
            return !result.contains("invalid");
        }
        if (type.equals("location")){
            String res = webRequest.getLocation(key);
            return !res.contains("not valid");
        }
        else {
            return false;
        }

    }








    }








