package src.Sample;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Calendar;
import java.time.YearMonth;

/**
 * The file that models interface for the monthly view of a calendar.
 * Created by Claudia Naughton, Toni Eidmann, and Kyra Wilson on 11/10/17.
 */
public class MonthlyView implements DailyObserver {
	EventModelInterface model;
	EventControllerInterface controller;
	int year1;
	String firstDay;
	int daysInMonth;
	int blankBoxes;
	int month;
	int year;
	Calendar cal = Calendar.getInstance();
	HashMap events;
	double eventNum;
	String viewableTitle;
	int monthValue;

	//Constructor that initializes controller and model
	public MonthlyView(EventControllerInterface controller, EventModelInterface model) {
		this.controller = controller;
		this.model = model;
		model.registerObserver((DailyObserver)this);
	}

	//Creates the main BorderPane level of organization and opens scene
	public BorderPane createMonthlyView() {
		BorderPane view = new BorderPane();
		view.setPrefSize(900, 750);
		view.setCenter(addCalendarGrid());
		view.setTop(addMenuBar());
		return view;
	}

	//Creates the main BorderPane level of organization and opens scene with selected month and year
	public BorderPane createNewMonthlyView() {
		BorderPane view = new BorderPane();
		view.setPrefSize(900, 750);
		view.setCenter(addNewCalendarGrid());
		view.setTop(addNewMenuBar());
		return view;
	}

	//Creates the monthly calendar portion of scene
	public GridPane addCalendarGrid() {
		GridPane grid = new GridPane();
		grid.add(addDayLabels(), 7, 1);
		grid.add(currentDayBoxes(), 7, 2);
		grid.setAlignment(Pos.CENTER);
		return grid;
	}

	//Creates the monthly calendar portion of scene with the newly selected month and year
	public GridPane addNewCalendarGrid() {
		GridPane grid = new GridPane();
		grid.add(addDayLabels(), 7, 1);
		int year = model.getYear();
		int month = model.getMonth();
		grid.add(changeCalBoxes(year, month), 7, 2);
		grid.setAlignment(Pos.CENTER);
		return grid;
	}

