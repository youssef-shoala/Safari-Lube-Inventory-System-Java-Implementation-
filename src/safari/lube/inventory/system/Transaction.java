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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 *
 * @author youss
 */
public class Transaction {
    private final int productID;
    private final float priceSold; 
    private final Timestamp timestamp; 
    private final String seller; 
    private final String location; 
    private final int quantity;
    private final float profit;

    public Transaction(int productID, float priceSold, Timestamp timestamp, String seller, String location, int quantity, float profit) {
        this.productID = productID;
        this.priceSold = priceSold;
        this.timestamp = timestamp;
        this.seller = seller;
        this.location = location; 
        this.quantity = quantity;
        this.profit = profit;
    }
    
    private static Connection connection;
    private static PreparedStatement addTransaction; 
    private static PreparedStatement removeTransaction;
    private static PreparedStatement getMonthsProfit; 
    private static ResultSet resultSet; 

    public static Transaction addTransaction(int productID, String cls, String type, float priceSold, String seller, String location, int quantity){
        connection = DBConnection.getConnection();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        Transaction transaction = null; 
        try{
            ArrayList<Product> productsRemoved = Product.removeProductFromInventory(cls, type, location, quantity);
            float profit = 0;
            
            if (productsRemoved.size() != 1){
                for(int i=0; i<productsRemoved.size(); i++){
                    System.out.println(productsRemoved.get(i).getPriceBought());
                    System.out.println(productsRemoved.get(i).getQuantity());
                    float profitOfIndex = (priceSold - productsRemoved.get(i).getPriceBought())* productsRemoved.get(i).getQuantity(); 
                    profit += profitOfIndex;
                }
            }
            else{
                profit = (priceSold - productsRemoved.get(0).getPriceBought())*quantity;
            }
                        
            addTransaction = connection.prepareStatement("insert into transactions values (?,?,?,?,?,?,?)");
            addTransaction.setInt(1, productID);
            addTransaction.setFloat(2, priceSold);
            addTransaction.setTimestamp(3, currentTimestamp); 
            addTransaction.setString(4, seller); 
            addTransaction.setString(5,location);
            addTransaction.setInt(6, quantity);
            addTransaction.setFloat(7, profit);
            addTransaction.executeUpdate();
            transaction = new Transaction(productID, priceSold, currentTimestamp, seller, location, quantity, profit);
            
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        return transaction; 
    }
    
    public static void removeTransaction(int productID){
        connection = DBConnection.getConnection(); 
        try{
            removeTransaction = connection.prepareStatement("remove from transactions where product_ID = ?");
            removeTransaction.setInt(1, productID);
            removeTransaction.executeQuery();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
    }
    
    public static float getMonthsGrossProfit(String month){
        connection = DBConnection.getConnection(); 
        float profit = 0; 
        String[] monthsArray = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayList<String> monthsArrayList = new ArrayList<>();
        Collections.addAll(monthsArrayList, monthsArray);        
        
        try{
            getMonthsProfit = connection.prepareStatement("select Timestamp, Profit from transactions");
            resultSet = getMonthsProfit.executeQuery(); 
            
            while(resultSet.next()){
                if (resultSet.getTimestamp(1).getMonth() == monthsArrayList.indexOf(month)){
                    profit += resultSet.getLong(2);
                }
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return profit;
    }
    
    public static float getMonthsNetProfit(String month){
        connection = DBConnection.getConnection(); 
        float profit = 0; 
        String[] monthsArray = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayList<String> monthsArrayList = new ArrayList<>();
        Collections.addAll(monthsArrayList, monthsArray);        
        
        try{
            getMonthsProfit = connection.prepareStatement("select Timestamp, Profit from transactions");
            resultSet = getMonthsProfit.executeQuery(); 
            
            while(resultSet.next()){
                if (resultSet.getTimestamp(1).getMonth() == monthsArrayList.indexOf(month)){
                    profit += resultSet.getLong(2);
                }
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        ArrayList<Expenses> expenses = Expenses.getAllExpensesByMonth(month);
        float totalExpensesAmount=0; 
        for(int i=0; i<expenses.size(); i++){
            totalExpensesAmount += expenses.get(i).getAmount();
        }
        profit -= totalExpensesAmount;
        
        return profit;
    }
    
    public float getPriceSold() {
        return priceSold;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getSeller() {
        return seller;
    }

    public int getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }
}
