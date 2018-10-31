package scheduler.dbModels;

import java.util.Comparator;

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

        Job j = new Job(this.id,
                this.name,
                this.arrivalTime,
                this.runTime + runTime,
                this.deadline, this.endTime
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

    public int getEndTime() {
        return endTime;
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

    public static class RunTimeComparator implements Comparator<Job> {

        public int compare(Job a, Job b) {
            if (b.runTime < a.runTime)
                return 1;
            if (a.runTime < b.runTime)
                return -1;
            return 0;
        }
    }

    public static class StartTimeComparator implements Comparator<Job> {

        public int compare(Job a, Job b) {
            if (b.arrivalTime < a.arrivalTime)
                return 1;
            if (a.arrivalTime < b.arrivalTime)
                return -1;
            return 0;
        }
    }

    public static class EndTimeComparator implements Comparator<Job> {

        public int compare(Job a, Job b) {
            if (b.endTime < a.endTime)
                return 1;
            if (a.endTime < b.endTime)
                return -1;
            return 0;
        }
    }

    public static class SSSComparator implements Comparator<Job> {

        public int compare(Job a, Job b) {
            if (b.deadline-b.arrivalTime- b.runTime < a.deadline-a.arrivalTime- a.runTime)
                return 1;
            if ( a.deadline-a.arrivalTime- a.runTime < b.deadline-b.arrivalTime- b.runTime)
                return -1;
            return 0;
        }
    }

    public static class CRComparator implements Comparator<Job> {

        public int compare(Job a, Job b) {
            if (b.deadline-b.arrivalTime- b.runTime < a.deadline-a.arrivalTime- a.runTime)
                return 1;
            if ( a.deadline-a.arrivalTime- a.runTime < b.deadline-b.arrivalTime- b.runTime)
                return -1;
            return 0;
        }
    }
}
