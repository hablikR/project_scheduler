package scheduler.main;

import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


public class MultiThread  {


    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {

                //feladat külön szálon (time counter stb)

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Done");
                alert.setHeaderText("All process finished!");
                alert.showAndWait().ifPresent(rs -> {

                    if (rs == ButtonType.OK) {

                    }
                });
            }
        };



    }





}
