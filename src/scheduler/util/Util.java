package scheduler.util;

import java.util.*;

import scheduler.dbModels.*;

public class Util {


    public  List<Job> generateJobs(int numberOfJobs) {
        List<Job> jobList = new ArrayList<>();

        Random random = new Random();

        for (int i = 1; i <= numberOfJobs; i++) {
            String name = "Job_" + i;
            int arrival_time = random.nextInt(40) + 0;

            Job temp = new Job(name, arrival_time);

            jobList.add(temp);
        }
        return  jobList;
    }

    public List<Operation> generateOperations(int numberOfJobs) {
        List<Operation> operationList = new ArrayList<>();

        Random random = new Random();

        String prev_op_type = "";
        for (int i = 1; i <= numberOfJobs; i++) {

            int operationPerJob = random.nextInt(3) + 4;

            for (int j = 1; j <= operationPerJob; j++) {
                String operationName = "Job_" + i + "op_" + j;
                int runTime = random.nextInt(70) + 5;
                String op_type = Operation_Type.randomOp().toString();
                while(prev_op_type.equals(op_type)) {
                    op_type = Operation_Type.randomOp().toString();
                }
                if(j != operationPerJob) {
                    prev_op_type = op_type;
                }
                else {
                    prev_op_type ="";
                }
                Operation temp = new Operation(i,operationName,runTime,op_type);

                operationList.add(temp);
            }
        }

        return operationList;
    }

    public enum Operation_Type {
        A ("drilling"),
        B ("cutting"),
        C ("milling"),
        D ("grinding"),
        E ("weld");

        private final String name;
        private static final List<Operation_Type> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        Operation_Type(String s) {
            name = s;
        }

        public static Operation_Type randomOp()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }

        @Override
        public String toString() {
            return name;
        }
    }



}


