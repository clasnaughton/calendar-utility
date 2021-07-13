package src.Sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Separator;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Graphics for pop up menu that allows user to add or edit event.
 * Contains options to create event title, description, start/end time, repeat option,
 * and color code event. References css file event_stylesheet.css
 */
public class EditEvent{
    EventModelInterface model;
    EventControllerInterface controller;
    DailyObserver observer;

    Button save;
    Button cancel;
    Button delete;
    String eventTitle;
    String description;
    String hour;
    String min;
    String AMPM;
    String hourEnd;
    String minEnd;
    String AMPMEnd;
    StackPane dailyEvent;
    LocalDate clickedDate;


    /**Constructor for EditEvent. Creates instance variables from
     * the information that was previously stored about the event.
     */
    public EditEvent(EventControllerInterface controller, EventModelInterface model,
                     DailyObserver observer, String eventTitle, String description, String hour,
                     String min, String AMPM, String hourEnd, String minEnd, String AMPMEnd,
                     LocalDate clickedDate, StackPane dailyEvent){
        this.observer = observer;
        this.controller = controller;
        this.model = model;
        this.eventTitle = eventTitle;
        this.description = description;
        this.hour = hour;
        this.min = min;
        this.AMPM = AMPM;
        this.hourEnd = hourEnd;
        this.minEnd = minEnd;
        this.AMPMEnd = AMPMEnd;
        this.dailyEvent = dailyEvent;
        this.clickedDate = clickedDate;
    }

    /** Creates scene, adds elements to window. Additionally, runs
     * through the process of submitting new event
     * information to controller. */
    public void start(Stage primaryStage) {
        VBox outline = new VBox();
        Scene scene = new Scene(outline, 550, 325);
        scene.getStylesheets().add("src/event_stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Edit Event Menu");

        Text title = new Text();
        title.setText("Edit Event:");
        title.getStyleClass().add("textHeader");
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.TOP_CENTER);

        //box for entering new event's title
        HBox eventBox = new HBox();
        Label eTitle = new Label();
        eTitle.setText("Event Title: ");
        TextField eventInput = new TextField(this.eventTitle);
        eventBox.getChildren().addAll(eTitle, eventInput);
        eventBox.setPadding(new Insets(10));
        eventBox.setSpacing(5);

        //box for entering new event's description
        HBox descrBox = new HBox();
        Label eDescr = new Label();
        eDescr.setText("Description:");
        TextField descrInput = new TextField(this.description);
        descrBox.getChildren().addAll(eDescr, descrInput);
        descrBox.setPadding(new Insets(10));
        descrBox.setSpacing(5);

        HBox categoryBox = setCategory();
        HBox startTimeBox = startTime("Start:", this.hour, this.min, this.AMPM);
        startTimeBox.setPadding(new Insets(10));
        HBox endTimeBox1 = endTime("End:", this.hourEnd, this.minEnd, this.AMPMEnd);
        endTimeBox1.setPadding(new Insets(10));
        HBox saveCancelBox = new HBox();

