package scheduler.business_logic;

public class ScheduledTask {

    private int jobId;
    private int operationId;
    private int resourcesId;
    private int runTime;

    public ScheduledTask(int jobId, int operationId, int resourcesId, int runTime){
        this.jobId = jobId;
        this.operationId = operationId;
        this.resourcesId =resourcesId;
        this.runTime = runTime;
    }

    public int getJobId() {
        return jobId;
    }

    public int getOperationId() {
        return operationId;
    }

    public int getResourcesId() {
        return resourcesId;
    }

    public int getRunTime() {
        return runTime;
    }
}
