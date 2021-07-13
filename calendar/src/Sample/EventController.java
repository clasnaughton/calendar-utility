package src.Sample;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Controller for Calendar Utility
 * Gets input from view and passes it to model and handles
 * construction of new views
 * Created by Toni Eidmann, Kyra Wilson, and Claudia Naughton on 11/15/17.
 */
public class EventController implements EventControllerInterface {

	EventModelInterface model;
	DailyView view;
	Stage primaryStage;
	MonthlyView view2;
	Calendar today;
	
	public EventController(EventModelInterface model, Stage primaryStage) {
		this.model = model;
		this.primaryStage = primaryStage;
		model.initialize();
		setToday();
	}

    public void setToday(){
        Calendar today = Calendar.getInstance();
	    this.today = today;
    }

    public Calendar getToday(){
        return today;
    }

	public void addEventTitle(String title) {
		model.setEventTitle(title);
	}
	
	public void addEventDescription(String descr) {
		model.setEventDescr(descr);
	}
	
	public void addEventDate(LocalDate date) {
        model.setEventDate(date);
	}
	
	public void addEventHour(String hour) {
        model.setEventHour(hour);
	}
	
	public void addEventMin(String minute) {
        model.setEventMin(minute);
	}

	public void addEventMinEnd(String minute) {
        model.setEventMinEnd(minute);
	}

	public void addEventHourEnd(String hour) {
        model.setEventHourEnd(hour);
	}

	public void addEventAMPM(String AMPM) {
		model.setEventAMPM(AMPM);
	}

	public void addEventAMPMEnd(String AMPM) {
		model.setEventAMPMEnd(AMPM);
	}

    public void addColor(String color){
        model.setEventColor(color);
    }

	public void saveToDo(String toDoString){
    	model.saveToDo(toDoString);
	}

    public void addInputYear(int year) {
        model.setYear(year);
    }

    public void addInputMonth(int month) {
        model.setMonth(month);
    }

    public ArrayList getToDoList(){
        return model.getToDoList();
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

	public void save(DailyObserver o) {
	    model.saveEvent();
		model.notifyObservers(o);
    }

    /**
     * Switches calendar to the selected day.
     * @param  LocalDate date user wants to view
     * @return Scene daily view of selected date
     */
	public Scene switchDailyView(LocalDate clickedDate){
		view = new DailyView(this, model, clickedDate);
    	Pane root = new Pane();
    	root.getChildren().add(view.asView());
    	return new Scene(root, 900, 750);
	}

    /**
     * Switches calendar to the current month.
     * @param  none
     * @return Scene monthly view of current month
     */
	public Scene switchMonthlyView(){
		view2 = new MonthlyView(this, model);
		Pane root = new Pane();
		root.getChildren().add(view2.createMonthlyView());
		Scene monthlyScene = new Scene(root, 900, 750);
		monthlyScene.getStylesheets().add("src/Calendar_stylesheet.css");
		return monthlyScene;
	}

    /**
     * Switches calendar to the selected month and year
     * @param  none
     * @return Scene daily view of selected month and year
     */
	public Scene NewMonthlyView(){
		view2 = new MonthlyView(this, model);
		Pane root = new Pane();
		root.getChildren().add(view2.createNewMonthlyView());
		Scene monthlyScene = new Scene(root, 900, 750);
		monthlyScene.getStylesheets().add("src/Calendar_stylesheet.css");
		return monthlyScene;
	}

}
