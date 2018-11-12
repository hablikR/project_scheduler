package scheduler.business_logic;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import scheduler.dbModels.Job;
import scheduler.dbModels.Operation;
import scheduler.main.MultiThread;
import scheduler.util.SQLManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScedulingRules {

    private SQLManager sqlManager;

    private MultiThread processor;

    public List<Operation> getOperationResultList() {
        return operationResultList;
    }

    private List<Operation> operationResultList = new ArrayList<>();

    public ScedulingRules() {

        sqlManager = new SQLManager("db");
         }

    public List<Operation> spt_srpt(int time) {

        System.out.println("spt run");
        System.out.println("Time: " + time);
        Resources resources;
        List<Operation> operationListList = sqlManager.getOperationList("SELECT * FROM Operation WHERE " +
                "IsFinished = false and " +
                "jobid in ( SELECT ID from JOB where arrival_time <= " + time + " and isAvailable = true )");

        int jobid = 0;
        for (Operation o : operationListList) {
            if (jobid != o.getJobID()) {
                System.out.println("Operation:" + o);
                jobid = o.getJobID();

                resources = sqlManager.findNeededResource(o.getOpType());
                if(resources != null) {
                    setResource(o, resources);
                    operationResultList.add(o);
                    sqlManager.setJobActive(o.getJobID(), false);

                    //Start a new thread
                    processor = new MultiThread(o, resources);
                    Task<Void> t = processor.createTask();
                    ProgressBar bar = new ProgressBar();
                    bar.progressProperty().bind(t.progressProperty());
                    new Thread(t).start();

                    System.out.println(t);
                }
            }
        }
        return operationListList;
    }

    private void setResource(Operation o, Resources r) {
        sqlManager.setResourceUsed(r.getId());
    }



//
//    public List<Job> lpt() {
//        List<Job> jobList = sqlManager.getJobList("SELECT * FROM Job ORDER BY arrival_time, run_time DESC");
//        return jobList;
//    }
//
//    private List<Job> edd() {
//        List<Job> jobList = sqlManager.getJobList("SELECT * FROM Job ORDER BY deadline");
//
//        return jobList;
//    }
//
//    private List<Job> erd() {
//        List<Job> jobList = sqlManager.getJobList("SELECT * FROM Job ORDER BY arrival_time");
//
//        return jobList;
//    }
//
//    private List<Job> sss() {
//        List<Job> jobList = sqlManager.getJobList("SELECT *  FROM Job ORDER BY" +
//                "(deadline - arrival_time -run_time");
//        return jobList;
//    }
//
//    public List<Job> cr(int actual_time) {
//        return null;
//    }

//    public void startTimer(Operation o) {
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            int i = 1;
//
//            public void run() {
//                if (i <= o.getRunTime()) {
//                    System.out.println("Timer2: " + i);
//                    i++;
//                } else
//                    timer.cancel();
//            }
//        }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
//
//        System.out.println("Timer2 end");
//        sqlManager.setJobActive(o.getJobID(), true);
//        sqlManager.setOperationFinished(o.getId());
//    }

}
