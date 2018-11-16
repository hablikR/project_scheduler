package scheduler.dbModels;

import java.util.Comparator;
import java.util.Random;

public class Job {

    private int id;
    private String name;
    private int arrivalTime;
    private int runTime;
    private int deadline;
    private int endTime;


    public Job(String name, String arrivalTime, String deadline) {

        this.name = name;
        this.arrivalTime = Integer.parseInt(arrivalTime);
        this.deadline = Integer.parseInt(deadline);
    }


    public Job(String name, int arrivalTime) {

        this.name = name;
        this.arrivalTime = arrivalTime;

    }

    public Job(int id, String name, int arrivalTime, int deadline) {

        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.deadline = deadline;
    }

    public Job(int id, String name, int arrivalTime, int runTime, int deadline, int endTime) {

        this.id = id;
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.runTime = runTime;
        this.deadline = deadline;
        this.endTime = endTime;
    }

    public Job(Operation operation) {
        this.id = operation.getJobID();
        this.runTime = operation.getRunTime();
    }

    public Job refresh(int runTime) {
        System.out.println(runTime);
        Random random = new Random();
    int newRunTime = this.runTime + runTime;
        Job j = new Job(this.id,
                this.name,
                this.arrivalTime,
                newRunTime,
                newRunTime + random.nextInt(80)+40,
                 this.endTime
        );
        return j;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getRunTime() {
        return runTime;
    }

    public int getDeadline() {
        return deadline;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", runTime=" + runTime +
                ", deadline=" + deadline +
                ", endTime=" + endTime +
                '}';
    }
}
