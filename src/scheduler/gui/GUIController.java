package scheduler.gui;

import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import scheduler.business_logic.ScedulingRules;
import scheduler.dbModels.Job;
import scheduler.dbModels.Operation;
import scheduler.util.SQLManager;
import scheduler.util.Util;

public class GUIController implements Initializable {
    //#region operation
    @FXML
    TextField op_jobID_text;
    @FXML
    TextField op_run_text;
    @FXML
    TextField op_Name_Text;

    @FXML
    Button op_cancel_btn;
    @FXML
    Button op_submit_btn;
    @FXML
    Button operation_remove_btn;
    @FXML
    ListView<String> operationID_List;
    @FXML
    ComboBox<String> operation_dropdown;

    //#endregion
    //#region Job
    @FXML
    TextField job_Name_Text;
    @FXML
    TextField job_arrivaltime_Text;
    @FXML
    TextField job_deadline_Text;
    @FXML
    Button job_add_Button;

    @FXML
    ListView<Integer> jobID_List;

    @FXML
    Button job_remove_btn;
    //#endregion

    @FXML
    Button run_Button;
    @FXML
    Button randomData_Button;


    @FXML
    private Text counterText;


    private SQLManager sqlManager;

    public GUIController() {

        sqlManager = new SQLManager("db");
    }

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        System.out.println("Initializing GUI controller");
        init();
    }

    public void init() {
        buttonAction();
        fillIDLists();
        fillOperationList();
    }

    public void buttonAction() {
        op_submit_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                try {
                    sqlManager.insertOperation(new Operation(op_jobID_text.getText(),
                            op_Name_Text.getText(), op_run_text.getText(), operation_dropdown.valueProperty().getValue()));
                    clearOperationFiled();
                } catch (NumberFormatException error) {
                    System.out.println("Wrong number: " + error);
                }
                fillIDLists();
            }
        });

        op_cancel_btn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                clearOperationFiled();
                fillIDLists();
            }
        });

        operation_remove_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //int temp_id = findIDinString(operationID_List.getSelectionModel().getSelectedItem());
                sqlManager.removebyOperationByID(findIDinString(operationID_List.getSelectionModel().getSelectedItem()));
                fillIDLists();
            }
        });

        job_remove_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sqlManager.deleteJob(jobID_List.getSelectionModel().getSelectedItem());
                fillIDLists();
            }
        });

        job_add_Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sqlManager.inserJob(new Job(job_Name_Text.getText(), job_arrivaltime_Text.getText(), job_deadline_Text.getText()));
                fillIDLists();
            }
        });

        run_Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              // ScedulingRules s = new ScedulingRules();
               // s.spt();
                setTimer();
            }
        });

        randomData_Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generateRandomData();
            }
        });
    }

    private void fillIDLists() {
        operationID_List.getItems().clear();
        jobID_List.getItems().clear();

        operationID_List.getItems().addAll(sqlManager.downloadOperationIDList());
        jobID_List.getItems().addAll(sqlManager.downloadJobIDList());
    }

    private void fillOperationList() {
        operation_dropdown.getItems().addAll(sqlManager.operationType());
    }

    private void clearOperationFiled() {
        op_jobID_text.clear();
        op_Name_Text.clear();
        op_run_text.clear();
        operation_dropdown.valueProperty().setValue(null);
    }

    private int findIDinString(String data) {
        String[] splitted = data.split("\\s+");
        return Integer.parseInt(splitted[1]);
    }

    private void generateRandomData(){

        Util util = new Util();
        sqlManager.clearTables();

        List<Job> randomJobList = util.generateJobs(10);
        List<Operation> randomOperationList = util.generateOperations(10);

        for (Job j : randomJobList) {
            sqlManager.inserJob(j);
        }
        fillIDLists();
        for (Operation o : randomOperationList) {
            sqlManager.insertOperation(o);
        }
        fillIDLists();
    }

    public void setTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 1;
            public void run() {
                if(i <= 10)
                {
                    Platform.runLater(() -> counterText.setText("Time: " + i));
                    i++;
                }
                else
                    timer.cancel();
            }
        }, 1000,1000);
    }

}
