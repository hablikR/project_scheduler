package scheduler.util;

import org.sqlite.SQLiteException;
import scheduler.dbModels.OperationResult;
import scheduler.dbModels.Resources;
import scheduler.dbModels.Job;
import scheduler.dbModels.Operation;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SQLManager {
    // Connection
    private static Connection conn;
    // Local DB path
    private final String LOCAL_DB = "scheduler_db.sqlite";
    // Contains the whole path of the DB
    private String DB_PATH_INCOMMING;
    private static String DB_FULL_PATH;
    // DB path
    private final String CONNECTION_URL = "jdbc:sqlite:";
    // DB creator script location
    private final String DB_CREATOR_SCRIPT = "db/create_db.sql";

    public SQLManager(String db_path) {

        // Close former connection if already exist
        closeDB();

        conn = null;

        // Save the incomming path
        DB_PATH_INCOMMING = db_path + "/";

        // Save the full path
        DB_FULL_PATH = DB_PATH_INCOMMING + LOCAL_DB;

        this.connect();
    }

    public void finalize() {
        System.out.println("finalize...");
    }

    private Connection connect() {

        boolean foundDB = false;

        if (existDB(DB_FULL_PATH)) {
            foundDB = true;
            System.out.println("SQLiteManager - Database found...");
        } else {
            System.out.println("SQLiteManager - Database NOT found...");

        }

        try {
            conn = DriverManager.getConnection(CONNECTION_URL + DB_FULL_PATH);

            if (foundDB) {
                System.out.println("SQLiteManager - connected...");
            } else {
                System.out.println("SQLiteManager - Database created and connected...");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        // DB létrehozó szkript futtatása ha nincs db
        if (!foundDB) {
            createDatabaseFromScript(new File(DB_CREATOR_SCRIPT));
        }
        return conn;
    }

    private void createDatabaseFromScript(File inputFile) {

        System.out.println("SQLProjectManager - Running database creation script...");

        // Delimiter
        String delimiter = ";";

        // Create scanner
        Scanner scanner;
        try {
            scanner = new Scanner(inputFile);
            scanner.useDelimiter(delimiter);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }

        // Loop through the SQL file statements
        Statement currentStatement = null;
        while (scanner.hasNext()) {

            // Get statement
            String rawStatement = scanner.next() + delimiter;

            if (rawStatement.trim().length() == 1)
                continue;

            try {
                // Execute statement
                currentStatement = conn.createStatement();
                currentStatement.execute(rawStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Release resources
                if (currentStatement != null) {
                    try {
                        currentStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                currentStatement = null;
            }
        }
        scanner.close();
    }

    private static boolean existDB(String path) {
        return (new File(path).exists());
    }

    public void clearTables() {
        String jobDelete = "DELETE FROM Job";
        String operationDelete = "DELETE FROM Operation";

        Statement statement;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(operationDelete);
            statement.executeUpdate(jobDelete);
        } catch (Exception e) {
            System.out.println("Table clear methode error: " + e);
        }
    }

    public List<String> operationType() {
        String sqlString = "SELECT Operation_type from Operation_type";
        List<String> opertionList = new ArrayList<>();
        Statement statement;
        ResultSet rs;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sqlString);
            while (rs.next()) {
                String temp = rs.getString("Operation_type").trim();
                opertionList.add(temp);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Select operation List error: " + e);
        }

        return opertionList;
    }

    public List<Operation> getOperationList(String sqlQuery) {

        List<Operation> operationList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sqlQuery);
            while (rs.next()) {
                Operation temp = new Operation(
                        rs.getInt("ID"),
                        rs.getInt("JobID"),
                        rs.getString("Name"),
                        rs.getInt("run_time"),
                        rs.getString("Type")
                );

                operationList.add(temp);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Select Operation List error: " + e);
        }
        return operationList;
    }

    public void insertOperation(Operation operation) {

        String SQLString = "INSERT INTO Operation (JobID, Name, run_time, Type ) VALUES" +
                "(?,?,?,?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQLString);
            preparedStatement.setInt(1, operation.getJobID());
            preparedStatement.setString(2, operation.getOperationName());
            preparedStatement.setInt(3, operation.getRunTime());
            preparedStatement.setString(4, operation.getOpType());

            preparedStatement.executeUpdate();
            // System.out.println("insert data OK");
        } catch (SQLiteException e) {
            System.out.println("The Job is not exist" + e);
        } catch (Exception e) {
            System.out.println("Insert faild:" + e);
        }

        updateJobAfterAddnewOperation(operation.getJobID(), (operation.getRunTime()));
    }

    public List<String> downloadOperationIDList() {
        String sqlString = "SELECT ID, JobID FROM Operation order by ID";
        List<String> idList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sqlString);
            while (rs.next()) {
                int tempID = rs.getInt("ID");
                int tempjobID = rs.getInt("JobID");
                idList.add("ID: " + tempID + " (jobID: " + tempjobID + ")");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Select ID List error: " + e);
        }
        return idList;
    }

    public List<Integer> downloadJobIDList() {
        String sqlString = "SELECT ID FROM Job ORDER BY ID";
        List<Integer> idList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sqlString);
            while (rs.next()) {
                int temp = rs.getInt("ID");
                idList.add(temp);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Select ID List error: " + e);
        }
        return idList;
    }

    public void removebyOperationByID(int id) {
        String sqlString = "DELETE FROM Operation where ID = " + id;

        Statement statement;
        try {
            statement = conn.createStatement();
            statement.executeUpdate(sqlString);
        } catch (Exception e) {
            System.out.println("Delete ID error: " + e);
        }
    }

    public void inserJob(Job job) {
        String SQLString = "INSERT INTO Job (Name, arrival_time,deadline ) VALUES" +
                "(?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQLString);
            preparedStatement.setString(1, job.getName());
            preparedStatement.setInt(2, job.getArrivalTime());
            preparedStatement.setInt(3, job.getDeadline());

            preparedStatement.executeUpdate();
            //  System.out.println("insert data OK");
        } catch (SQLiteException e) {
            System.out.println("The Job name is exist" + e);
        } catch (Exception e) {
            System.out.println("Insert faild:" + e);
        }

    }

    public void deleteJob(int id) {

        String sqlString_job = "DELETE FROM Job WHERE ID = " + id;
        String sqlString_operation = "DELETE FROM Operation WHERE JobID = " + id;
        try {
            Statement statement;
            statement = conn.createStatement();
            statement.executeUpdate(sqlString_job);
            statement.executeQuery(sqlString_operation);
        } catch (Exception e) {
            System.out.println("Delete error (methode:delete job) : " + e);
        }
    }

    public List<Job> getJobList(String sqlQuery) {

        List<Job> jobList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sqlQuery);
            while (rs.next()) {
                Job temp = new Job(rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getInt("arrival_time"),
                        rs.getInt("run_time"),
                        rs.getInt("deadline"),
                        rs.getInt("end_time")
                );

                jobList.add(temp);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Select Job List error: " + e);
        }
        return jobList;
    }

    public List<Resources> getResourcesList() {
        String sqlString = "SELECT ID, Name, Operation_type FROM Resource";
        List<Resources> result = new ArrayList<>();

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while (resultSet.next()) {
                Resources temp = new Resources(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("operation_type")
                );
                result.add(temp);
            }
        } catch (Exception e) {
            System.out.println("Get resourcers list error: " + e);
        }

        return result;
    }

    public Resources findNeededResource(String type, String rule) {
        String sqlString = "SELECT ID, Total_run_time_" + rule + ", job_count_" + rule + ", name FROM Resource WHERE Operation_type = '" + type + "'"
                + " and IsAvailable_" + rule + " = true LIMIT 1";
        Resources result = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while (resultSet.next()) {
                result = new Resources(
                        resultSet.getInt("id"),
                        resultSet.getInt("Total_run_time_" + rule),
                        resultSet.getInt("job_count_" + rule),
                        true
                );
                System.out.println("id:" + resultSet.getInt("id") +
                        "name:" + resultSet.getString("name"));
            }
        } catch (Exception e) {
            System.out.println("Get resource by type error: " + e);
        }
        return result;
    }

    public Resources findNeededResourceById(int id, String rule) {
        String sqlString = "SELECT ID, Total_run_time_" + rule + ", job_count_" + rule + " FROM Resource WHERE id = '" + id + "'";

        Resources result = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while (resultSet.next()) {
                result = new Resources(
                        resultSet.getInt("id"),
                        resultSet.getInt("Total_run_time_" + rule),
                        resultSet.getInt("job_count_" + rule),
                        true
                );
            }
        } catch (Exception e) {
            System.out.println("Get resource by id error: " + e);
        }
        return result;
    }

    public void setJobActive(int jobId, boolean isAvailable, String rule) {
        String sqlString = "UPDATE Job SET  isAvailable_" + rule + "= ? WHERE ID = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
            preparedStatement.setBoolean(1, isAvailable);
            preparedStatement.setInt(2, jobId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("update job status error: " + e);
        }
    }

    public void setOperationFinished(int opId, String rule) {
        String sqlString = "UPDATE Operation SET  " + rule + "Finished= ? WHERE ID = ?";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, opId);
            preparedStatement.executeUpdate();
            System.out.println(sqlString + " ID: " + opId);
        } catch (Exception e) {
            System.out.println(" update operation status error: " + e);
        }
    }

    public void setResourceUsed(int id, String rule) {
        String sqlString = "UPDATE Resource SET IsAvailable_" + rule + "= false WHERE ID = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Resource update error: " + e);
        }
    }

    public void setResourceAvailable(int id, int jobCount, int runtime, String rule) {
        String sqlString = "UPDATE Resource SET IsAvailable_" + rule + "= true, job_count_" + rule + " = ?, Total_run_time_" + rule + " = ? WHERE ID = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
            preparedStatement.setInt(1, jobCount);
            preparedStatement.setInt(2, runtime);
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Resource update error: " + e);
        }
    }

    public int getNotFinishedOperations(String rule) {
        String sqlString = "SELECT count(*) FROM Operation WHERE " + rule + "Finished = false";
        int result = 0;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Get not finished operaion count: " + e);
        }
        return result;
    }

    public void insertSelectedOperationInResultTable(String rule, Operation operation, int time) {
        String sqlString = "INSERT INTO Result_" + rule + " ( OperationID, JobID,Name,start_time,run_time,Type)" +
                " VALUES (?,?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString);
            preparedStatement.setInt(1, operation.getId());
            preparedStatement.setInt(2, operation.getJobID());
            preparedStatement.setString(3, operation.getOperationName());
            preparedStatement.setInt(4, time);
            preparedStatement.setInt(5, operation.getRunTime());
            preparedStatement.setString(6, operation.getOpType());

            preparedStatement.executeUpdate();
            // System.out.println("insert data OK");
        } catch (SQLiteException e) {
            System.out.println("Result table insert error: " + e);
        } catch (Exception e) {
            System.out.println("Insert faild:" + e);
        }
    }

    public void resetResult() {
        String sqlString = "DELETE FROM Result_cr;" +
                "DELETE FROM Result_sss;" +
                "DELETE FROM Result_edd;" +
                "DELETE FROM Result_erd;" +
                "DELETE FROM Result_lpt;" +
                "DELETE FROM Result_spt;" +
                "DELETE FROM Result_sa;";

        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sqlString);
        } catch (Exception e) {
            System.out.println("DELETE rule error: " + e);
        }
    }

    public void resetResources() {
        String sqlString = "UPDATE Resource SET job_count_spt = 0, Total_run_time_spt = 0, IsAvailable_spt = 1, " +
                "job_count_lpt = 0, Total_run_time_lpt = 0, IsAvailable_lpt = 1, job_count_edd = 0, " +
                "Total_run_time_edd = 0,  IsAvailable_edd = 1, job_count_erd = 0, Total_run_time_erd = 0, " +
                "IsAvailable_erd = 1, job_count_sss = 0, Total_run_time_sss = 0, IsAvailable_sss = 1, job_count_cr = 0," +
                " Total_run_time_cr = 0, IsAvailable_cr = 1, job_count_sa = 0, Total_run_time_sa = 0, IsAvailable_sa = 1";
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sqlString);
        } catch (Exception e) {
            System.out.println("Reset resources data error" + e);
        }

    }

    public void resetOperations() {
        String sqlString = "UPDATE Operation SET sptFinished = 0,lptFinished = 0,erdFinished = 0,eddFinished = 0," +
                "sssFinished = 0,crFinished = 0,saFinished = 0";

        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sqlString);
        } catch (Exception e) {
            System.out.println("Reset operations data error" + e);
        }

    }

    public void resetJobs() {
        String sqlString = "UPDATE Job SET end_time = 0, isAvailable_spt = 1,isAvailable_lpt = 1,isAvailable_edd = 1, " +
                "isAvailable_erd = 1,isAvailable_sss =1 ,isAvailable_cr = 1,isAvailable_sa = 1";
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(sqlString);
        } catch (Exception e) {
            System.out.println("Reset job data error" + e);
        }

    }

    public List<OperationResult> getOperationResultList(String rule) {
        String sqlQuery = "SELECT ID, OperationID, JobID, Name, " +
                "start_time, run_time, type FROM Result_" + rule;

        List<OperationResult> result = new ArrayList<>();

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            OperationResult temp;
            while (resultSet.next()) {
                temp = new OperationResult(
                        resultSet.getInt("ID"),
                        resultSet.getInt("OperationID"),
                        resultSet.getInt("JobID"),
                        resultSet.getString("Name"),
                        resultSet.getInt("start_time"),
                        resultSet.getInt("run_time"),
                        resultSet.getString("type")
                        );

                result.add(temp);
            }
        } catch (Exception e) {
            System.out.println("Result list error by " + rule + e);
        }

        return result;

    }

    //Private methodes

    private Job selectJobByID(int id) {

        String sqlString = "SELECT * FROM Job where ID = " + id;

        Job job = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while (resultSet.next()) {
                job = new Job(
                        id,
                        resultSet.getString("Name"),
                        resultSet.getInt("arrival_time"),
                        resultSet.getInt("run_time"),
                        resultSet.getInt("deadline"),
                        resultSet.getInt("end_time")
                );
            }
        } catch (Exception e) {
            System.out.println("Select data error in update job methode: " + e);
        }

        return job;
    }

    private void updateJobAfterAddnewOperation(int id, int runTime) {

        Job job = selectJobByID(id);
        Job result = job.refresh(runTime);
        updateJobRunTime(result);
    }

    private void updateJobRunTime(Job job) {

        String sqlString = "UPDATE Job SET  run_time= ?, deadline = ? WHERE ID = ?";
        //  System.out.println(job.toString());
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlString);

            preparedStatement.setInt(1, job.getRunTime());
            preparedStatement.setInt(2, job.getDeadline());
            preparedStatement.setInt(3, job.getId());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Runtime update error: " + e);
        }
    }

    private void closeDB() {
        if (conn == null)
            return;

        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
