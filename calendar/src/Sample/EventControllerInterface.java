package src.Sample;

import javafx.stage.Stage;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public interface EventControllerInterface {

	void setToday();

	Calendar getToday();

	void addEventTitle(String title);
	
	void addEventDescription(String descr); 
	
	void addEventDate(LocalDate date);
	
	void addEventHour(String Hour);
	
	void addEventMin(String Minute);

	void save(DailyObserver observer);

	void addEventAMPM(String AMPM);

	void addEventAMPMEnd(String AMPM);

	void addEventMinEnd(String minute);

	void addEventHourEnd(String hour);

	void addColor(String Color);

	void saveToDo(String toDo);

	ArrayList getToDoList();

	Scene switchDailyView(LocalDate clickedDate);

    Scene switchMonthlyView();

	Scene NewMonthlyView();

    Stage getPrimaryStage();

	void addInputYear(int year);

	void addInputMonth(int month);
	
}