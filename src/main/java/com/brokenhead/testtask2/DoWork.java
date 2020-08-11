/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brokenhead.testtask2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author brokenhead
 */
public class DoWork {

    private static DBHandler dbh;
    private String[] args;
    private final static String OUTPUT_FILE_PATH = "TestTask1Out.txt";
    private final static HashMap<Integer, Character> symbolsMap = new HashMap<Integer, Character>();

    public DoWork(String[] args) {
        dbh = new DBHandler();
        this.args = args;
        symbolsMap.put(0, 'a');
        symbolsMap.put(1, 'b');
        symbolsMap.put(2, 'c');
        symbolsMap.put(3, 'd');
        symbolsMap.put(4, 'e');
        symbolsMap.put(5, 'f');
        symbolsMap.put(6, 'g');
        symbolsMap.put(7, 'h');
        symbolsMap.put(8, 'i');
        symbolsMap.put(9, 'j');
        symbolsMap.put(10, 'k');
        symbolsMap.put(11, 'l');
        symbolsMap.put(12, 'm');
        symbolsMap.put(13, 'n');
        symbolsMap.put(14, 'o');
        symbolsMap.put(15, 'p');
        symbolsMap.put(16, 'q');
        symbolsMap.put(17, 'r');
        symbolsMap.put(18, 's');
        symbolsMap.put(19, 't');
        symbolsMap.put(20, 'u');
        symbolsMap.put(21, 'v');
        symbolsMap.put(22, 'w');
        symbolsMap.put(23, 'x');
        symbolsMap.put(24, 'y');
        symbolsMap.put(25, 'z');
    }

    public void parseCommand() {
        int commandNumber = Integer.parseInt(args[0]);
        switch (commandNumber) {
            case 1:
                dbh.createTable();
                break;
            case 2:
                if (args.length < 4) {
                    System.out.println("Not all the data entered. Try: \"first last patron\" date of birth gender");
                    return;
                } else {
                    //LastName, FirstName, Patronomic. ORDER!!!
                    String[] splittedName = args[1].split(" ");
                    dbh.createSingleUser(splittedName[0], splittedName[1], splittedName[2], args[2], args[3]);
                }
                break;
            case 3:
                LinkedList<String> users = dbh.getNameDateUnique();
                writeAnswerToFile(users);
                break;
            case 4:
                fillUpTable();
                break;
            case 5:
                LinkedList<String> usersList = dbh.selectFNameAndMale();
                writeAnswerToFile(usersList);              
                break;
        }
    }

    private void fillUpTable() {
        Calendar now = Calendar.getInstance();
        Random rnd = new Random();
        int counter = 0;
        int fCounter = 0;
        int boundary = 1000000;
        while (counter < boundary) {
            String patron = generateName(true, false);
            String dob = generateDateOfBirth();
            String first = generateName(false, false);
            String gender = null;
            String last = null;
            if (fCounter < 100) { // with F only male
                last = generateName(false, true);
                gender = "male";
                fCounter++;
            } else {
                last = generateName(false, false);
                boolean genderGen = rnd.nextBoolean();
                if (genderGen) {
                    gender = "male";
                } else {
                    gender = "female";
                }
            }
            dbh.createUserWithBatch(last, first, patron, dob, gender);
            counter++;
            if (counter % 1000 == 0 || counter == boundary) {
                dbh.makeExecuteBatch();
            }
        }
        Calendar future = Calendar.getInstance();
        long timeCheck = future.getTimeInMillis() - now.getTimeInMillis();
        System.out.println("Time in seconds of insertion of million users: " + (timeCheck)/1000);
    }

    private String generateName(boolean isPatron, boolean isWithF) {
        StringBuilder strb = new StringBuilder();
        int counter = 0;
        String firstChar;
        Random rnd = new Random();
        int nameSize = rnd.nextInt(17) + 3;
        while (counter++ < nameSize) {
            if (counter == 1 && isWithF) {
                strb.append("F");
                continue;
            }
            if (counter == 1) {
                firstChar = symbolsMap.get(rnd.nextInt(26)).toString();
                strb.append(firstChar.toUpperCase());
                continue;
            }
            strb.append(symbolsMap.get(rnd.nextInt(26)));
        }
        return strb.toString();
    }

    private String generateDateOfBirth() {
        //Format: dd.MM.yyyy
        StringBuilder strb = new StringBuilder();
        Random rnd = new Random();
        int day = rnd.nextInt(30) + 1;  //avoid zeroes
        if(day<10) {
            strb.append(0).append(day).append(".");   // day
        } else {
            strb.append(day).append(".");
        }
        int month = rnd.nextInt(12) + 1; // avoiding zeroes!
        if (month < 10) {
            strb.append(0).append(month).append(".");   // month. need zero before 1 or 7
        } else {
            strb.append(month).append(".");   // year
        }
        strb.append(rnd.nextInt(120) + 1900);
        return strb.toString();
    }

    private void writeAnswerToFile(LinkedList<String> users) {
        File resultFile = new File(OUTPUT_FILE_PATH);
        try (PrintWriter writer = new PrintWriter(resultFile, "UTF-8")) {
            for (String user : users) {
                writer.println(user);
            }
        } catch (IOException iox) {
            System.out.println("IOException on write!");
        }
    }
}
