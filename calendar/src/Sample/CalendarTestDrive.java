package src.Sample;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.time.LocalDate;


/**
 * The executable class for the Calendar Utility.
 * Created by Toni Eidmann, Kyra Wilson, and Claudia Naughton on 11/09/17.
 */
public class CalendarTestDrive extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception {
        LocalDate today = LocalDate.now();
        EventModelInterface model = new EventModel();
        EventControllerInterface controller = new EventController(model, primaryStage);
        DailyView view = new DailyView(controller, model, today);



            Scene scene = new Scene(view.asView(), 900, 750);
            scene.getStylesheets().add("src/DailyView_Stylesheet.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Daily View");
            primaryStage.show();


        
    }

    public static void main (String[] args) {
        launch(args);
    }
}