        //cancel button which closes addEvent window when clicked
        cancel = new Button();
        cancel.setText("Cancel");
        cancel.getStyleClass().add("cancelButton");
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });

        delete = new Button();
        delete.setText("Delete");
        delete.getStyleClass().add("cancelButton");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                model.deleteEvent(eventTitle, clickedDate);
                ((Pane) dailyEvent.getParent()).getChildren().remove(dailyEvent);
                primaryStage.close();
            }
        });

        //save button sends all inputted information to controller when clicked
        save = new Button();
        save.setText("Save");
        save.getStyleClass().add("saveButton");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                { CHECK:
                if(!eventInput.getText().equals("") && !descrInput.getText().equals("")){
                    String evtName = eventInput.getText();
                    String evtDescr = descrInput.getText();

                    //retrieving time info from boxes that contain it
                    List<javafx.scene.Node> childList = startTimeBox.getChildren();
                    ChoiceBox hourInput = (ChoiceBox) childList.get(1);
                    ChoiceBox minInput = (ChoiceBox) childList.get(2);
                    RadioButton amButton = (RadioButton) childList.get(3);
                    RadioButton pmButton = (RadioButton) childList.get(4);
                    DatePicker startDateInput = (DatePicker) childList.get(5);
                    List<javafx.scene.Node> childListEnd = endTimeBox1.getChildren();
                    ChoiceBox hourInputEnd = (ChoiceBox) childListEnd.get(1);
                    ChoiceBox minInputEnd = (ChoiceBox) childListEnd.get(2);
                    RadioButton amButtonEnd = (RadioButton) childListEnd.get(3);
                    RadioButton pmButtonEnd = (RadioButton) childListEnd.get(4);

                    List<javafx.scene.Node> categoryInput = categoryBox.getChildren();
                    CheckBox redInput = (CheckBox) categoryInput.get(1);
                    CheckBox orangeInput = (CheckBox) categoryInput.get(2);
                    CheckBox yellowInput = (CheckBox) categoryInput.get(3);
                    CheckBox greenInput = (CheckBox) categoryInput.get(4);
                    CheckBox blueInput = (CheckBox) categoryInput.get(5);
                    CheckBox purpleInput = (CheckBox) categoryInput.get(6);

                    //sends controller specific color to be displayed based on user choice
                    if(redInput.isSelected()) {
                        controller.addColor("SALMON");
                    }
                    else if(orangeInput.isSelected()) {
                        controller.addColor("SANDYBROWN");
                    }
                    else if(yellowInput.isSelected()) {
                        controller.addColor("PALEGOLDENROD");
                    }
                    else if(greenInput.isSelected()) {
                        controller.addColor("YELLOWGREEN");
                    }
                    else if(blueInput.isSelected()) {
                        controller.addColor("SKYBLUE");
                    }
                    else if(purpleInput.isSelected()) {
                        controller.addColor("THISTLE");
                    }
                    else{
                        controller.addColor("TRANSPARENT");
                    }

                    //gets time information for submitting to controller and to verify validity
                    Object hour = hourInput.getValue();
                    Object min = minInput.getValue();
                    Object hourEnd = hourInputEnd.getValue();
                    Object minEnd = minInputEnd.getValue();
                    LocalDate startDate = startDateInput.getValue();
                    String tempStartHour = (String) hour;
                    String tempEndHour = (String) hourEnd;
                    if (hour.equals("Hour") || min.equals("Min")) {
                        runErrorMessage();
                        break CHECK;

                    } else {
                        String startAMPM;
                        String endAMPM;
                        if (amButton.isSelected()) {
                            controller.addEventAMPM("AM");
                            startAMPM = "AM";
                            if (tempStartHour == "12"){
                                tempStartHour = "0";
                            }
                        } else if (pmButton.isSelected()) {
                            controller.addEventAMPM("PM");
                            startAMPM = "PM";
                            if (tempStartHour == "12"){
                                tempStartHour = "12";
                            }
                            else{
                                tempStartHour = String.valueOf(Integer.valueOf((String) hour) + 12);
                            }
                        } else {
                            runErrorMessage();
                            break CHECK;
                        }
                        if (amButtonEnd.isSelected()) {
                            controller.addEventAMPMEnd("AM");
                            endAMPM = "AM";
                            if (tempEndHour == "12"){
                                tempEndHour = "0";
                            }
                        } else if (pmButtonEnd.isSelected()) {
                            controller.addEventAMPMEnd("PM");
                            endAMPM = "PM";
                            if (tempEndHour == "12"){
                                tempEndHour = "12";
                            }
                            else{
                                tempEndHour = String.valueOf(Integer.valueOf((String) hourEnd) + 12);
                            }
                        } else {
                            runErrorMessage();
                            break CHECK;
                        }

                        String start = (String) tempStartHour + ":" + min + " " + startAMPM;
                        String end = (String) tempEndHour + ":" + minEnd + " " + endAMPM;
                        try {
                            //catches error if end time is before start time
                            Date startTime = new SimpleDateFormat("HH:mm a", Locale.ENGLISH).parse(start);
                            Date endTime = new SimpleDateFormat("HH:mm a", Locale.ENGLISH).parse(end);
                            if (startTime.after(endTime)) {
                                runTimeErrorMessage();
                                break CHECK;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //sends controller time information as strings
                        String hourString = (String) hour;
                        controller.addEventHour(hourString);
                        String minString = (String) min;
                        controller.addEventMin(minString);
                        String hourEndString = (String) hourEnd;
                        controller.addEventHourEnd(hourEndString);
                        String minEndString = (String) minEnd;
                        controller.addEventMinEnd(minEndString);
                        //TODO: Add AM/PM designation, switch order
                    }

                    //sends controller title, description, start date
                    model.deleteEvent(eventTitle, clickedDate);
                    ((Pane) dailyEvent.getParent()).getChildren().remove(dailyEvent);
                    controller.addEventTitle(evtName);
                    controller.addEventDescription(evtDescr);
                    controller.addEventDate(startDate);
                    controller.save(observer);
                    primaryStage.close();
                }

                //when title or description are null, run alert popup
                else {
                    runErrorMessage();
                    break CHECK;

                }
                }
            }});


        saveCancelBox.getChildren().addAll(cancel, delete, save);
        saveCancelBox.setPadding(new Insets(10));
        saveCancelBox.setSpacing(5);
        saveCancelBox.setAlignment(Pos.BOTTOM_RIGHT);
        outline.getChildren().addAll(titleBox, eventBox, descrBox, startTimeBox, endTimeBox1,
               categoryBox, saveCancelBox);
        outline.setPadding(new Insets(10));
    }

    //makes pop up box for empty fields
    private void runErrorMessage(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Oops");
        alert.setHeaderText("Empty Text Field");
        alert.setContentText("Please fill out all empty forms");
        alert.showAndWait();

    }

    //alert pop up for when user enters invalid time
    private void runTimeErrorMessage(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Oops");
        alert.setHeaderText("Invalid Times");
        alert.setContentText("Please choose valid times.");
        alert.showAndWait();

    }

     /**creates HBox that holds category/color options, chosen by clicking
     colored checkboxes */
    private HBox setCategory() {
        HBox categoryBox = new HBox();
        Label ctgLabel = new Label();
        ctgLabel.setText("Event Color:");
        CheckBox cRed = new CheckBox();
        cRed.getStyleClass().add("cbRed");
        CheckBox cOrange = new CheckBox();
        cOrange.getStyleClass().add("cbOrange");
        CheckBox cYellow = new CheckBox();
        cYellow.getStyleClass().add("cbYellow");
        CheckBox cGreen = new CheckBox();
        cGreen.getStyleClass().add("cbGreen");
        CheckBox cBlue = new CheckBox();
        cBlue.getStyleClass().add("cbBlue");
        CheckBox cPurple = new CheckBox();
        cPurple.getStyleClass().add("cbPurple");

        cRed.setOnAction((event) -> {
            boolean selected = cRed.isSelected();
            if (cOrange.isSelected()) {
                cOrange.setSelected(false);
            }
            if (cYellow.isSelected()) {
                cYellow.setSelected(false);
            }

            if (cGreen.isSelected()) {
                cGreen.setSelected(false);
            }

            if (cBlue.isSelected()) {
                cBlue.setSelected(false);
            }

            if (cPurple.isSelected()) {
                cPurple.setSelected(false);
            }
        });


        cOrange.setOnAction((event) -> {
            boolean selected = cOrange.isSelected();
            if (cRed.isSelected()) {
                cRed.setSelected(false);
            }

            if (cYellow.isSelected()) {
                cYellow.setSelected(false);
            }

            if (cGreen.isSelected()) {
                cGreen.setSelected(false);
            }

            if (cBlue.isSelected()) {
                cBlue.setSelected(false);
            }

            if (cPurple.isSelected()) {
                cPurple.setSelected(false);
            }
        });

        cYellow.setOnAction((event) -> {
            boolean selected = cYellow.isSelected();
            if (cRed.isSelected()) {
                cRed.setSelected(false);
            }

            if (cOrange.isSelected()) {
                cOrange.setSelected(false);
            }

            if (cGreen.isSelected()) {
                cGreen.setSelected(false);
            }

            if (cBlue.isSelected()) {
                cBlue.setSelected(false);
            }

            if (cPurple.isSelected()) {
                cPurple.setSelected(false);
            }

        });


        cGreen.setOnAction((event) -> {
            boolean selected = cGreen.isSelected();
            if (cRed.isSelected()) {
                cRed.setSelected(false);
            }

            if (cOrange.isSelected()) {
                cOrange.setSelected(false);
            }

            if (cYellow.isSelected()) {
                cYellow.setSelected(false);
            }

            if (cBlue.isSelected()) {
                cBlue.setSelected(false);
            }

            if (cPurple.isSelected()) {
                cPurple.setSelected(false);
            }

        });


        cBlue.setOnAction((event) -> {
            boolean selected = cBlue.isSelected();
            if (cRed.isSelected()) {
                cRed.setSelected(false);
            }

            if (cOrange.isSelected()) {
                cOrange.setSelected(false);
            }

            if (cYellow.isSelected()) {
                cYellow.setSelected(false);
            }

            if (cGreen.isSelected()) {
                cGreen.setSelected(false);
            }

            if (cPurple.isSelected()) {
                cPurple.setSelected(false);
            }

        });

        cPurple.setOnAction((event) -> {
            boolean selected = cPurple.isSelected();
            if (cRed.isSelected()) {
                cRed.setSelected(false);
            }

            if (cOrange.isSelected()) {
                cOrange.setSelected(false);
            }

            if (cYellow.isSelected()) {
                cYellow.setSelected(false);
            }

            if (cGreen.isSelected()) {
                cGreen.setSelected(false);
            }

            if (cBlue.isSelected()) {
                cBlue.setSelected(false);
            }
        });

        categoryBox.getChildren().addAll(ctgLabel, cRed, cOrange, cYellow, cGreen, cBlue,
                cPurple);
        categoryBox.setPadding(new Insets(10));
        categoryBox.setSpacing(5);

        return categoryBox;
    }

    //creates HBox that contains all of the settings for choosing a start time
    private HBox startTime(String timeLabel, String prevHour, String prevMin, String prevAMPM) {
        HBox timeBox = new HBox();
        Label chooseTimeLabel = new Label(timeLabel);
        ChoiceBox hourChoice = new ChoiceBox();
        hourChoice.setItems(FXCollections.observableArrayList("Hour", new Separator(), "12",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        hourChoice.setValue(prevHour);
        ChoiceBox minChoice = new ChoiceBox();
        minChoice.setItems(FXCollections.observableArrayList("Min", new Separator(), "00",
                "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"));
        minChoice.setValue(prevMin);


        ToggleGroup selectAMPM = new ToggleGroup();
        RadioButton selectAM = new RadioButton();
        selectAM.setText("AM");
        selectAM.setToggleGroup(selectAMPM);
        selectAM.setUserData("am");
        RadioButton selectPM = new RadioButton();
        selectPM.setText("PM");
        selectPM.setToggleGroup(selectAMPM);
        selectPM.setUserData("pm");
        if (prevAMPM.equals("AM")){
            selectAM.setSelected(true);
        }
        else {
            selectPM.setSelected(true);

        }
        DatePicker datePicker = new DatePicker(clickedDate);
        datePicker.setOnAction(event -> {
            LocalDate date = datePicker.getValue();
        });
        timeBox.getChildren().addAll(chooseTimeLabel, hourChoice, minChoice, selectAM,
                selectPM, datePicker);
        timeBox.setSpacing(5);
        return timeBox;
    }

    //method similar to startTime, but does not contain date picker object
    private HBox endTime(String timeLabel, String prevHourEnd, String prevMinEnd, String prevAMPMEnd) {
        HBox timeBox1 = new HBox();
        Label chooseTimeLabel1 = new Label(timeLabel);
        ChoiceBox hourChoice1 = new ChoiceBox();
        hourChoice1.setItems(FXCollections.observableArrayList("Hour", new Separator(), "12",
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        hourChoice1.setValue(prevHourEnd);
        ChoiceBox minChoice1 = new ChoiceBox();
        minChoice1.setItems(FXCollections.observableArrayList("Min", new Separator(), "00",
                "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"));
        minChoice1.setValue(prevMinEnd);
        ToggleGroup selectAMPM1 = new ToggleGroup();
        RadioButton selectAM1 = new RadioButton();
        selectAM1.setText("AM");
        selectAM1.setToggleGroup(selectAMPM1);
        RadioButton selectPM1 = new RadioButton();
        selectPM1.setText("PM");
        selectPM1.setToggleGroup(selectAMPM1);
        if (prevAMPMEnd.equals("AM")){
            selectAM1.setSelected(true);
        }
        else {
            selectPM1.setSelected(true);

        }
        timeBox1.getChildren().addAll(chooseTimeLabel1, hourChoice1, minChoice1, selectAM1,
                selectPM1);
        timeBox1.setSpacing(5);
        return timeBox1;
    }

}