package src.Sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The Daily View UI for the Calendar Utility.
 * Created by Toni Eidmann, Kyra Wilson, and Claudia Naughton on 11/09/17.
 */
public class DailyView implements src.Sample.DailyObserver {
    src.Sample.EventModelInterface model;
    src.Sample.EventControllerInterface controller;
    VBox schedule;
    Button event1;
    Stage primaryStage;
    BorderPane view;
    HashMap events;
    double eventNum;
    String viewableTitle;
    String viewableDescription;
    LocalDate clickedDate;

    public DailyView(src.Sample.EventControllerInterface controller, src.Sample.EventModelInterface model,
                     LocalDate clickedDate) {
        this.controller = controller;
        this.model = model;
        this.clickedDate = clickedDate;
        model.setEventDate(clickedDate);
        model.registerObserver((src.Sample.DailyObserver)this);
        view = createDailyView();
    }

    public Parent asView() {
        return view;
    }


    /**Creates a Window as a BorderPane and divides it into Center, Top, Right, Bottom sections */
    public BorderPane createDailyView() {
        BorderPane view = new BorderPane();
        view.setMinSize(900, 750);
        view.setPadding(new Insets(20));

        eventNum = 0;
        view.setCenter(addDailyEvents());
        view.setTop(addTop());
        view.setRight(addToDo());
        view.setBottom(addBottomBorderPane());
        view.setMargin(addToDo(), new Insets(0, 15, 0, 30));

        return view;
    }

    /** Creates a BorderPane that gets added to the bottom of
     * the main window displaying yesterday, today, and tomorrow. */
    public BorderPane addBottomBorderPane() {
        BorderPane bottomPane = new BorderPane();

        bottomPane.setLeft(addYesterday());
        bottomPane.setMinHeight(100);
        bottomPane.setRight(addTomorrow());

        Text today = new Text();
        today.setText(String.valueOf(clickedDate.getDayOfWeek()));
        today.getStyleClass().add("DailyHeader");
        bottomPane.setCenter(today);
        bottomPane.getStyleClass().add("borderPane");

        return bottomPane;
    }

    /** Creates a StackPane at the top of the window displaying
     * main navigation buttons and displays day text. */
    public StackPane addTop() {
        StackPane nav = new StackPane();
        nav.setPadding(new Insets(15, 12, 15, 12));

        event1 = eventButton();

        Button monthly =  switchToMonthlyButton();

        HBox mainHead = new HBox();
        mainHead.setSpacing(10);
        Text header = new Text();
        header.setText(String.valueOf(clickedDate.getMonth() + " " +
                String.valueOf(clickedDate.getDayOfMonth()) + ", "
                + String.valueOf(clickedDate.getYear())));
        header.getStyleClass().add("DailyHeader");
        mainHead.getChildren().addAll(header);

        nav.getChildren().addAll(event1, mainHead, monthly);
        nav.setAlignment(event1, Pos.TOP_RIGHT);
        mainHead.setAlignment(Pos.TOP_CENTER);
        nav.setAlignment(monthly, Pos.TOP_LEFT);
        nav.getStyleClass().add("headerText");
        mainHead.setMaxWidth(500);

        return nav;
    }


    private Button eventButton(){
        Button addEventButton = new Button();
        addEventButton.setText("Add Event");
        addEventButton.setOnAction(new EventHandler<ActionEvent>() {@Override
        public void handle (ActionEvent event){
            boolean eventLimit = false;
            if (eventNum > 14){
                eventLimit = true;
            }
            if(eventLimit){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Can't add event");
                alert.setHeaderText("Event Limit");
                alert.setContentText("There are too many events to add another on this day.");
                alert.showAndWait();
            }

            else {
                AddEvent newEvent = new AddEvent(controller, model, DailyView.this);
                Stage newStage = new Stage();
                newEvent.start(newStage);
            }
        }
        });

        return addEventButton;
    }

