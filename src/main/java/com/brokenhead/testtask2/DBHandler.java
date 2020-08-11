/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brokenhead.testtask2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brokenhead
 */
public class DBHandler {

    private static int rowsAffected;
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/Test1DB?useUnicode=true&useJDBCCompliantTimezoneShift=true&"
            + "useLegacyDatetimeCode=false&serverTimezone=UTC&rewriteBatchedStatements=true";
    private static final String USER = "root";
    private static final String PASS = "3107892Mysql";
    private Connection conn = null;
    private Statement stmt = null;
    private String dropTable;
    private String createTableUser;
    private String selectMaleF;
    private String selectNameDateUnique;
    private PreparedStatement insertUserPS;

    //private PreparedStatement selectMaleFPS = null;
    public DBHandler() {
        String insertUser = "INSERT INTO Test1DB.UserTable (lastname, firstname, patronname, dob, gender) "
                + "VALUES (?, ?, ?, ?, ?)";
        dropTable = "DROP TABLE IF EXISTS UserTable;";
        createTableUser = "CREATE TABLE UserTable "
                + "(id INTEGER not NULL AUTO_INCREMENT, "
                + " lastname VARCHAR(20), "
                + " firstname VARCHAR(20), "
                + " patronname VARCHAR(20), "
                + " dob VARCHAR(30), "
                + " gender VARCHAR(8), "
                + " PRIMARY KEY ( id ));";
        selectNameDateUnique = "SELECT DISTINCT lastname, firstname, patronname, dob, gender FROM Test1DB.UserTable "
                + "ORDER BY lastname, firstname, patronname;";
        selectMaleF = "SELECT lastname, firstname, patronname, dob, gender "
                + "FROM Test1DB.UserTable WHERE lastname LIKE 'F%' AND gender = 'male';";
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            insertUserPS = conn.prepareStatement(insertUser);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-----> Error in  DBHandler constructor!");
        }
    }

    public void createTable() {
        try {
            stmt.executeUpdate(dropTable);
            stmt.executeUpdate(createTableUser);
        } catch (SQLException ex) {
            System.out.println("Error: create table.");
        }
    }

    public void createSingleUser(String last, String first, String patron, String dob, String gender) {
        try {
            //LastName, FirstName, Patronomic. ORDER!!!
            insertUserPS.setString(1, last);
            insertUserPS.setString(2, first);
            insertUserPS.setString(3, patron);
            insertUserPS.setString(4, dob);
            insertUserPS.setString(5, gender);
            rowsAffected = insertUserPS.executeUpdate();
            if (rowsAffected != 1) {
                throw new SQLException();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createUserWithBatch(String last, String first, String patron, String dob, String gender) {
        try {
            insertUserPS.setString(1, last);
            insertUserPS.setString(2, first);
            insertUserPS.setString(3, patron);
            insertUserPS.setString(4, dob);
            insertUserPS.setString(5, gender);
            insertUserPS.addBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void makeExecuteBatch() {
        try {
            insertUserPS.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public LinkedList<String> selectFNameAndMale() {
        LinkedList<String> usersList = new LinkedList<String>();
        Calendar now = Calendar.getInstance();
        try {
            ResultSet result = stmt.executeQuery(selectMaleF);
            
            while (result.next()) {
                StringBuilder strb = new StringBuilder();
                String last = result.getString("lastname");
                String first = result.getString("firstname");
                String patron = result.getString("patronname");
                String dob = result.getString("dob");
                String gender = result.getString("gender");
                strb.append(last).append(" ").append(first).append(" ").append(patron).append(". ")
                        .append(dob).append(", ").append(gender).append(".");
                usersList.add(strb.toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        Calendar future = Calendar.getInstance();
        long timeCheck = future.getTimeInMillis() - now.getTimeInMillis();
        System.out.println("Time in millis of selection male users with F% in lastname: " + (timeCheck));
        return usersList;
    }    
    

    public LinkedList<String> getNameDateUnique() {
        LinkedList<String> usersList = new LinkedList<String>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate nowDate = LocalDate.now();
        LocalDate userBirth;
        Period period;
        try {
            ResultSet result = stmt.executeQuery(selectNameDateUnique);
            while (result.next()) {
                StringBuilder strb = new StringBuilder();
                String last = result.getString("lastname");
                String first = result.getString("firstname");
                String patron = result.getString("patronname");
                String dob = result.getString("dob");
                String gender = result.getString("gender");
                strb.append(first).append(" ").append(last).append(" ").append(patron).append(". ")
                        .append(dob).append(", ").append(gender).append(".");
                
                userBirth = LocalDate.parse(dob, formatter);
                period = Period.between(userBirth, nowDate);
                strb.append(" Full years: ").append(period.getYears()).append("\n");
                usersList.add(strb.toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return usersList;
    }
    
    

}
