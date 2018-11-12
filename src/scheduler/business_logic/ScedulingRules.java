package scheduler.business_logic;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Stop;
import scheduler.dbModels.Operation;
import scheduler.dbModels.Resources;
import scheduler.main.MultiThread;
import scheduler.util.SQLManager;

import java.util.ArrayList;
import java.util.List;

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

    public List<ScheduledTask> spt_srpt(int time) {

        //List<ScheduledTask> scheduledTaskList = new ArrayList<>();

        System.out.println("spt run");
        System.out.println("Time: " + time);
        //TODO Ide EZ kell: (4 = time) és csak az operáció ID kell belőle, az alapján kell letölteni az operáció listát
        // SELECT o.id, o.jobid FROM (
        //SELECT id, jobid, sum(run_time) as total_run_time FROM Operation WHERE
        //IsFinished = false
        //group by jobid )
        //AS o
        //inner join
        //(select * from job where arrival_time <= 4) as j
        //on j.ID = o.JobID

        // TODO operationList letöltése a fentebb létrehozott operácó ID k alapján.
        List<Operation> operationList = sqlManager.getOperationList("SELECT * FROM Operation WHERE " +
                "IsFinished = false and " +
                "jobid in ( SELECT ID from JOB where arrival_time <= " + time + " and isAvailable = true )");

        return magic(operationList, time);
    }

    public List<ScheduledTask> lpt_lrpt(int time) {

        System.out.println("lpt run");
        System.out.println("Time: " + time);

        //TODO Ide EZ kell: (4 = time) a fentiek alapján + meg kell benne cserélni, CSÖKKENŐ sorrendebn legyen a total_run_time
        // SELECT o.id, o.jobid FROM (
        //SELECT id, jobid, sum(run_time) as total_run_time FROM Operation WHERE
        //IsFinished = false
        //group by jobid )
        //AS o
        //inner join
        //(select * from job where arrival_time <= 4) as j
        //on j.ID = o.JobID

        // TODO operationList letöltése a fentebb létrehozott operácó ID k alapján.
        List<Operation> operationList = sqlManager.getOperationList("SELECT * FROM Operation WHERE " +
                "IsFinished = false and " +
                "jobid in ( SELECT ID from JOB where arrival_time <= " + time + " and isAvailable = true )");

        return magic(operationList, time);
    }

    private List<ScheduledTask> erd(int time) {
        //Ez kb pont az ami nekem kell, sorban jönnek ami éppen elérhető
        List<Operation> operationList = sqlManager.getOperationList("SELECT * FROM Operation WHERE " +
                "IsFinished = false and " +
                "jobid in ( SELECT ID from JOB where arrival_time <= " + time + " and isAvailable = true )");

        return magic(operationList, time);
    }

    private List<ScheduledTask> edd(int time) {
        //TODO ez meg valami ehhez hasonló kéne legyen
//        SELECT * FROM (
//                SELECT * FROM Operation WHERE
//                IsFinished = false)
//        AS o
//        inner join
//        (select id, min(deadline) from job where arrival_time <= 4) as j
//        on j.ID = o.JobID

        List<Operation> operationList = sqlManager.getOperationList("SELECT * FROM Operation WHERE " +
                "IsFinished = false and " +
                "jobid in ( SELECT ID from JOB where arrival_time <= " + time + " and isAvailable = true )");

        return magic(operationList, time);
    }

//    TODO A legkisebb gyártási időtartalékkal rendelkező job
//    kerül kiválasztásra. Gyártási időtartalék =
//            határidő – indítási időpont – műveleti idők összege.
//
//        private List<Job> sss() {
//        List<Job> jobList = sqlManager.getJobList("SELECT *  FROM Job ORDER BY" +
//                "(deadline - arrival_time -run_time");
//        return jobList;
//    }

        //TODO CR ütemezés
    //A legkisebb kritikus rátával (CR) rendelkező job kerül
//    kiválasztásra. CR = (határidő – aktuális időpont) /
//    hátralévő műveletek idejének összege.
//             Ha CR = 1 a job kritikus.
// Ha CR < 1 a job már késik.
//             Ha CR > 1 a job-nak van tartaléka.

//    public List<Job> cr(int actual_time) {
//        return null;
//    }


    // Here happened the magic :)
    private List<ScheduledTask> magic(List<Operation> operationList, int time) {
        List<ScheduledTask> scheduledTaskList = new ArrayList<>();
        Resources resources;

        int jobid = 0;
        for (Operation o : operationList) {
            if (jobid != o.getJobID()) {
                if (time == 63)
                    System.out.println("now");
                System.out.println("Operation:" + o);
                jobid = o.getJobID();

                resources = sqlManager.findNeededResource(o.getOpType());
                if (resources != null) {
                    setResource(o, resources);
                    operationResultList.add(o);
                    sqlManager.setJobActive(o.getJobID(), false);
                    scheduledTaskList.add(new ScheduledTask(o.getJobID(), o.getId(), resources.getId(), o.getRunTime() + time));
                }
            }
        }
        return scheduledTaskList;
    }

    private void setResource(Operation o, Resources r) {
        sqlManager.setResourceUsed(r.getId());
    }
    
}