    private Button switchToMonthlyButton(){
        Button monthly = new Button();
        monthly.setText("Monthly View");
        monthly.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene monthlyScene = controller.switchMonthlyView();
                monthlyScene.getStylesheets().add("src/Calendar_stylesheet.css");
                controller.getPrimaryStage().setScene(monthlyScene);
                controller.getPrimaryStage().setTitle("Monthly Calendar");
            }
        });
        return monthly;
    }

    /** Creates a VBox as the center part of the daily view
     * calendar and adds all saved events in a vertical sequence. */
    public VBox addDailyEvents() {
        EVENTS: {
        ArrayList todayEvents;
        events = model.getEvents();
        schedule = new VBox();
        schedule.getStyleClass().add("VBox");

        //get the current date as Strings
        int dayOfMonth = clickedDate.getDayOfMonth();
        String dayOfMonthStr = String.valueOf(dayOfMonth);
        int month = clickedDate.getMonthValue();
        String monthStr = String.valueOf(month);
        int year = clickedDate.getYear();
        String yearStr = String.valueOf(year);

        try{
            HashMap yearEvents = (HashMap) events.get(yearStr);
            HashMap monthEvents = (HashMap) yearEvents.get(monthStr);
            todayEvents = (ArrayList) monthEvents.get(dayOfMonthStr);
            eventNum = todayEvents.size();
        }
        catch (NullPointerException e) {
            break EVENTS;
        }

        for (int i = 0; i < todayEvents.size(); i++) {

            HashMap current = (HashMap) todayEvents.get(i);

            //limits viewable title to what can be shown on screen
            String title = (String) current.get("title");
            if (title.length() > 50) {
                viewableTitle = title.substring(0, 50);
            } else {
                viewableTitle = title;
            }

            String description = (String) current.get("description");
            String hour = (String) current.get("hourStart");
            String minute = (String) current.get("minute");
            String AMPM = (String) current.get("AMPM");
            String hourEnd = (String) current.get("hourEnd");
            String minuteEnd = (String) current.get("minuteEnd");
            String AMPMEnd = (String) current.get("endAMPM");
            String color = (String) current.get("color");

            //sets up the text and appearance of the event
            HBox newEventBox = new HBox();
            Text newEvent = new Text();
            newEvent.setText(hour + ":" + minute + AMPM + "-" + hourEnd + ":" + minuteEnd +
                    AMPMEnd + " " + viewableTitle);
            newEventBox.getChildren().add(newEvent);
            newEventBox.getStyleClass().add("eventBox");
            newEventBox.setStyle("-fx-background-color: " + color + ";");

            //creates new StackPane and adds Hbox that contains the event title
            // so that transparent edit button can later be added on top of it
            StackPane buttonEventLayered = new StackPane();
            buttonEventLayered.getChildren().add(newEventBox);

            Button edEvent = new Button();
            edEvent.getStyleClass().add("editEventButton");
            edEvent.setMaxWidth(650);
            edEvent.setMaxHeight(2);

            //when edit event button is clicked, edit Event is called and sends in the event's information
            //as default values
            edEvent.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    src.Sample.EditEvent editEvent = new src.Sample.EditEvent(controller, model,
                            DailyView.this, title, description, hour, minute,
                            AMPM, hourEnd, minuteEnd, AMPMEnd, clickedDate, buttonEventLayered);
                    Stage editStage = new Stage();
                    editEvent.start(editStage);
                }
            });

            if (description.length() > 100) {
                viewableDescription = description.substring(0, 100);
            } else {
                viewableDescription = description;
            }

            //adds the event's description pop up that appears when an event is hovered over
            HBox popupBox = descrBox(viewableDescription);
            popupBox.setMinHeight(30);
            popupBox.setMinWidth(100);
            popupBox.setAlignment(Pos.CENTER);
            popupBox.getStyleClass().add("descrPopup");
            Popup descrPopup = new Popup();
            descrPopup.getContent().add(popupBox);
            descrPopup.setAutoFix(true);

            edEvent.setOnMouseMoved(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent t) {
                    descrPopup.show(controller.getPrimaryStage());

                }
            });
            edEvent.setOnMouseExited(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {

                    descrPopup.hide();
                }
            });
            buttonEventLayered.getChildren().add(edEvent);

            schedule.getChildren().add(buttonEventLayered);
        }
        }
        return schedule;
    }


    /** Adds a To-Do list to the right side of the window in the form of a VBox.*/
    public VBox addToDo(){
        //set up main To Do list section
        VBox outerBox= new VBox();
        VBox schedule = new VBox();
        Text title = new Text();
        title.setText("To Do:");
        //Add new items to list
        HBox toDoInputBox = new HBox();
        TextField toDoInput = new TextField();
        Button toDoSubmit = new Button();
        toDoSubmit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //to not add empty inputs as to do options
                if (!toDoInput.getText().equals("")) {
                    String toDoString = toDoInput.getText();
                    CheckBox toDoItem = new CheckBox(toDoString);
                    schedule.getChildren().add(toDoItem);
                    controller.saveToDo(toDoString);

                    //make item delete once checkbox clicked
                    toDoItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            schedule.getChildren().removeAll(toDoItem);
                            model.removeToDo(toDoString);
                        }});
                }
                //set as empty text field to avoid adding duplicates
                toDoInput.setText("");
            }});
        toDoSubmit.setText("Add");
        toDoInputBox.getChildren().addAll(toDoInput, toDoSubmit);
        toDoInputBox.setSpacing(10);
        schedule.getChildren().addAll(title, toDoInputBox);
        schedule.setAlignment(Pos.CENTER_LEFT);
        outerBox.getChildren().addAll(schedule);
        schedule.getStyleClass().add("VBox");
        outerBox.getStyleClass().add("VBoxSpecial");
        title.getStyleClass().add("VBoxHeader");

        //get items from file and put them in schedule
        ArrayList toDoList = controller.getToDoList();
        for (int i = 0; i < toDoList.size(); i++) {
            String toDoStr = toDoList.get(i).toString();
            CheckBox toDo = new CheckBox((String) toDoList.get(i));
            toDo.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //delete event
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    schedule.getChildren().removeAll(toDo);
                    model.removeToDo(toDoStr);
                }});
            schedule.getChildren().add(toDo);
        }
        return outerBox;
    }

    /** Creates the region for the previous day that gets added
     * to the BorderPane at the bottom left of the window.
     * Makes the region a button so that when it is clicked
     * on it goes to that day in the calendar.*/
    public StackPane addYesterday(){
        LocalDate yesterday = clickedDate.minusDays(1);
        StackPane outerStackPane = new StackPane();
        Button hiddenButton = new Button();
        hiddenButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene dailyScene = controller.switchDailyView(yesterday);
                dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
                controller.getPrimaryStage().setScene(dailyScene);
                controller.getPrimaryStage().setTitle("Daily Calendar");
            }
        });
        hiddenButton.setMinWidth(250);
        hiddenButton.setMinHeight(100);
        VBox outerBox = new VBox();
        VBox yesterdaySchedule = addOtherDayEvents(yesterday);
        Text day = new Text();
        day.setText(String.valueOf(yesterday.getMonth() + " " +
                String.valueOf(yesterday.getDayOfMonth())));
        day.getStyleClass().add("VBoxHeader");
        outerBox.getChildren().addAll(day, yesterdaySchedule);
        outerStackPane.getChildren().addAll(outerBox, hiddenButton);
        outerStackPane.getStyleClass().add("VBox");
        outerStackPane.setMinWidth(250);
        hiddenButton.getStyleClass().add("specialButton");
        outerStackPane.getStyleClass().add("stackPane");

        return outerStackPane;
    }

    /** Creates the region for the next day that gets added
     * to the BorderPane at the bottom right of the window. 
     * * Makes the region a button so that when it is clicked
     * on it goes to that day in the calendar.*/
    public StackPane addTomorrow(){
        LocalDate tomorrow = clickedDate.plusDays(1);
        StackPane outerStackPane = new StackPane();
        VBox outerBox = new VBox();
        Button btn = new Button();
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene dailyScene = controller.switchDailyView(tomorrow);
                dailyScene.getStylesheets().add("src/DailyView_Stylesheet.css");
                controller.getPrimaryStage().setScene(dailyScene);
                controller.getPrimaryStage().setTitle("Daily Calendar");
            }
        });
        btn.setMinWidth(250);
        btn.setMinHeight(100);
        VBox tomorrowSchedule = addOtherDayEvents(tomorrow);
        Text day = new Text();
        day.setText(String.valueOf(tomorrow.getMonth() + " " +
                String.valueOf(tomorrow.getDayOfMonth())));
        day.getStyleClass().add("VBoxHeader");
        outerBox.getChildren().addAll(day, tomorrowSchedule);
        outerStackPane.getChildren().addAll(outerBox, btn);
        outerStackPane.getStyleClass().add("VBox");
        outerStackPane.setMinWidth(250);
        btn.getStyleClass().add("specialButton");
        outerStackPane.getStyleClass().add("stackPane");

        return outerStackPane;
    }


    /** updateEvent places new event onto daily view window, setting up its appearance
     * and adding description hover box and edit event button*/
    public void updateEvent() {
        StackPane buttonEventLayered = new StackPane();
        //creates stackPane that will eventually hold both the event information in an hbox
        // and an overlying button to edit event
        String title = model.getEventTitle();
        String description = model.getEventDescr();
        String hour = model.getEventHour();
        String minute = model.getEventMin();
        String AMPM = model.getEventAMPM();
        String hourEnd = model.getEventHourEnd();
        String minuteEnd = model.getEventMinEnd();
        String AMPMEnd = model.getEventAMPMEnd();
        String color = model.getEventColor();
        LocalDate eventDate = model.getEventDate();

        if (eventDate.equals(clickedDate)) {
            //limits length of title that can be displayed on screen
            if (title.length() > 50) {
                viewableTitle = title.substring(0, 47) + "...";
            } else {
                viewableTitle = title;
            }

            HBox newEventBox = new HBox();

            Text newEvent = new Text();
            newEvent.setText(hour + ":" + minute + AMPM + "-" + hourEnd + ":" + minuteEnd + AMPMEnd + " " + viewableTitle);
            newEventBox.getChildren().addAll(newEvent);
            newEventBox.setAlignment(Pos.CENTER_LEFT);
            newEventBox.getStyleClass().add("eventBox");
            newEventBox.setStyle("-fx-background-color: " + color + ";");

            //creates edit event button that links to EditEvent popup window
            Button edEvent = new Button();
            edEvent.getStyleClass().add("editEventButton");
            double boxWidth = newEventBox.getWidth();
            edEvent.setMaxWidth(650);
            edEvent.setMaxHeight(2);

            edEvent.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //passes in event information into EditEvent so that they can display as default values in popup
                    EditEvent editEvent = new EditEvent(controller, model, DailyView.this, title,
                            description, hour, minute, AMPM, hourEnd, minuteEnd, AMPMEnd,
                            clickedDate, buttonEventLayered);
                    Stage editStage = new Stage();
                    editEvent.start(editStage);
                }
            });

            //limits the length of description that can be viewed in hover box
            if (description.length() > 100) {
                viewableDescription = description.substring(0, 100);
            } else {
                viewableDescription = description;
            }

            //makes hbox that shows description, and popup that it is contained within
            HBox popupBox = descrBox(viewableDescription);
            popupBox.setMinHeight(30);
            popupBox.setMinWidth(100);
            popupBox.setAlignment(Pos.CENTER);
            popupBox.getStyleClass().add("descrPopup");
            Popup descrPopup = new Popup();
            descrPopup.getContent().add(popupBox);
            descrPopup.setAutoFix(true);

            //when event is hovered over, description popup displays
            edEvent.setOnMouseMoved(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent t) {

                    descrPopup.show(controller.getPrimaryStage());
                }
            });
            //description goes away when mouse is no longer hovering over event
            edEvent.setOnMouseExited(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {

                    descrPopup.hide();
                }
            });

            buttonEventLayered.getChildren().add(newEventBox);
            buttonEventLayered.getChildren().add(edEvent);
            schedule.getChildren().add(buttonEventLayered);
            eventNum++;
        }
        else if(eventDate.equals(clickedDate.plusDays(1))){
            view.setBottom(addBottomBorderPane());
        }
        else if(eventDate.equals(clickedDate.minusDays(1))){
            view.setBottom(addBottomBorderPane());
        }
    }


    private HBox descrBox(String description){
        Label descriptionLabel = new Label();
        descriptionLabel.setText(description);

        return new HBox(descriptionLabel);
    }

    /** adds first two events on neighboring days to later be displayed at
     * the bottom of the screen */
    public VBox addOtherDayEvents(LocalDate date) {
        VBox eventHolder = new VBox();
        EVENTS:
        {
            ArrayList todayEvents;
            events = model.getEvents();


            Calendar today = Calendar.getInstance();

            //get the current date as Strings
            String dayOfMonthStr = String.valueOf(date.getDayOfMonth());
            String monthStr = String.valueOf(date.getMonthValue());
            String yearStr = String.valueOf(date.getYear());

            try {
                HashMap yearEvents = (HashMap) events.get(yearStr);
                HashMap monthEvents = (HashMap) yearEvents.get(monthStr);
                todayEvents = (ArrayList) monthEvents.get(dayOfMonthStr);
                eventNum = todayEvents.size();
            } catch (NullPointerException e) {
                break EVENTS;
            }
            if (eventNum < 2) {
                for (int i = 0; i < todayEvents.size(); i++) {

                    HashMap current = (HashMap) todayEvents.get(i);

                    String title = (String) current.get("title");
                    if (title.length() > 30) {
                        viewableTitle = title.substring(0, 27) + "...";
                    } else {
                        viewableTitle = title;
                    }

                    String hour = (String) current.get("hourStart");
                    String minute = (String) current.get("minute");
                    String AMPM = (String) current.get("AMPM");

                    String color = (String) current.get("color");

                    HBox newEventBox = new HBox();
                    Text newEvent = new Text();
                    newEvent.setText(viewableTitle);
                    newEventBox.getChildren().addAll(newEvent);
                    newEventBox.getStyleClass().add("eventBox");
                    newEventBox.setStyle("-fx-background-color: " + color + ";");

                    eventHolder.getChildren().add(newEventBox);
                }


            } else {
                for (int i = 0; i < 2; i++) {

                    HashMap current = (HashMap) todayEvents.get(i);

                    String title = (String) current.get("title");
                    if (title.length() > 30) {
                        viewableTitle = title.substring(0, 27) + "...";
                    } else {
                        viewableTitle = title;
                    }

                    String hour = (String) current.get("hourStart");
                    String minute = (String) current.get("minute");
                    String AMPM = (String) current.get("AMPM");

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
