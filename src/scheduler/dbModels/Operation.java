package scheduler.dbModels;

public class Operation {

    private int id;
    private int jobID;
    private String operationName;
    private int runTime;

    private String opType;


    public Operation(int jobID, String operationName, int runTime, String opType) {

        this.jobID = jobID;
        this.operationName = operationName;
        this.runTime = runTime;
        this.opType = opType;
    }


    public Operation(String jobID, String operationName, String runTime, String opType) throws  NumberFormatException {

        this.jobID = Integer.parseInt(jobID);
        this.operationName = operationName;
        this.runTime = Integer.parseInt(runTime);
        this.opType = opType;

    }

    public Operation(int id, int jobID, String name, int runTime, String type){
        this.id = id;
        this.jobID = jobID;
        this.operationName = name;
        this.runTime = runTime;
        this.opType = type;
    }

    public int getId() {
        return id;
    }

    public int getJobID() {
        return jobID;
    }

    public int getRunTime() {
        return runTime;
    }

    public String getOpType() {
        return opType;
    }

    public String getOperationName() {
        return operationName;
    }

}