package scheduler.main;


import javafx.application.Platform;
import javafx.concurrent.Task;
import scheduler.business_logic.Resources;
import scheduler.business_logic.ScedulingRules;
import scheduler.dbModels.Operation;
import scheduler.gui.GUIController;
import scheduler.util.PublicVariables;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static scheduler.main.SchedulerContoller.sqlManager;


public class MultiThread {


    private Operation operation;
    private Resources resources;
    private GUIController guiController;

    public MultiThread(Operation o, Resources r) {
        this.operation = o;
        this.resources = r;
    }


    public synchronized Task<Void> createTask() {
        Task<Void> t = new Task<Void>() {
            int i = 1;
            int temp = 0;

            @Override
            public Void call() {

                while (i <= operation.getRunTime()) {
                    //  for (i=1; i<=operation.getRunTime(); i++) {
                    if (isCancelled()) {
                        sqlManager.setJobActive(operation.getJobID(), true);
                        sqlManager.setOperationFinished(operation.getId());
                        int increaseJobCountOnResource = resources.getJob_count();
                        increaseJobCountOnResource++;
                        sqlManager.setResourceAvailable(resources.getId(), resources.getTotalRunTime() + operation.getRunTime(), increaseJobCountOnResource);
                    }

                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        //  int i = 1;
                        ScedulingRules scedulingRules = new ScedulingRules();

                        public void run() {
                            if (true) { //kilépési feltéltel, ha elfogytak a jobok.
                                List<Operation> sptJobList = scedulingRules.spt_srpt(temp);
                                System.out.println(temp);
                                temp++;
                            }
                            if (i <= operation.getRunTime()) {
                                System.out.println("o.name: " + operation.getOperationName() + "o.runtime: " +
                                        " " + operation.getRunTime() + " time: " + i);
                                i++;
                            } else
                                timer.cancel();
                        }
                    }, PublicVariables.TimerDelay, PublicVariables.TimerPeriod);
                    updateProgress(i, operation.getRunTime());
                }
                return null;
            }
        };
        return t;
    }

//    meghívása bármelyik
//    osztályban bárhol:
//
//    Osztalyneve processor = new Osztalyneve();
//        processor.start();

}



/*
meghívása bármelyik osztályban bárhol:
Osztalyneve processor = new Osztalyneve();
processor.start();
*/
