package src.Sample;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public interface EventModelInterface {
	void initialize();
  
    void setEventTitle(String title);
  
	String getEventTitle();
	
	void setEventDescr(String descr);
	
	String getEventDescr();
	
	void setEventDate(LocalDate date);
	
	LocalDate getEventDate();
	
	void setEventHour(String hour);

	void setYear(int year);

	void setMonth(int month);

	int getYear();

	int getMonth();
	
	String getEventHour();
	
	void setEventMin(String minute);
	
	String getEventMin();

	void setToDo(String toDo);

	void registerObserver(DailyObserver o);
  
	void removeObserver(DailyObserver o);
	
	void notifyObservers(DailyObserver o);

	void setEventAMPM(String AMPM);

	String getEventAMPM();

	void setEventAMPMEnd(String AMPM);

	String getEventAMPMEnd();

	void setEventHourEnd(String hourEnd);

	String getEventHourEnd();

	void setEventMinEnd(String minuteEnd);

	String getEventMinEnd();

	void setEventColor(String color);

	String getEventColor();

	void saveEvent();

	void saveToDo(String todo);

	ArrayList getToDoList();

	HashMap getEvents();

	void removeToDo(String toDo);

    void writeToDoFile(ArrayList toDoList);

    void deleteEvent(String title, LocalDate clickedDate);

}
