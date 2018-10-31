package scheduler.business_logic;

import scheduler.dbModels.Job;
import scheduler.dbModels.Operation;
import scheduler.util.SQLManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScedulingRules {

    private SQLManager sqlManager;

    public ScedulingRules() {

        sqlManager = new SQLManager("db");
    }

    public List<Job> spt() {

        List<Job> jobList = sqlManager.downloadAllJob();
        List<Job> sptList = new ArrayList<>();

        jobList.sort((new Job.StartTimeComparator()));
        for (int i = 0; i < jobList.size(); i++) {

        }

        return jobList;
    }

    private List<Job> lpt() {
        List<Job> jobList = sqlManager.downloadAllJob();
        jobList.sort(new Job.RunTimeComparator().reversed());
        for (Job j : jobList) {
            System.out.println(j.toString());
        }
        return jobList;
    }

    private List<Job> edd() {
        List<Job> jobList = sqlManager.downloadAllJob();
        jobList.sort(new Job.StartTimeComparator());
        for (Job j : jobList) {
            System.out.println(j.toString());
        }
        return jobList;
    }

    private List<Job> erd() {
        List<Job> jobList = sqlManager.downloadAllJob();
        jobList.sort(new Job.EndTimeComparator());
        for (Job j : jobList) {
            System.out.println(j.toString());
        }
        return jobList;
    }

    private List<Job> SSS() {
        List<Job> jobList = sqlManager.downloadAllJob();
        jobList.sort(new Job.SSSComparator());
        for (Job j : jobList) {
            System.out.println(j.toString());
        }
        return jobList;
    }

    private List<Job> CR() {
        List<Job> jobList = sqlManager.downloadAllJob();
        jobList.sort(new Job.SSSComparator());
        for (Job j : jobList) {
            System.out.println(j.toString());
        }
        return jobList;
    }


}
