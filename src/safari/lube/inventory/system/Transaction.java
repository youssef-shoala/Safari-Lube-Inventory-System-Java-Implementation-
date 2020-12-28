/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package safari.lube.inventory.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author youss
 */
public class Transaction {
    private final int productID;
    private final int priceSold; 
    private final Timestamp timestamp; 
    private final String seller; 
    private final String location; 

    public Transaction(int productID, int priceSold, Timestamp timestamp, String seller, String location) {
        this.productID = productID;
        this.priceSold = priceSold;
        this.timestamp = timestamp;
        this.seller = seller;
        this.location = location; 
    }
    
    private static Connection connection;
    private static PreparedStatement addTransaction; 
    private static PreparedStatement removeTransaction;

    public static Transaction addTransaction(int productID, int priceSold, Timestamp timestamp, String seller, String location){
        connection = DBConnection.getConnection(); 
        Transaction transaction = null; 
        try{
            addTransaction = connection.prepareStatement("intsert into transactions values (?,?,?,?,?)");
            addTransaction.setInt(1, productID);
            addTransaction.setInt(2, priceSold);
            addTransaction.setTimestamp(3, timestamp); 
            addTransaction.setString(4, seller);
            addTransaction.setString(4,location);
            addTransaction.executeQuery();
            transaction = new Transaction(productID, priceSold, timestamp, seller, location);
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
    
    public int getPriceSold() {
        return priceSold;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getSeller() {
        return seller;
    }
    
    
    
}
