package scheduler.dbModels;


public class Resources {

    private int id;
    private String name;
    private String type;
    private boolean isAvailable;
    private double avg_usage;
    private int job_count;


    private int totalRunTime;

    public Resources(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Resources(int id, int totalRunTime, int job_count, boolean isAvailable) {
        this.id = id;
        this.totalRunTime = totalRunTime;
        this.isAvailable = isAvailable;
        this.job_count = job_count;
    }

    public int getId() {
        return id;
    }

    public int getTotalRunTime() {
        return totalRunTime;
    }

    public int getJob_count() {
        return job_count;
    }
}
