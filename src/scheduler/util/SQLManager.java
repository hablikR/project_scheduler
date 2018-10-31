package scheduler.util;

import org.sqlite.SQLiteException;
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

    /**
     * Adatbazis zarasa amikor a program bezarodik
     */
    public void finalize() {
        System.out.println("finalize...");
    }

    /**
     * Connect to the database
     *
     * @return the Connection object
     */
    public Connection connect() {

        boolean foundDB = false;

        if (existDB(DB_FULL_PATH) == true) {
            foundDB = true;
            System.out.println("SQLiteManager - Database found...");
        } else {
            System.out.println("SQLiteManager - Database NOT found...");

				/*if (Util.createDirectory(DB_PATH_INCOMMING) == false) {
					System.out.println("SQLiteManager - ERROR: cannot create Database!");
				}*/
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

    /**
     * Close connection
     */
    private void closeDB() {
        if (conn == null)
            return;

        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Check DB already exist
     *
     * @return
     */
    public static boolean existDB(String path) {
        if (new File(path).exists()) {
            return true;
        }
        return false;
    }


    public void createDatabaseFromScript(File inputFile) {

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

    //#region Operation
    public List<String> operationType() {
        String sqlString = "SELECT Operation_type from Operation_type";
        List<String> opertionList = new ArrayList<>();
        Statement statement;
        ResultSet rs;
        try {
            statement = this.connect().createStatement();
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

    /**
     * @param operation
     */
    public void insertOperation(Operation operation) {

        String SQLString = "INSERT INTO Operation (JobID, Name, run_time, Type ) VALUES" +
                "(?,?,?,?)";

        try {
            PreparedStatement preparedStatement = this.connect().prepareStatement(SQLString);
            preparedStatement.setInt(1, operation.getJobID());
            preparedStatement.setString(2,operation.getOperationName());
            preparedStatement.setInt(3, operation.getRunTime());
            preparedStatement.setString(4,operation.getOpType());

            preparedStatement.executeUpdate();
            System.out.println("insert data OK");
        }
        catch (SQLiteException e)
        {
            System.out.println("The Job is not exist" + e);
        }
        catch (Exception e) {
            System.out.println("Insert faild:" + e);
        }

        updateJobAfterAddnewOperation(operation.getJobID(), (operation.getRunTime()));
    }
//#endregion

    public List<String> downloadOperationIDList() {
        String sqlString = "SELECT ID, JobID FROM Operation order by ID";
        List<String> idList = new ArrayList<>();
        try {
            Statement statement = this.connect().createStatement();
            ResultSet rs = statement.executeQuery(sqlString);
            while (rs.next()) {
                int tempID = rs.getInt("ID");
                int tempjobID = rs.getInt("JobID");
                idList.add("ID: " + tempID + " (jobID: " + tempjobID +")" );
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
            Statement statement = this.connect().createStatement();
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
            statement = this.connect().createStatement();
            statement.executeUpdate(sqlString);
        } catch (Exception e) {
            System.out.println("Delete ID error: " + e);
        }

        updateJobAfterRemoveOperation(id);
    }

    //#region Job methodes

    public void inserJob(Job job){
        String SQLString = "INSERT INTO Job (Name, arrival_time,deadline ) VALUES" +
                "(?,?,?)";
        try {
            PreparedStatement preparedStatement = this.connect().prepareStatement(SQLString);
            preparedStatement.setString(1, job.getName());
            preparedStatement.setInt(2,job.getArrivalTime());
            preparedStatement.setInt(3,job.getDeadline());

            preparedStatement.executeUpdate();
            System.out.println("insert data OK");
        }
        catch (SQLiteException e)
        {
            System.out.println("The Job name is exist" + e);
        }
        catch (Exception e) {
            System.out.println("Insert faild:" + e);
        }

    }

    public void deleteJob(int id) {

        String sqlString_job = "DELETE FROM Job WHERE ID = " + id;
        String sqlString_operation = "DELETE FROM Operation WHERE JobID = " + id;
        try {
            Statement statement;
            statement = this.connect().createStatement();
            statement.executeUpdate(sqlString_job);
            statement.executeQuery(sqlString_operation);
        }
        catch (Exception e ){
            System.out.println("Delete error (methode:delete job) : " + e);
        }
    }

    private void updateJobAfterAddnewOperation(int id, int runTime) {

        Job job = selectJobByID(id);
        Job result = job.refresh(runTime);
        updateJobRunTime(result);
    }

    private void updateJobAfterRemoveOperation(int id) {

        try{

        }
        catch (Exception e){
            System.out.println("Update Job after remove operation error: " + e);
        }

    }

    private void updateJobRunTime(Job job){

        String sqlString = "UPDATE Job SET  run_time= ? WHERE ID = ?";
        System.out.println(job.toString());
        try{
            PreparedStatement preparedStatement = this.connect().prepareStatement(sqlString);

            preparedStatement.setInt(1, job.getRunTime());
            preparedStatement.setInt(2, job.getId());
            preparedStatement.executeUpdate();
        }

        catch (Exception e ){
            System.out.println("Runtime update error: " + e);
        }
    }

    private Job selectJobByID(int id){

        String sqlString ="SELECT * FROM Job where ID = " + id;

        Job job  = null;
        try{
            Statement statement = this.connect().createStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while (resultSet.next()){
                job = new Job(
                        id,
                        resultSet.getString("Name"),
                        resultSet.getInt("arrival_time"),
                        resultSet.getInt("run_time"),
                        resultSet.getInt("deadline"),
                        resultSet.getInt("end_time")
                );
            }
        }
        catch(Exception e)
        {
            System.out.println("Select data error in update job methode: " + e);
        }

        return job;

    }

    public List<Job> downloadAllJob(){
        String sqlString = "SELECT * FROM Job";
        List<Job> jobList = new ArrayList<>();
        try {
            Statement statement = this.connect().createStatement();
            ResultSet rs = statement.executeQuery(sqlString);
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

    public List<Operation> downloadAllOperation(){
        String sqlString = "SELECT * FROM Job";
        List<Operation> operationList = new ArrayList<>();
        try {
            Statement statement = this.connect().createStatement();
            ResultSet rs = statement.executeQuery(sqlString);
            while (rs.next()) {
                Operation temp = new Operation(rs.getInt("ID"),
                        rs.getInt("JobID"),
                        rs.getString("Name"),
                        rs.getInt("run_time"),
                        rs.getString("Type")
                );

                operationList.add(temp);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Select Job List error: " + e);
        }

        return operationList;
    }
    //#endregion

    public void clearTables(){
        String jobDelete = "DELETE FROM Job";
        String operationDelete = "DELETE FROM Operation";

        Statement statement;
        try {
            statement = this.connect().createStatement();
            statement.executeUpdate(operationDelete);
            statement.executeUpdate(jobDelete);
        } catch (Exception e) {
            System.out.println("Table clear methode error: " + e);
        }
    }
}
