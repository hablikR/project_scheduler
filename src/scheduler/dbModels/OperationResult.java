package scheduler.dbModels;

public class OperationResult {

    private int id;
    private int operationID;
    private int jobID;
    private String name;
    private int startTime;
    private int runTime;
    private int endTime;
    private String type;

    public OperationResult(int id, int operationID, int jobID, String name, int startTime, int runTime, String type){
        this.id = id;
        this.operationID = operationID;
        this.name = name;
        this.startTime = startTime;
        this.runTime = runTime;
        this.endTime = startTime + runTime;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getRunTime() {
        return runTime;
    }
}
