package scheduler.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import scheduler.business_logic.ScedulingRules;
import scheduler.business_logic.ScheduledTask;
import scheduler.dbModels.Job;
import scheduler.dbModels.Operation;
import scheduler.dbModels.OperationResult;
import scheduler.dbModels.Resources;
import scheduler.util.PublicVariables;
import scheduler.util.SQLManager;
import scheduler.util.Util;

import java.net.URL;
import java.util.*;

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
    Button reset_Button;
    @FXML
    Button randomData_Button;
    @FXML
    ChoiceBox rules_DropDown;

    @FXML
    public Text counterText;

//    @FXML
//    NumberAxis x_axis;
//    @FXML
//    CategoryAxis y_axis;
//    @FXML
//    BarChart<Number,String> spt_chart;
//
    //#region SPT Chart
    @FXML
    NumberAxis x_axisStacked;
    @FXML
    CategoryAxis y_axisStacked;
    @FXML
    StackedBarChart<Number, String> spt_stackedChart;
    //#endregion


    @FXML
    public Button show_Button;

    private SQLManager sqlManager;
//
//    private void chartOneColor(){
//        x_axis.setLabel("Percent");
//        x_axis.setTickLabelRotation(90);
//        y_axis.setLabel("Performance");
//
//        XYChart.Series<Number, String> series1 = new XYChart.Series();
//        series1.getData().add(new XYChart.Data( 81,"lofasz"));
//        spt_chart.getData().add(series1);
//    }

    private void sptChart(List<OperationResult> operationResultList){
        x_axisStacked.setLabel("Percent");
        x_axisStacked.setTickLabelRotation(90);
        y_axisStacked.setLabel("Performance");

        List<String> groups = new ArrayList<>();


        XYChart.Series<Number, String> series1 = new XYChart.Series<Number, String>();
        XYChart.Series<Number, String> series2 = new XYChart.Series<Number, String>();

        series1.setName("watingTime");
//        for(int i=0; i < 35; i++){
//            groups.add(operationResultList.get(i).getName());
//            series1.getData().add(new XYChart.Data<Number, String>(operationResultList.get(i).getStartTime(),
//                    operationResultList.get(i).getName()));
//        }
        for (OperationResult o : operationResultList) {

            groups.add(o.getName());
            series1.getData().add(new XYChart.Data<Number, String>(o.getStartTime(), o.getName()));
        }

       series2.setName("runTime");
       /* for(int i=0; i < 35; i++){
            series2.getData().add(new XYChart.Data<Number, String>(operationResultList.get(i).getRunTime(),
                    operationResultList.get(i).getName()));
        }*/

        for (OperationResult o : operationResultList) {
            series2.getData().add(new XYChart.Data<Number, String>(o.getRunTime(), o.getName()));
        }

        y_axisStacked.setCategories(FXCollections.<String>observableArrayList(groups));

        spt_stackedChart.setCategoryGap(1);
        spt_stackedChart.autosize();
        spt_stackedChart.getData().addAll(series1, series2);

    }
    public GUIController() {

        sqlManager = new SQLManager("db");
    }

    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        System.out.println("Initializing GUI controller");
        init();
    }

    private void init() {
        buttonAction();
        fillIDLists();
        fillOperationList();
        fillDropDownList();
    }

    private void buttonAction() {
        op_submit_btn.setOnAction(e -> {
            try {
                sqlManager.insertOperation(new Operation(op_jobID_text.getText(),
                        op_Name_Text.getText(), op_run_text.getText(), operation_dropdown.valueProperty().getValue()));
                clearOperationFiled();
            } catch (NumberFormatException error) {
                System.out.println("Wrong number: " + error);
            }
            fillIDLists();
        });

        op_cancel_btn.setOnAction(e -> {
            clearOperationFiled();
            fillIDLists();
        });

        operation_remove_btn.setOnAction(event -> {
            sqlManager.removebyOperationByID(findIDinString(operationID_List.getSelectionModel().getSelectedItem()));
            fillIDLists();
        });

        job_remove_btn.setOnAction(event -> {
            sqlManager.deleteJob(jobID_List.getSelectionModel().getSelectedItem());
            fillIDLists();
        });

        job_add_Button.setOnAction(event -> {
            sqlManager.inserJob(new Job(job_Name_Text.getText(), job_arrivaltime_Text.getText(), job_deadline_Text.getText()));
            fillIDLists();
        });

        run_Button.setOnAction(event -> {
            //TODO You can here turn off the not needed rules ;)
            erdTimer();
            eddTimer();
            sptTimer();
            lptTimer();
            sssTimer();
            crTimer();

            System.out.println("----Scheduling rules----");

        });

        reset_Button.setOnAction(event -> {
            sqlManager.resetJobs();
            sqlManager.resetOperations();
            sqlManager.resetResources();
            sqlManager.resetResult();
        });

        show_Button.setOnAction(event -> {
            sptChart(sqlManager.getOperationResultList("spt"));
        });
        randomData_Button.setOnAction(event -> generateRandomData());
    }

    private void fillDropDownList() {
        String[] rules = {"SPT", "LPT", "EDD", "SSS", "ERD", "CR"};

        rules_DropDown.getItems().addAll(Arrays.asList(rules));
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

    private void generateRandomData() {

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

    private void sptTimer() {
        ScedulingRules scedulingRules = new ScedulingRules();
        List<ScheduledTask> sceduledTask = new ArrayList<>();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;
            int operationCount = sqlManager.getNotFinishedOperations("spt");

            public void run() {
                if (operationCount != 0) {
                    Platform.runLater(() -> counterText.setText("Time: " + i));
                    System.out.println("SPT Time: " + i);
                    List<ScheduledTask> jobList = scedulingRules.spt_srpt(i);
                    sceduledTask.addAll(jobList);
                    operationCount = sqlManager.getNotFinishedOperations("spt");
                    findFinishedOperation(i, sceduledTask, "spt");
                    i++;
                } else
                    timer.cancel();
            }

        }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
    }

    private void lptTimer() {
        ScedulingRules scedulingRules = new ScedulingRules();
        List<ScheduledTask> sceduledTask = new ArrayList<>();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;
            int operationCount = sqlManager.getNotFinishedOperations("lpt");

            public void run() {
                if (operationCount != 0) {

                    //Platform.runLater(() -> counterText.setText("Time: " + i));
                    List<ScheduledTask> jobList = scedulingRules.lpt_lrpt(i);
                    sceduledTask.addAll(jobList);
                    operationCount = sqlManager.getNotFinishedOperations("lpt");
                    findFinishedOperation(i, sceduledTask, "lpt");
                    i++;
                } else
                    timer.cancel();
            }

        }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
    }

    private void erdTimer() {
        ScedulingRules scedulingRules = new ScedulingRules();
        List<ScheduledTask> sceduledTask = new ArrayList<>();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;
            int operationCount = sqlManager.getNotFinishedOperations("erd");

            public void run() {
                if (operationCount != 0) {

                    //   Platform.runLater(() -> counterText.setText("Time: " + i));
                    System.out.println("ERD Time:" + i);
                    List<ScheduledTask> jobList = scedulingRules.erd(i);
                    sceduledTask.addAll(jobList);
                    operationCount = sqlManager.getNotFinishedOperations("erd");
                    findFinishedOperation(i, sceduledTask, "erd");
                    i++;
                } else
                    timer.cancel();
            }

        }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
    }

    private void eddTimer() {
        ScedulingRules scedulingRules = new ScedulingRules();
        List<ScheduledTask> sceduledTask = new ArrayList<>();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;
            int operationCount = sqlManager.getNotFinishedOperations("edd");

            public void run() {
                if (operationCount != 0) {

                    //  Platform.runLater(() -> counterText.setText("Time: " + i));
                    System.out.println("EDD Time:" + i);
                    List<ScheduledTask> jobList = scedulingRules.edd(i);
                    sceduledTask.addAll(jobList);
                    operationCount = sqlManager.getNotFinishedOperations("edd");
                    findFinishedOperation(i, sceduledTask, "edd");
                    i++;
                } else
                    timer.cancel();
            }

        }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
    }

    private void sssTimer() {
        ScedulingRules scedulingRules = new ScedulingRules();
        List<ScheduledTask> sceduledTask = new ArrayList<>();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;
            int operationCount = sqlManager.getNotFinishedOperations("sss");

            public void run() {
                if (operationCount != 0) {

                    //   Platform.runLater(() -> counterText.setText("Time: " + i));
                    System.out.println("SSS Time:" + i);
                    List<ScheduledTask> jobList = scedulingRules.sss(i);
                    sceduledTask.addAll(jobList);
                    operationCount = sqlManager.getNotFinishedOperations("sss");
                    findFinishedOperation(i, sceduledTask, "sss");
                    i++;
                } else
                    timer.cancel();
            }

        }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
    }

    private void crTimer() {
        ScedulingRules scedulingRules = new ScedulingRules();
        List<ScheduledTask> sceduledTask = new ArrayList<>();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;
            int operationCount = sqlManager.getNotFinishedOperations("cr");

            public void run() {
                if (operationCount != 0) {

                    //   Platform.runLater(() -> counterText.setText("Time: " + i));
                    System.out.println("CR Time:" + i);
                    List<ScheduledTask> jobList = scedulingRules.cr(i);
                    sceduledTask.addAll(jobList);
                    operationCount = sqlManager.getNotFinishedOperations("cr");
                    findFinishedOperation(i, sceduledTask, "cr");
                    i++;
                } else
                    timer.cancel();
            }

        }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
    }

    //simulated annealin
