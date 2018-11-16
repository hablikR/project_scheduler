package scheduler.business_logic;

import scheduler.dbModels.Operation;
import scheduler.dbModels.Resources;
import scheduler.util.SQLManager;

import java.util.ArrayList;
import java.util.List;

public class ScedulingRules {

    private SQLManager sqlManager;

    public ScedulingRules() {

        sqlManager = new SQLManager("db");
    }

    public List<ScheduledTask> spt_srpt(int time) {

        String sqlQuery = "SELECT o.* FROM(\n" +
                "SELECT id, jobid , name, op.total_run_time, type, run_time, sptFinished FROM  Operation \n" +
                "inner join\n" +
                "(\n" +
                "SELECT jobid as j_id, sum(run_time) as total_run_time FROM Operation WHERE sptFinished = false\n" +
                "group by  jobid \n" +
                ") as op\n" +
                "on op.j_id = operation.jobid\n" +
                ") as o\n" +
                "INNER JOIN\n" +
                "(SELECT * FROM Job WHERE isAvailable_spt = true and arrival_time <="+time + ")as j\n" +
                "on j.id = o.jobid where o.sptFinished = false";

        List<Operation> operationList = sqlManager.getOperationList(sqlQuery);

        return magic(operationList, time, "spt");
    }

    public List<ScheduledTask> lpt_lrpt(int time) {

        System.out.println("lpt run");
        System.out.println("Time: " + time);

        String sqlQuery = "SELECT o.* FROM(\n" +
                "SELECT id, jobid , name, op.total_run_time, type, run_time, lptFinished FROM  Operation \n" +
                "inner join\n" +
                "(\n" +
                "SELECT jobid as j_id, sum(run_time) as total_run_time FROM Operation \n" +
                "group by  jobid \n" +
                ") as op\n" +
                "on op.j_id = operation.jobid\n" +
                ") as o\n" +
                "INNER JOIN\n" +
                "(SELECT * FROM Job WHERE isAvailable_lpt = true and arrival_time <="+time+ ")" +
                "as j\n" +
                "on j.id = o.jobid WHERE O.lptFinished = false order by total_run_time desc  ";

        List<Operation> operationList = sqlManager.getOperationList(sqlQuery);

        return magic(operationList, time,"lpt");
    }

    public List<ScheduledTask> erd(int time) {
        String sqlQuery = "SELECT * FROM Operation WHERE erdFinished = false and " +
                "jobid in ( SELECT ID from JOB where arrival_time <= " + time + " and isAvailable_erd = true )";
        List<Operation> operationList = sqlManager.getOperationList(sqlQuery);

        return magic(operationList, time, "erd");
    }

    public List<ScheduledTask> edd(int time) {
        String sqlQuery = "\n" +
                "SELECT o.*, j.deadline FROM \n" +
                "(\n" +
                "SELECT * FROM Operation WHERE eddFinished = false\n" +
                " ) as o\n" +
                " INNER JOIN\n" +
                "  (\n" +
                "  SELECT id, deadline FROM Job WHERE isAvailable_edd = true and arrival_time <="+time +"\n" +
                "  ) as j on j.id = o.JobID\n" +
                "  where o.eddFinished = false\n" +
                "  order by deadline";

        List<Operation> operationList = sqlManager.getOperationList(sqlQuery);

        return magic(operationList, time, "edd");
    }

        public List<ScheduledTask> sss(int time) {
            String sqlQuery = "SELECT o.*,  (j.deadline - "+ time + " -o.rrt) as SSS FROM (\n" +
                    " \n" +
                    "SELECT id, jobid,name, run_time,type, sssFinished,  op.rest_runtime as rrt from Operation \n" +
                    "inner join(\n" +
                    "SELECT jobid as jid, sum(run_time) as rest_runtime from Operation group by jobid) \n" +
                    "AS OP\n" +
                    "on op.jid = operation.JobID\n" +
                    ") AS o\n" +
                    "inner join\n" +
                    " (\n" +
                    " SELECT * from Job where arrival_time  <= "+time +" and isAvailable_sss = true\n" +
                    " ) as j\n" +
                    " on j.id = o.JobID\n" +
                    " WHERE sssFinished = false  order by SSS, id";
            List<Operation> operationList =sqlManager.getOperationList(sqlQuery);

            return   magic(operationList, time, "sss");
    }

    public List<ScheduledTask> cr(int time) {
        String sqlQuery = "SELECT o.*,  ((cast(j.deadline as double) - " +time  +" ) / o.rrt)  as cr FROM (\n" +
                " \n" +
                "SELECT id, jobid,name, run_time,type, crFinished,  op.rest_runtime as rrt from Operation \n" +
                "inner join(\n" +
                "SELECT jobid as jid, sum(run_time) as rest_runtime from Operation group by jobid) \n" +
                "AS OP\n" +
                "on op.jid = operation.JobID\n" +
                ") AS o\n" +
                "inner join\n" +
                " (\n" +
                " SELECT * from Job where arrival_time  <= " +time  +"  and isAvailable_cr = true\n" +
                " ) as j\n" +
                " on j.id = o.JobID\n" +
                " WHERE crFinished = false\n" +
                " order by cr, id";
        List<Operation> operationList =sqlManager.getOperationList(sqlQuery);

        return   magic(operationList, time, "cr");
    }

    // Here happened the magic :)
    private List<ScheduledTask> magic(List<Operation> operationList, int time, String rule) {
        List<ScheduledTask> scheduledTaskList = new ArrayList<>();
        Resources resources;

        int jobid = 0;
        for (Operation o : operationList) {
            if (jobid != o.getJobID()) {
                System.out.println("Operation:" + o);
                jobid = o.getJobID();

                resources = sqlManager.findNeededResource(o.getOpType(), rule);
                if (resources != null) {
                    setResource( resources, rule);
                    sqlManager.insertSelectedOperationInResultTable(rule,o,time);
                    sqlManager.setJobActive(o.getJobID(), false, rule);
                    scheduledTaskList.add(new ScheduledTask(o.getJobID(), o.getId(), resources.getId(), o.getRunTime() + time));
                }
            }
        }
        return scheduledTaskList;
    }

    private void setResource(Resources r, String rule) {
        sqlManager.setResourceUsed(r.getId(),rule );
    }

}
