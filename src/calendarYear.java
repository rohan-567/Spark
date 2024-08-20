
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

public class calendarYear {



    public int currentYear = Year.now().getValue();

    calendarDay[] calendarDays;

    /**
     * Returns a List of calendarDay's that have one or more events.
     *
     * @return a List of calendarDay's that have one or more events
     */
    public List<calendarDay> getDaysWithEvents() {
        List<calendarDay> daysWithEvents = new ArrayList<>(); // List to store calendarDay's with events
        int j=0; // Index for daysWithEvents

        // Iterate over all calendarDays
        for (int i=0; i<calendarDays.length; i++){
            // If the current calendarDay has events, add it to daysWithEvents
            if (!(calendarDays[i].getEvents().isEmpty())){
                daysWithEvents.add(j,calendarDays[i]);
                j++;
            }
        }
        return daysWithEvents;
    }


    public calendarYear() throws IOException, ParseException { //Constructs an array of calendarDays which also takes leap years into consideration


        int yearLength = 365;
        if (Year.isLeap(currentYear))yearLength ++;
        calendarDay[] dayList = new calendarDay[yearLength];

        for (int i = 0; i < yearLength; i++) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            LocalDate ld = Year.of(currentYear).atDay(i + 1);
            int month = ld.getMonthValue() - 1;

            gregorianCalendar.set(ld.getYear(), month, ld.getDayOfMonth());

            dayList[i] = new calendarDay(gregorianCalendar, new ArrayList<>());
        }

        this.calendarDays = dayList;
        loadEvents(); //this call makes it so that the year data structure possesses previously entered events
        removeOutdatedEvents();
    }

    public GregorianCalendar parseInputtedDate(String date){
        String[] lst = date.split("\\.");
        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        gregorianCalendar.set(Integer.valueOf(lst[2]), Integer.parseInt(lst[1])-1, Integer.parseInt(lst[0]));

        return gregorianCalendar;
    } //creates an array of Strings that represent year, month and day which then get parsed into the calendar



    public void addEvent(GregorianCalendar eventDay, String eventName) throws IOException {

        storeEvents(eventDay,eventName);

        int dayOfYear = eventDay.get(GregorianCalendar.DAY_OF_YEAR)-1;



            calendarDay calendarDay = calendarDays[dayOfYear];
            calendarDay.month = eventDay.get(GregorianCalendar.MONTH) + 1;
            List<String> events = calendarDay.getEvents();
            events.add(eventName);





    } //helper function that adds Events to the list of events of one particular day




    /**
     * Stores the given event name on the given date in the saved JSON file.
     * If the date already exists in the file, the event name is added to the list of events for that date.
     * If the date does not exist in the file, a new entry is created with the event name.
     * @param calendar the date to store the event on
     * @param eventName the name of the event to store
     * @throws IOException when there is an issue with reading or writing the file
     */
    public void storeEvents(GregorianCalendar calendar, String eventName) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = simpleDateFormat.format(calendar.getTime());

        File savedCalendarFile = new File("/Users/rohan/IdeaProjects/Spark_POC/src/savedCalendar.json");

        String filePath = "/Users/rohan/IdeaProjects/Spark_POC/src/savedCalendar.json";
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject objectsToStore = new JSONObject();



        try{objectsToStore = new JSONObject(content);}
        catch (Exception e){}

        JSONArray events = new JSONArray();
        Writer writer = new FileWriter(savedCalendarFile);

        if (objectsToStore.has(dateString)){
            events = objectsToStore.getJSONArray(dateString);
            objectsToStore.put(dateString,events.put(eventName));
            objectsToStore.write(writer);
            writer.close();
            return;

        }

        objectsToStore.put(dateString,events.put(eventName));
        objectsToStore.write(writer);
        writer.close();

        /*This takes a calendar and en event name and adds it to the json file pertaining to events. It even allows storage of events beyond the current year.
        If the json file already has the day you're trying to add to, it will update the event list. Otherwise, it will create a new entry.

       */

    }



    /**
     * Loads events from the saved JSON file and adds them to the respective days in the calendarYear.
     * @throws IOException when there is an issue with reading the file
     * @throws ParseException when there is an issue with parsing the date
     */
    public void loadEvents() throws IOException, ParseException {

        // Path to the saved JSON file

        String filePath = "/savedCalendar.json";
        // Read the content of the file
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        // Parse the content into a JSON object
        JSONObject objectsToStore = new JSONObject(content);

        // Iterate over the keys in the JSON object
        Iterator<String> keys = objectsToStore.keys();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        // Iterate over each key in the JSON object

        while (keys.hasNext()){
            String next = keys.next();

            // Check if the key contains the currentyear
            if(next.contains(Integer.toString(currentYear))){

                // Get the event list for the current key

                JSONArray eventList = objectsToStore.getJSONArray(next);

                // Parse the key into a Date object


                Date date = simpleDateFormat.parse(next);
                // Set the Date object in the GregorianCalendar
                gregorianCalendar.setTime(date);
                // Get the events list for the corresponding day in the calendarYear
                List<String> eventsToAdd  = this.calendarDays[gregorianCalendar.get(GregorianCalendar.DAY_OF_YEAR ) -1 ].events;
                // Add each event from the eventList to the eventsToAdd list
                for (int i=0; i<eventList.length(); i++){
                    eventsToAdd.add(eventList.getString(i));
                }
                // Update the events list for the corresponding day in the calendarYear
                this.calendarDays[gregorianCalendar.get(GregorianCalendar.DAY_OF_YEAR ) -1 ].events = eventsToAdd;



            }
        }





    }


    /**
     * Removes all events that are before the current date from the saved JSON file
     * @throws IOException when there is an issue with reading the file
     * @throws ParseException when there is an issue with parsing the date
     */
    public void removeOutdatedEvents() throws IOException, ParseException {

        String filePath = "/savedCalendar.json";
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject objectsToStore = new JSONObject(content);

        Iterator<String> keys = objectsToStore.keys();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        List<String> keysToRemove = new ArrayList<>();

        // iterate over all keys in the JSONObject
        while (keys.hasNext()){
            String key = keys.next();
            // if the date is before the current date, add it to the list of keys to remove
            if (simpleDateFormat.parse(key).before(Calendar.getInstance().getTime())){
                keysToRemove.add(key);

            }


        }
        // remove all the keys from the JSONObject
        for(String s:keysToRemove){
            objectsToStore.remove(s);

        }
        // write the updated JSONObject to the file
        File savedCalendarFile = new File("/Users/rohan/IdeaProjects/Spark_POC/src/savedCalendar.json");
        Writer writer = new FileWriter(savedCalendarFile);
        objectsToStore.write(writer);





}}