//    private void saTimert() {
//
//
//    }

    private List<ScheduledTask> findFinishedOperation(int actualTime, List<ScheduledTask> sceduledTask, String rule) {
        System.out.println("findFinishedOperation -  RULE: " + rule);
        List<ScheduledTask> endedTasks = new ArrayList<>();

        for (ScheduledTask task : sceduledTask) {
            if (task.getRunTime() == actualTime) {

                System.out.println("Operation finished: " + task.getOperationId() + " Time: " + actualTime);
                //TODO adott job utosó operációjánál a Job táblába be kell írni a végzés idejét
                sqlManager.setJobActive(task.getJobId(), true, rule);
                sqlManager.setOperationFinished(task.getOperationId(), rule);
                Resources resources = sqlManager.findNeededResourceById(task.getResourcesId(), rule);
                int increaseJobCountOnResource = resources.getJob_count();
                increaseJobCountOnResource++;
                sqlManager.setResourceAvailable(task.getResourcesId(),
                        increaseJobCountOnResource,
                        (resources.getTotalRunTime() + task.getRunTime())
                        , rule);
                endedTasks.add(task);
                System.out.println(task.getOperationId() + " finished");
            }
        }

        if (endedTasks.size() > 0)
            sceduledTask.removeAll(endedTasks);
        return sceduledTask;
    }
}
