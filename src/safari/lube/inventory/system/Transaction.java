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
import java.util.Calendar;

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

    public Transaction(int productID, float priceSold, Timestamp timestamp, String seller, String location, int quantity) {
        this.productID = productID;
        this.priceSold = priceSold;
        this.timestamp = timestamp;
        this.seller = seller;
        this.location = location; 
        this.quantity = quantity;
    }
    
    private static Connection connection;
    private static PreparedStatement addTransaction; 
    private static PreparedStatement removeTransaction;

    public static Transaction addTransaction(int productID, String cls, String type, float priceSold, String seller, String location, int quantity){
        connection = DBConnection.getConnection();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        Transaction transaction = null; 
        try{
            addTransaction = connection.prepareStatement("insert into transactions values (?,?,?,?,?,?)");
            addTransaction.setInt(1, productID);
            addTransaction.setFloat(2, priceSold);
            addTransaction.setTimestamp(3, currentTimestamp); 
            addTransaction.setString(4, seller); 
            addTransaction.setString(5,location);
            addTransaction.setInt(6, quantity);
            addTransaction.executeUpdate();
            transaction = new Transaction(productID, priceSold, currentTimestamp, seller, location, quantity);
            Product.removeProductFromInventory(cls, type, location, quantity);
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
