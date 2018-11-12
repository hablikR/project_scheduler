package scheduler.main;


import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import scheduler.gui.GUIController;
import scheduler.util.SQLManager;


/**
 * GUI interfész a PDF extractorhoz. Java FX implementáció
 *
 * @author Robert Hablik
 */
public class SchedulerContoller extends Application {
    /**
     * A GUI fő Stage objektuma
     */
    public static Stage mRootStage;

    /** A GUI fő Pane objeltuma */
    public static Pane mRootPane;

    /**
     * Globálisan elérhető GUI kontroller referencia
     */
    public static GUIController sController;

    /**
     * Globális adatbázis menedzser
     */
    public static SQLManager sqlManager;


    @Override
    public void start(Stage stage) throws Exception {

        mRootStage = stage;

        // Load fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../schedulerForm.fxml"));

        // Get root pane reference
        mRootPane = (Pane) loader.load();

        // Get reference to our controller
        sController = loader.<GUIController>getController();

        // Create a new scene
        Scene scene = new Scene(mRootPane, 1400, 1100);

        // Set title
        stage.setTitle("TITLE " );
        //stage.getIcons().add(new Image("file:./icons/bosch.jpg"));
        stage.setScene(scene);

        // Set scene size
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        stage.show();
    }
    /**
     * Run the application
     *
     * @param args
     */
    public static void main(String[] args) {
        sqlManager = new SQLManager("db");
        launch(args);

    }

}
