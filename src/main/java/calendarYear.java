


import main.java.calendarDay;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

public class calendarYear {



    public int currentYear = Year.now().getValue();

    calendarDay[] calendarDays;

    public List<calendarDay> getDaysWithEvents() {
        List<calendarDay> daysWithEvents = new ArrayList<>();
        int j=0;
        for (int i=0; i<calendarDays.length; i++){
            if (!(calendarDays[i].getEvents().isEmpty())){
                daysWithEvents.add(j,calendarDays[i]);
                j++;
            }
        }
        return daysWithEvents;

    }  //Returns a List of main.java.calendarDay's that have one or more events


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

    public boolean isValidDate(String date){

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        sdf.setLenient(false);
        try {
            Date dates =sdf.parse(date);
            if (dates.before(new Date())){
                return false;
            }
            else{
                return true;
            }



        } catch (ParseException e) {
            return false;
        }
    }



    public void addEvent(GregorianCalendar eventDay, String eventName) throws IOException {



        storeEvents(eventDay,eventName);

        int dayOfYear = eventDay.get(GregorianCalendar.DAY_OF_YEAR)-1;



            calendarDay calendarDay = calendarDays[dayOfYear];
            calendarDay.month = eventDay.get(GregorianCalendar.MONTH) + 1;
            List<String> events = calendarDay.getEvents();
            events.add(eventName);





    } //helper function that adds Events to the list of events of one particular day



    public void storeEvents(GregorianCalendar calendar, String eventName) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = simpleDateFormat.format(calendar.getTime());

        File savedCalendarFile = ApplicationFilesHandler.savedCalendar;


        String content = ApplicationFilesHandler.getDataFromJSON("calendar").toString();
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

    public void loadEvents() throws IOException, ParseException {



        String content = ApplicationFilesHandler.getDataFromJSON("calendar").toString();




        if (content.isEmpty()){
            return;
        }

        JSONObject objectsToStore = new JSONObject(content);

        Iterator<String> keys = objectsToStore.keys();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");


        while (keys.hasNext()){
            String next = keys.next();
            if(next.contains("2024")){


                JSONArray eventList = objectsToStore.getJSONArray(next);



                Date date = simpleDateFormat.parse(next);
                gregorianCalendar.setTime(date);
                List<String> eventsToAdd  = this.calendarDays[gregorianCalendar.get(GregorianCalendar.DAY_OF_YEAR ) -1 ].events;
                for (int i=0; i<eventList.length(); i++){
                    eventsToAdd.add(eventList.getString(i));
                }
                this.calendarDays[gregorianCalendar.get(GregorianCalendar.DAY_OF_YEAR ) -1 ].events = eventsToAdd;



            }
        }





    }

    public void removeOutdatedEvents() throws IOException, ParseException {


        String content = ApplicationFilesHandler.getDataFromJSON("calendar").toString();

        if (content.isEmpty()){
            return;
        }


        JSONObject objectsToStore = new JSONObject(content);



        Iterator<String> keys = objectsToStore.keys();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        List<String> keysToRemove = new ArrayList<>();

        while (keys.hasNext()){
            String key = keys.next();
            if (simpleDateFormat.parse(key).before(Calendar.getInstance().getTime())){
                keysToRemove.add(key);

            }


        }
        for(String s:keysToRemove){
            objectsToStore.remove(s);

        }
        File savedCalendarFile = new File("/Users/rohan/IdeaProjects/Spark_POC/src/savedCalendar.json");
        Writer writer = new FileWriter(savedCalendarFile);
        objectsToStore.write(writer);




    }





}
