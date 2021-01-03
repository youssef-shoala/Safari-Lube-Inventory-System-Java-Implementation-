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
    private int quantity; 

    public Product(String cls, String type, int productID, float priceBought, float priceSold, Timestamp dateAdded, String location, int quantity) {
        this.cls = cls;
        this.type = type;
        this.productID = productID; 
        this.priceBought = priceBought;
        this.priceSold = priceSold;
        this.dateAdded = dateAdded;
        this.location = location;
        this.quantity = quantity; 
    }

    
    
    private static Connection connection;
    private static PreparedStatement addProduct; 
    private static PreparedStatement removeProduct;
    private static PreparedStatement getInventory; 

    
    
    public static Product addProductToInventory(String cls, String type, int id, float price_bought, float price_sold, String location, int quantity) {
        connection = DBConnection.getConnection();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        Product product = null;

        try
        {
            ArrayList<Product> getInventoryOfType = getInventoryOfType(type);
            if(getInventoryOfType.size() == 0){
                addProduct = connection.prepareStatement("insert into inventory values (?,?,?,?,?,?,?,?)");
                addProduct.setString(1,cls);
                addProduct.setString(2,type);
                addProduct.setInt(3,id);
                addProduct.setFloat(4,price_sold);
                addProduct.setFloat(5,price_bought);
                addProduct.setTimestamp(6,currentTimestamp);
                addProduct.setString(7, location);
                addProduct.setInt(8,quantity);
                addProduct.executeUpdate();
                product = new Product(cls, type, id, price_bought, price_sold,currentTimestamp, location, quantity);
            }
            else{
                boolean added = false;
                for(int i=0; i<getInventoryOfType.size(); i++){
                    if(price_bought == getInventoryOfType.get(i).getPriceBought() && location.equals(getInventoryOfType.get(i).getLocation())){
                        addProduct = connection.prepareStatement("update inventory set quantity = quantity+? where type = ? and price_bought = ? and location = ?");
                        addProduct.setInt(1, quantity);
                        addProduct.setString(2, type);
                        addProduct.setFloat(3, price_bought);
                        addProduct.setString(4, location);
                        addProduct.executeUpdate(); 
                        added = true;
                        //create product of previously existing item with new quantity
                        break;
                    }
                }
                if(!added){
                    addProduct = connection.prepareStatement("insert into inventory values (?,?,?,?,?,?,?,?)");
                    addProduct.setString(1,cls);
                    addProduct.setString(2,type);
                    addProduct.setInt(3,id);
                    addProduct.setFloat(4,price_sold);
                    addProduct.setFloat(5,price_bought);
                    addProduct.setTimestamp(6,currentTimestamp);
                    addProduct.setString(7, location);
                    addProduct.setInt(8,quantity);
                    addProduct.executeUpdate();
                    product = new Product(cls, type, id, price_bought, price_sold,currentTimestamp, location, quantity);
                }
            }

            
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }

        return product;
    }
    
    public static ArrayList<Product> removeProductFromInventory(String cls, String type, String location, int quantity){
        connection = DBConnection.getConnection(); 
        ArrayList<Product> inventoryByDate = getInventoryByDate();
        ArrayList<Product> productsRemoved = new ArrayList<>();
        Product productToRemove = null; 
        
        for(int i=0; i<inventoryByDate.size(); i++){
            if(cls.equals(inventoryByDate.get(i).getCls()) && type.equals(inventoryByDate.get(i).getType()) && location.equals(inventoryByDate.get(i).getLocation())){
                productToRemove = inventoryByDate.get(i);
                break;
            }
        }   
        
        if(productToRemove != null){
            
            int quantityInInventory = productToRemove.getQuantity();
            int idOfProduct = productToRemove.getProductID();
            
            if(quantityInInventory > quantity){
                try{
                    removeProduct = connection.prepareStatement("update inventory set quantity = quantity - ? where ID = ? ");
                    removeProduct.setInt(1, quantity); 
                    removeProduct.setInt(2, idOfProduct);
                    removeProduct.executeUpdate(); 
                }
                catch(SQLException sqlException)
                {
                    sqlException.printStackTrace();
                }
                productToRemove.setQuantity(quantity);
                productsRemoved.add(productToRemove);
            }

            else if(quantityInInventory <= quantity){
                try{
                    removeProduct = connection.prepareStatement("delete from inventory where id = ?");
                    removeProduct.setInt(1, idOfProduct);
                    removeProduct.executeUpdate(); 
                }
                catch(SQLException sqlException)
                {
                    sqlException.printStackTrace();
                }
                productsRemoved.add(productToRemove);
                productsRemoved.addAll(removeProductFromInventory( cls,  type,  location,  quantity-quantityInInventory));
            }
        }
        else{
                System.out.println("This doesnt exist in inventory");
            }
        return productsRemoved;
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
                                             resultSet.getFloat("Price_Bought"),
                                             resultSet.getFloat("Price_Sold"),
                                             resultSet.getTimestamp("Date_Added"), 
                                             resultSet.getString("Location"),
                                             resultSet.getInt("quantity")));
            }
            
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace(); 
        }
        return products;
    }
    
    public static boolean checkInventoryByID(int productID){
        ArrayList<Product> inventory = getInventoryByDate();
        boolean inInventory = false;
        for(int i=0; i<inventory.size(); i++){
            if(productID == inventory.get(i).getProductID()){
                inInventory = true; 
                break; 
            }
        }
        
        return inInventory;
    }
    
    public static ArrayList<Product> getInventoryOfType(String type){
        ArrayList<Product> inventory = getInventoryByDate(); 
        ArrayList<Product> allProductsOfType = new ArrayList(); 
        for(int i=0; i<inventory.size(); i++){
            if (type.equals(inventory.get(i).getType())){
                allProductsOfType.add(inventory.get(i));
            }
        }
        return allProductsOfType; 
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getLocation() {
        return location;
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

    public int getQuantity() {
        return quantity;
    }
    
    
}
