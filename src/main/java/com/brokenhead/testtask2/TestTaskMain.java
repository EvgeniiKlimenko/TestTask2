/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brokenhead.testtask2;

/**
 *
 * @author brokenhead
 */
public class TestTaskMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DoWork dw = new DoWork(args);
        dw.parseCommand();
    }
    
}
