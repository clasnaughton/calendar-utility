package src.Sample;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.HashMap;

/**
 * Model for Calendar Utility
 * Handles logic of saving and deleting events and To Do's
 * Created by Toni Eidmann, Kyra Wilson, and Claudia Naughton on 11/15/17.
 */
public class EventModel implements EventModelInterface {
    ArrayList observersList = new ArrayList();
    String title;
    String descr;
    String hour;
    String minute;
    String AMPM;
    String AMPMEnd;
    String hourEnd;
    String minuteEnd;
    String color;
    String toDo;
    String year;
    String month;
    String day;
    LocalDate date;
    ArrayList toDoList;

    public void initialize(){
    }

    public void setEventTitle(String title){
        this.title = title;
    }

    public void setYear(int yearInt) {
        String year = String.valueOf(yearInt);
        this.year = year;
    }

    public void setMonth(int monthInt) {
        String month = String.valueOf(monthInt);
        this.month = month;
    }

    public int getYear() {
        int yearInt = Integer.valueOf(year);
        return yearInt;
    }

    public int getMonth() {
        int monthInt = Integer.valueOf(month) ;
        return monthInt;
    }

    public String getEventTitle(){
        return  title;
    }

    public void setEventDescr(String descr) {
        this.descr = descr;
    }

    public String getEventDescr() {
        return descr;
    }

    public void setToDo(String toDo) {
        this.toDo = toDo;
    }

    public void setEventDate(LocalDate date) {
        this.date = date;
        this.year = String.valueOf(date.getYear());
        this.month = String.valueOf(date.getMonthValue());
        this.day = String.valueOf(date.getDayOfMonth());
    }

    public LocalDate getEventDate() {
        return date;
    }

    public void setEventHour(String hour) {
        this.hour = hour;
    }

    public String getEventHour() {
        return hour;
    }

    public void setEventMin(String minute) {
        this.minute = minute;
    }

    public String getEventMin() {
        return minute;
    }

    public void setEventAMPM(String AMPM) {
        this.AMPM = AMPM;
    }

    public String getEventAMPM() {
        return AMPM;
    }

    public void setEventAMPMEnd(String AMPM) {
        this.AMPMEnd = AMPM;
    }

    public String getEventAMPMEnd() {
        return AMPMEnd;
    }

    public void setEventHourEnd(String hourEnd) {
        this.hourEnd = hourEnd;
    }

    public String getEventHourEnd() {
        return hourEnd;
    }

    public void setEventMinEnd(String minuteEnd) {
        this.minuteEnd = minuteEnd;
    }

    public String getEventMinEnd() {
        return minuteEnd;
    }

    public void setEventColor(String color) {
        this.color = color;
    }

    public String getEventColor() {
        return color;
    }

    public void registerObserver(DailyObserver o) {
        observersList.add(o);
    }

    public void removeObserver(DailyObserver o) {
        int i = observersList.indexOf(o);
        if (i >= 0) {
            observersList.remove(i);
        }
    }

    public void notifyObservers(DailyObserver observer) {
            observer.updateEvent();
        }

    /**
     * This method opens the serialized file of events.
     * @param  none
     * @return HashMap map of events
     */
    public HashMap getEvents() {

        HashMap eventMap = new HashMap();

        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream("eventHashMap.ser");
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            eventMap = (HashMap) in.readObject();

            in.close();
            file.close();

        } catch (IOException | ClassNotFoundException ex) {
        }