	//Creates the menu bar at the top of the calendar for navigation, adding events, and switching views
	public StackPane addMenuBar() {
		StackPane toolbar = new StackPane();
		toolbar.setPadding(new Insets(15, 12, 15, 12));
		Button addEventBtn = new Button();
		addEventBtn.setText("Add Event");
		//opens the Add Event window when clicked
		addEventBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				AddEvent newEvent = new AddEvent(controller, model, (DailyObserver)MonthlyView.this);
				Stage newStage = new Stage();
				newEvent.start(newStage);
			}
		});
		Button dailyViewBtn = new Button();
		dailyViewBtn.setText("Daily View");
		//opens the Daily View window and closes monthly view
		dailyViewBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
                LocalDate clickedDate = LocalDate.now();
                Scene dailyScene = controller.switchDailyView(clickedDate);
				dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
				controller.getPrimaryStage().setScene(dailyScene);
				controller.getPrimaryStage().setTitle("Daily Calendar");
			}
		});
		HBox monthNav = addMonthNav();
		toolbar.getChildren().addAll(addEventBtn, monthNav, dailyViewBtn);
		toolbar.setAlignment(addEventBtn, Pos.TOP_RIGHT);
		toolbar.setAlignment(dailyViewBtn, Pos.TOP_LEFT);
		toolbar.getStyleClass().add("headerText");
		toolbar.setMaxWidth(900);
		return toolbar;
	}

	//Creates the menu bar at the top of the calendar for navigation, adding events, and switching views
	public StackPane addNewMenuBar() {
		StackPane toolbar = new StackPane();
		toolbar.setPadding(new Insets(15, 12, 15, 12));
		Button addEventBtn = new Button();
		addEventBtn.setText("Add Event");
		//opens the Add Event window when clicked
		addEventBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				AddEvent newEvent = new AddEvent(controller, model, MonthlyView.this);
				Stage newStage = new Stage();
				newEvent.start(newStage);
			}
		});
		Button dailyViewBtn = new Button();
		dailyViewBtn.setText("Daily View");
		//opens the Daily View window and closes monthly view
		dailyViewBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
                LocalDate clickedDate = LocalDate.now();
                Scene dailyScene = controller.switchDailyView(clickedDate);
				dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
				controller.getPrimaryStage().setScene(dailyScene);
				controller.getPrimaryStage().setTitle("Daily Calendar");
			}
		});
		HBox monthNav = addNewMonthNav();
		toolbar.getChildren().addAll(addEventBtn, monthNav, dailyViewBtn);
		toolbar.setAlignment(addEventBtn, Pos.TOP_RIGHT);
		toolbar.setAlignment(dailyViewBtn, Pos.TOP_LEFT);
		toolbar.getStyleClass().add("headerText");
		toolbar.setMaxWidth(900);
		return toolbar;
	}

	//Helper method for addMenuBar()
	//Creates buttons to allow for navigation between different months
	private HBox addMonthNav() {
		HBox monthNavBox = new HBox();
		monthNavBox.setSpacing(10);
		ChoiceBox monthChoices = new ChoiceBox(FXCollections.observableArrayList(
				"January", "February", "March", "April", "May", "June", "July",
				"August", "September", "October", "November", "December"));
		//user clicks on certain month
		Button submit = new Button();
		submit.setText("Go");
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				List<javafx.scene.Node> inputList = monthNavBox.getChildren();
				ChoiceBox yearInput = (ChoiceBox) inputList.get(0);
				ChoiceBox monthInput = (ChoiceBox) inputList.get(1);
				Object year = yearInput.getValue();
				Object month = monthInput.getValue();
				String yearString = (String) year;
				int yearInt = Integer.parseInt(yearString);
				controller.addInputYear(yearInt);
				String monthString = (String) month;
				String monthStr = monthString.toUpperCase();
				int monthInt = getMonthVal(monthStr);
				setMonth(monthInt);
				setYear(yearInt);
				controller.addInputMonth(monthInt);
				controller.addInputYear(yearInt);
                Scene monthlyScene = controller.NewMonthlyView();
                monthlyScene.getStylesheets().add("src/Calendar_stylesheet.css");
                controller.getPrimaryStage().setScene(monthlyScene);
                controller.getPrimaryStage().setTitle("Monthly Calendar");
			}
		});
		List<String> monthList = monthChoices.getItems();
		ChoiceBox yearChoices = new ChoiceBox(FXCollections.observableArrayList("2017", "2018", "2019", "2020"));
        int tempInt = cal.get(Calendar.MONTH);
        int currentMonth;
		int currentYear;
		if (tempInt == 0){
			currentMonth = 12;
			currentYear = cal.get(Calendar.YEAR) - 1;
		}
		else {
			currentMonth = cal.get(Calendar.MONTH);
			currentYear = cal.get(Calendar.YEAR);
		}
		monthChoices.setValue(monthList.get(currentMonth));
		yearChoices.setValue(Integer.toString(currentYear));
		//gets the year the user selected
		yearChoices.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				year1 = getYear(yearChoices);
			}
		});
		//gets the month the user selected
		monthChoices.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
			}
		});
		monthNavBox.getChildren().addAll(yearChoices, monthChoices, submit);
		monthNavBox.setAlignment(Pos.TOP_CENTER);
		monthNavBox.setMaxWidth(400);
		return monthNavBox;
	}

	//Helper method for addNewMenuBar()
	//Creates buttons to allow for navigation between different months
	private HBox addNewMonthNav() {
		HBox monthNavBox = new HBox();
		monthNavBox.setSpacing(10);
		ChoiceBox monthChoices = new ChoiceBox(FXCollections.observableArrayList(
				"January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December"));
		//user clicks on certain month
		Button submit = new Button();
		submit.setText("Go");
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				List<javafx.scene.Node> inputList = monthNavBox.getChildren();
				ChoiceBox yearInput = (ChoiceBox) inputList.get(0);
				ChoiceBox monthInput = (ChoiceBox) inputList.get(1);
				Object year = yearInput.getValue();
				Object month = monthInput.getValue();
				String yearString = (String) year;
				int yearInt = Integer.parseInt(yearString);
				controller.addInputYear(yearInt);
				String monthString = (String) month;
				String monthStr = monthString.toUpperCase();
				int monthInt = getMonthVal(monthStr);
				setMonth(monthInt);
				setYear(yearInt);
				controller.addInputMonth(monthInt);
				controller.addInputYear(yearInt);
				Scene monthlyScene = controller.NewMonthlyView();
				monthlyScene.getStylesheets().add("src/Calendar_stylesheet.css");
				controller.getPrimaryStage().setScene(monthlyScene);
				controller.getPrimaryStage().setTitle("Monthly Calendar");
			}
		});
		List<String> monthList = monthChoices.getItems();
		ChoiceBox yearChoices = new ChoiceBox(FXCollections.observableArrayList("2017", "2018", "2019", "2020"));
		int tempInt = cal.get(Calendar.MONTH);
		int currentMonth;
		int currentYear;
		if (tempInt == 0){
			currentMonth = 12;
			currentYear = cal.get(Calendar.YEAR) - 1;
		}
		else{
			currentMonth = cal.get(Calendar.MONTH);
			currentYear = cal.get(Calendar.YEAR);
		}
		monthChoices.setValue(monthList.get(currentMonth - 1));
		yearChoices.setValue(Integer.toString(currentYear));
		//gets user input on changing year
		yearChoices.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				year1 = getYear(yearChoices);
			}
		});
		//gets user input on changing months
		monthChoices.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
			}
		});
		monthNavBox.getChildren().addAll(yearChoices, monthChoices, submit);
		monthNavBox.setAlignment(Pos.TOP_CENTER);
		monthNavBox.setMaxWidth(400);
		return monthNavBox;
	}

	//Setter method that returns the month
	private void setMonth(int month) {
		this.month = month;
	}

	//Setter method that returns the year
	private void setYear(int year) {
		this.year = year;
	}

	//Helper method that gets the value of a month from 1-12
	private int getMonthVal(String monthStr) {
		int monthNum;
		if (monthStr.equals("JANUARY")) {
			monthNum = 1;
		} else if (monthStr.equals("FEBRUARY")) {
			monthNum = 2;
		} else if (monthStr.equals("MARCH")) {
			monthNum = 3;
		} else if (monthStr.equals("APRIL")) {
			monthNum = 4;
		} else if (monthStr.equals("MAY")) {
			monthNum = 5;
		} else if (monthStr.equals("JUNE")) {
			monthNum = 6;
		} else if (monthStr.equals("JULY")) {
			monthNum = 7;
		} else if (monthStr.equals("AUGUST")) {
			monthNum = 8;
		} else if (monthStr.equals("SEPTEMBER")) {
			monthNum = 9;
		} else if (monthStr.equals("OCTOBER")) {
			monthNum = 10;
		} else if (monthStr.equals("NOVEMBER")) {
			monthNum = 11;
		} else {
			monthNum = 12;
		}
		return monthNum;
	}

	//Creates the boxes for the calendar given changed month and year
	private TilePane changeCalBoxes(int year, int month) {
		//sets the new calendar
		cal.set(year, month, 1);
        YearMonth ym = YearMonth.of(year, month);
        //gets days in given month and day of week of first day
		daysInMonth = ym.lengthOfMonth();
		firstDay = ym.atDay(1).getDayOfWeek().name();
		TilePane dayBoxesChanged = new TilePane();
		blankBoxes = numBoxes(firstDay);
		int boxNum;
		if ((daysInMonth + blankBoxes) > 35){
			boxNum = 42;
		}
		else{
			boxNum = 35;
		}
		List<Button> dayBoxBtns = new ArrayList<Button>();
		for (int j = 1; j <= blankBoxes; j++)
			dayBoxBtns.add(new Button());
		//adds beginning boxes
		for (int k = 1; k <= boxNum; k++) {
			for (int i = 1; i <=daysInMonth; i++) {
				Button dayButton = new Button("" + i);
				dayButton.setStyle("-fx-alignment: top-right;");
				dayBoxBtns.add(dayButton);
			}
			Button tempBoxBtn = dayBoxBtns.get(k - 1);
			tempBoxBtn.getStyleClass().add("specialButton");
			tempBoxBtn.setMinWidth(100);
			tempBoxBtn.setMinHeight(100);
			int current = k;
			//goes to Daily View of button when a day in calendar is selected in the Monthly View
			tempBoxBtn.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					String dayNum = tempBoxBtn.getText();
					if (!dayNum.equals("") && current < 28) {
						LocalDate clickedDate = LocalDate.of(year, month, Integer.valueOf(dayNum));
						Scene dailyScene = controller.switchDailyView(clickedDate);
						dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
						controller.getPrimaryStage().setScene(dailyScene);
						controller.getPrimaryStage().setTitle("Daily Calendar");
					} else if (current >= 28) {
						if (!dayNum.equals("1") && !dayNum.equals("2") && !dayNum.equals("3") &&
								!dayNum.equals("4") && !dayNum.equals("5") && !dayNum.equals("6") &&
								!dayNum.equals("7")) {
							LocalDate clickedDate = LocalDate.of(year, month, Integer.valueOf(dayNum));
							Scene dailyScene = controller.switchDailyView(clickedDate);
							dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
							controller.getPrimaryStage().setScene(dailyScene);
							controller.getPrimaryStage().setTitle("Daily Calendar");
						}
					}
				}
			});
			VBox outerBox = new VBox();
            HBox spacingBox = new HBox();
            spacingBox.setMinHeight(30);
            VBox boxOfEvents = addDailyEvents(k - blankBoxes, year, month);
            boxOfEvents.setPadding(new Insets(0, 2, 0, 2));
            outerBox.getChildren().addAll(spacingBox, boxOfEvents);
            StackPane dayBox = new StackPane();
            dayBox.getChildren().addAll(outerBox, tempBoxBtn);
            dayBoxesChanged.getChildren().add(dayBox);
            tempBoxBtn.setAlignment(Pos.TOP_RIGHT);
		}
		dayBoxesChanged.setMinWidth(700);
		dayBoxesChanged.setMaxWidth(700);
		dayBoxesChanged.setPrefWidth(700);
		dayBoxesChanged.setTileAlignment(Pos.CENTER);
        dayBoxesChanged.getStyleClass().add("gridBox");
		return dayBoxesChanged;
	}

	//Helper method that returns the number of blank boxes that need to go at the beginning of the calendar
	private int numBoxes(String firstDay) {
		if (firstDay.equals("SUNDAY")) {
			blankBoxes = 0;
		} else if (firstDay.equals("MONDAY")) {
			blankBoxes = 1;
		} else if (firstDay.equals("TUESDAY")) {
			blankBoxes = 2;
		} else if (firstDay.equals("WEDNESDAY")) {
			blankBoxes = 3;
		} else if (firstDay.equals("THURSDAY")) {
			blankBoxes = 4;
		} else if (firstDay.equals("FRIDAY")) {
			blankBoxes = 5;
		} else {
			blankBoxes = 6;
		}
		return blankBoxes;
	}

	//Helper method for addCalendarGrid()
	//Creates the day labels that go in the calendar grid
	private HBox addDayLabels() {
		HBox daysBox = new HBox();
		daysBox.setSpacing(28);
		Text sunday = new Text();
		sunday.setText("Sunday");
		Text monday = new Text();
		monday.setText("Monday");
		Text tuesday = new Text();
		tuesday.setText("Tuesday");
		Text wednesday = new Text();
		wednesday.setText("Wednesday");
		Text thursday = new Text();
		thursday.setText("Thursday");
		Text friday = new Text();
		friday.setText("Friday");
		Text saturday = new Text();
		saturday.setText("Saturday");
		daysBox.getChildren().addAll(sunday, monday, tuesday, wednesday, thursday, friday, saturday);
		daysBox.getStyleClass().add("DayText");
		return daysBox;
	}

	//Helper method for addCalendarGrid()
	//Creates the boxes for each of the days of the month
	private TilePane currentDayBoxes() {
		TilePane dayBoxes = new TilePane();
		YearMonth ym = YearMonth.of(cal.get(Calendar.YEAR), cal.getMaximum(Calendar.MONTH));
		month = cal.get(Calendar.MONTH) + 1;
		year = cal.get(Calendar.YEAR);
		//gets number of days in a month for a given year and the first day of the month
		daysInMonth = ym.lengthOfMonth();
		firstDay = ym.atDay(1).getDayOfWeek().name();
		blankBoxes = numBoxes(firstDay);
		int boxNum;
		if ((daysInMonth + blankBoxes) > 35){
			boxNum = 42;
		}
		else{
			boxNum = 35;
		}
		List<Button> dayBoxBtns = new ArrayList<Button>();
		for (int j = 1; j <= blankBoxes; j++)
			dayBoxBtns.add(new Button());
		//adds beginning boxes
		for (int k = 1; k <= boxNum; k++) {
			for (int i = 1; i <=daysInMonth; i++) {
				Button dayButton = new Button("" + i);
                dayButton.setStyle("-fx-alignment: top-right;");
				dayBoxBtns.add(dayButton);
			}
			Button tempBoxBtn = dayBoxBtns.get(k - 1);
			tempBoxBtn.getStyleClass().add("specialButton");
			tempBoxBtn.setMinWidth(100);
			tempBoxBtn.setMinHeight(100);
			int current = k;
			//when a certain day is clicked goes to the daily view of that day
			tempBoxBtn.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					String dayNum = tempBoxBtn.getText();
					if (!dayNum.equals("") && current < 28) {
                        LocalDate clickedDate = LocalDate.of(year, month, Integer.valueOf(dayNum));
                        Scene dailyScene = controller.switchDailyView(clickedDate);
						dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
						controller.getPrimaryStage().setScene(dailyScene);
						controller.getPrimaryStage().setTitle("Daily Calendar");
					} else if (current >= 28) {
						if (!dayNum.equals("1") && !dayNum.equals("2") && !dayNum.equals("3") &&
								!dayNum.equals("4") && !dayNum.equals("5") && !dayNum.equals("6") &&
								!dayNum.equals("7")) {
							LocalDate clickedDate = LocalDate.of(year, month, Integer.valueOf(dayNum));
							Scene dailyScene = controller.switchDailyView(clickedDate);
							dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
							controller.getPrimaryStage().setScene(dailyScene);
							controller.getPrimaryStage().setTitle("Daily Calendar");
						}
					}
				}
			});
			VBox outerBox = new VBox();
			HBox spacingBox = new HBox();
			spacingBox.setMinHeight(30);
			VBox boxOfEvents = addDailyEvents(k - blankBoxes, year, month);
			boxOfEvents.setPadding(new Insets(0, 2, 0, 2));
			outerBox.getChildren().addAll(spacingBox, boxOfEvents);
			StackPane dayBox = new StackPane();
			dayBox.getChildren().add(outerBox);
			dayBox.getChildren().add(tempBoxBtn);
			dayBoxes.getChildren().add(dayBox);
			tempBoxBtn.setAlignment(Pos.TOP_RIGHT);
		}
		dayBoxes.setMinWidth(700);
		dayBoxes.setMaxWidth(700);
		dayBoxes.setPrefWidth(700);
		dayBoxes.setTileAlignment(Pos.CENTER);
		dayBoxes.getStyleClass().add("gridBox");
		return dayBoxes;
	}

	@Override
	public void updateEvent() {
	    BorderPane newMonthlyScene = createNewMonthlyView();
        controller.getPrimaryStage().getScene().setRoot(newMonthlyScene);
	}

	//getter method for a selected year from the ChoiceBox
	private int getYear(ChoiceBox yearChoices) {
		int year;
		Object aYear = yearChoices.getValue();
		if (aYear.equals("2017")) {
			year = 2017;
		} else if (aYear.equals("2018")) {
			year = 2018;
		} else if (aYear.equals("2019")) {
			year = 2019;
		} else {
			year = 2020;
		}
		return year;
	}

	//Adds events to the monthly calendar
	public VBox addDailyEvents(int day, int year, int month) {
		VBox eventHolder = new VBox();
		EVENTS:
		{
			ArrayList todayEvents;
			events = model.getEvents();
			//get the current date as Strings
			String dayOfMonthStr = String.valueOf(day);
			monthValue = month;
			String monthStr = String.valueOf(this.monthValue);
			String yearStr = String.valueOf(year);
			try {
				HashMap yearEvents = (HashMap) events.get(yearStr);
				HashMap monthEvents = (HashMap) yearEvents.get(monthStr);
				todayEvents = (ArrayList) monthEvents.get(dayOfMonthStr);
				eventNum = todayEvents.size();
			} catch (NullPointerException e) {
				break EVENTS;
			}
			if (eventNum <= 4) {
				for (int i = 0; i < todayEvents.size(); i++) {
					HashMap current = (HashMap) todayEvents.get(i);
					String title = (String) current.get("title");
					//if title is longer than 15 characters, shows "..."
					if (title.length() > 15) {
						viewableTitle = title.substring(0, 12) + "...";
					}
					else {
						viewableTitle = title;
					}
					String color = (String) current.get("color");
					HBox newEventBox = new HBox();
					Text newEvent = new Text();
					newEvent.setText(viewableTitle);
					newEventBox.getChildren().addAll(newEvent);
					newEventBox.getStyleClass().add("eventBox");
					newEventBox.setStyle("-fx-background-color: " + color + ";");
					eventHolder.getChildren().add(newEventBox);
				}
			}
			else {
				for (int i = 0; i < 4; i++) {
					HashMap current = (HashMap) todayEvents.get(i);
					String title = (String) current.get("title");
					if (title.length() > 15) {
						viewableTitle = title.substring(0, 12) + "...";
					}
					else {
						viewableTitle = title;
					}
					String color = (String) current.get("color");
					HBox newEventBox = new HBox();
					Text newEvent = new Text();
					newEvent.setText(viewableTitle);
					newEventBox.getChildren().addAll(newEvent);
					newEventBox.getStyleClass().add("eventBox");
					newEventBox.setStyle("-fx-background-color: " + color + ";");
					eventHolder.getChildren().add(newEventBox);
				}
			}
		}
		return eventHolder;
	}
}
