/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package safari.lube.inventory.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author youss
 */
public class Expenses {
    private final String name; 
    private final float amount; 
    private final String month; 

    public Expenses(String name, float amount, String month) {
        this.name = name;
        this.amount = amount;
        this.month = month;
    }
    
    private static Connection connection;
    private static PreparedStatement addExpense; 
    private static PreparedStatement removeExpense; 
    private static PreparedStatement getAllExpenses; 
    private static ResultSet resultSet; 
    
    public static Expenses addExpense(String name, float amount, String month){
        connection = DBConnection.getConnection();
        Expenses expenseAdded = new Expenses(name, amount, month);;
        try{
            
            addExpense = connection.prepareStatement("insert into expenses values (?,?,?)");
            addExpense.setString(1, name);
            addExpense.setFloat(2, amount); 
            addExpense.setString(3, month);
            addExpense.executeUpdate();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return expenseAdded; 
    }
    
    public static void removeExpense(String name){
        //Expenses expense = new Expenses("", 23, "january");
        connection = DBConnection.getConnection();
        try{
            
            removeExpense = connection.prepareStatement("delete from expenses where name = ?");
            removeExpense.setString(1, name);
            removeExpense.executeUpdate();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
    }
    
    public static ArrayList<Expenses> getAllExpensesByMonth(String month){
        connection = DBConnection.getConnection();
        ArrayList<Expenses> allExpenses = new ArrayList<>(); 
        try{
            getAllExpenses = connection.prepareStatement("select * from expenses where Month = ?"); 
            getAllExpenses.setString(1, month);
            resultSet = getAllExpenses.executeQuery();
            
            while(resultSet.next()){
                allExpenses.add(new Expenses(resultSet.getString(1), resultSet.getFloat(2), resultSet.getString(3)));
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return allExpenses;
    }
    

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }

    public String getMonth() {
        return month;
    }
    
    
}
