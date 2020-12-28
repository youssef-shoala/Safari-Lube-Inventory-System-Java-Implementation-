/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package safari.lube.inventory.system;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;


/**
 *
 * @author youss
 */
public class Product {
    private final String cls;
    private final String type; 
    private final int productID; 
    private final float priceBought; 
    private float priceSold; 
    private final java.sql.Timestamp dateAdded; 
    private String location; 

    public Product(String cls, String type, int productID, float priceBought, float priceSold, Timestamp dateAdded, String location) {
        this.cls = cls;
        this.type = type;
        this.productID = productID; 
        this.priceBought = priceBought;
        this.priceSold = priceSold;
        this.dateAdded = dateAdded;
        this.location = location;
    }

    
    
    private static Connection connection;
    private static PreparedStatement addProduct; 
    private static PreparedStatement removeProduct;
    private static PreparedStatement getInventory; 

    
    
    public static Product addProduct(String cls, String type, int id, float price_bought, float price_sold, String location) {
        connection = DBConnection.getConnection();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        Product product = null;

        try
        {
            addProduct = connection.prepareStatement("insert into inventory values (?,?,?,?,?,?,?)");
            addProduct.setString(1,cls);
            addProduct.setString(2,type);
            addProduct.setInt(3,id);
            addProduct.setFloat(4,price_bought);
            addProduct.setFloat(5,price_sold);
            addProduct.setTimestamp(6,currentTimestamp);
            addProduct.setString(7, location);
            addProduct.executeUpdate();
            product = new Product(cls, type, id, price_bought, price_sold,currentTimestamp, location);
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }

        return product;
    }
    
    public static void removeProduct(int id){
        connection = DBConnection.getConnection(); 
            
        try {
            removeProduct = connection.prepareStatement("remove from inventory where id = ?");
            removeProduct.setInt(1, id);
            removeProduct.executeUpdate(); 
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        
    }
    
    public static ArrayList<Product> getInventoryByDate(){
        connection = DBConnection.getConnection(); 
        ArrayList<Product> products= new ArrayList<Product>(); 
        
        try{
            getInventory = connection.prepareStatement("select * from inventory order by Date_added");
            ResultSet resultSet = getInventory.executeQuery();
            while(resultSet.next()){
                products.add(new Product(resultSet.getString("Class"), 
                                             resultSet.getString("Type"), 
                                             resultSet.getInt("ID"), 
                                             resultSet.getFloat("Price_Sold"),
                                             resultSet.getFloat("Price_Bought"),
                                             resultSet.getTimestamp("Date_Added"), 
                                             resultSet.getString("Location")));
            }
            
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace(); 
        }
        return products;
    }
    
    public static boolean checkInventory(int productID){
        ArrayList<Product> inventory = getInventoryByDate();
        boolean inInventory = false;
        for(int i=0; i<inventory.size(); i++){
            if(productID == inventory.get(i).getProductID()){
                //if product were looking for is in inventory return true and break
            }
            else{
                //return false
            }
        }
        return inInventory;
        
    }
    
    
    
    public String getLocation() {
        return location;
    }
    
    public void setPriceSold(int price) {
        this.priceSold = price; 
    }
    
    public int getProductID() {
        return productID;
    }

    public float getPriceBought() {
        return priceBought;
    }

    public float getPriceSold() {
        return priceSold;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public String getCls() {
        return cls;
    }

    public String getType() {
        return type;
    }
    
}
