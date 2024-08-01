import java.time.Month;
import java.time.MonthDay;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class calendarDay {



    int month;

    int day;

    List<String> events;

    int dayOfYear;


    public calendarDay(Calendar date, List<String> events){
        this.month = date.get(GregorianCalendar.MONTH)+1;
        this.day = date.get(GregorianCalendar.DAY_OF_MONTH);
        this.events = events;
        this.dayOfYear = date.get(GregorianCalendar.DAY_OF_YEAR);

    }

    public List<String> getEvents() {
        return events;
    }


}