        return eventMap;
    }

    /**
     * This method is called once an event has been created or edited. It reads event
     * information from file, creates a HashMap, and adds events based on
     * whether there are other events present on the same day.
     * @param  none
     * @return void
     */
    public void saveEvent(){

        //Put event information into a dictionary within an ArrayList
        ArrayList eventList = new ArrayList();

        HashMap<String, String> singleEventDict = new HashMap<>();
        singleEventDict.put("title", getEventTitle());
        singleEventDict.put("description", getEventDescr());
        singleEventDict.put("AMPM", getEventAMPM());
        singleEventDict.put("endAMPM", getEventAMPMEnd());
        singleEventDict.put("color", getEventColor());
        singleEventDict.put("hourStart", getEventHour());
        singleEventDict.put("hourEnd", getEventHourEnd());
        singleEventDict.put("minute", getEventMin());
        singleEventDict.put("minuteEnd", getEventMinEnd());

        eventList.add(singleEventDict);

        //Get other events in calendar
        HashMap<String, HashMap> yearMap = getEvents();
        HashMap<String, HashMap> monthMap = new HashMap<>();
        HashMap<String, ArrayList> dayMap = new HashMap<>();

        //check if the year is already in the hashmap
        if (yearMap.get(year) == null) {
            yearMap.put(year, monthMap);
        }
        monthMap = yearMap.get(year);

        //check if the month is already in the Hashmap
        if (monthMap.get(month) == null){
            monthMap.put(month, dayMap);
        }
        dayMap = monthMap.get(month);

        //check if day is hashMap
        if (dayMap.get(day) == null){
            dayMap.put(day, eventList);
        }
        else{
            ArrayList tempEventList = new ArrayList();
            tempEventList = dayMap.get(day);
            tempEventList.add(singleEventDict);
            dayMap.put(day, tempEventList);
        }
        writeEventFile(yearMap);
    }

    /**
     * This method deletes an event from the serialized file. If it is the
     * only event on a day, it deletes the date key in the HashMap too.
     * @param  String title of event to be deleted
     * @param  LocalDate the date of the event to be deleted
     * @return void
     */
    public void deleteEvent(String title, LocalDate clickedDate){
        HashMap eventMap = getEvents();

        HashMap<String, HashMap> outerMap = (HashMap<String, HashMap>) eventMap.get(String.valueOf(clickedDate.getYear()));
        HashMap<String, HashMap> monthMap = (HashMap<String, HashMap>) outerMap.get(String.valueOf(clickedDate.getMonthValue()));

        Object tempDayList = monthMap.get(String.valueOf(clickedDate.getDayOfMonth()));
        ArrayList dayList = (ArrayList) tempDayList;

        if (dayList.size() == 1) {
            monthMap.remove(String.valueOf(clickedDate.getDayOfMonth()));
        }
        else {
            for (int i = 0; i < dayList.size(); i++){
                HashMap current = (HashMap) dayList.get(i);
                if (current.get("title").equals(title)) {
                    dayList.remove(i);
                }
                else {
                    i++;
                }
            }
        }
        writeEventFile(eventMap);
    }

    /**
     * This method writes the events in the calendar into a
     * serialized file.
     * @param  HashMap map of events in calendar
     * @return void
     */
    public void writeEventFile(HashMap eventMap){
        try
        {
            FileOutputStream fos =
                    new FileOutputStream("eventHashMap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(eventMap);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * This method reads items from a serializeable file and
     * adds them to an Array List.
     * @param  none
     * @return ArrayList list of To Do items
     */
    public ArrayList getToDoList(){
        ArrayList toDo = new ArrayList();
        try {
            FileInputStream file = new FileInputStream("toDoList.ser");
            ObjectInputStream in = new ObjectInputStream(file);
            toDo = (ArrayList) in.readObject();
            in.close();
            file.close();
        } catch (IOException | ClassNotFoundException ex) {
        }
        return toDo;
    }

    /**
     * This method adds a new item to the To Do list.
     * @param  none
     * @return none
     */
    public void saveToDo(String toDoString){
        toDoList = getToDoList();
        toDoList.add(toDoString);
        writeToDoFile(toDoList);
    }

    /**
     * This method removes an item from the To Do list.
     * @param  none
     * @return none
     */
    public void removeToDo(String toDo){
        ArrayList toDoList = getToDoList();
        toDoList.remove(toDo);
        writeToDoFile(toDoList);
    }

    /**
     * This method adds new To Do items to the serialized file.
     * @param  ArrayList list of To Do items
     * @return none
     */
    public void writeToDoFile(ArrayList toDoList){
        try
        {
            FileOutputStream fos =
                    new FileOutputStream("toDoList.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(toDoList);
            oos.close();
            fos.close();
        }catch(IOException ioe) {
        }
    }

}
