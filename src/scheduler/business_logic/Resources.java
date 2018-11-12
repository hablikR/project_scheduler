package scheduler.business_logic;


import com.sun.org.apache.regexp.internal.RE;

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

    public String getType() {
        return type;
    }

    public int getTotalRunTime() {
        return totalRunTime;
    }

    public void setTotalRunTime(int totalRunTime) {
        this.totalRunTime = totalRunTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getJob_count() {
        return job_count;
    }

}
