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
import java.util.Calendar;

/**
 *
 * @author youss
 */
public class Merchandise {
    private final String cls; 
    private final String type; 

    public Merchandise(String cls, String type) {
        this.cls = cls;
        this.type = type;
    }
    
    private static Connection connection;
    private static PreparedStatement getMerchandiseByClass; 
    private static PreparedStatement getMerchandiseClasses;
    private static PreparedStatement addMerchandise; 
    private static PreparedStatement removeMerchandise;
    
    public static ArrayList<Merchandise> getMerchandiseByClass(String cls){
        connection = DBConnection.getConnection();
        ArrayList<Merchandise> merchandise = new ArrayList();

        try
        {
            getMerchandiseByClass = connection.prepareStatement("select type from merchandise where class = ?");
            getMerchandiseByClass.setString(1,cls);
            ResultSet resultSet = getMerchandiseByClass.executeQuery();
            
            while(resultSet.next()){
                merchandise.add(new Merchandise(cls, resultSet.getString(1)));
            }
            
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }

        return merchandise;
    }
    
    public static ArrayList<String> getMerchandiseClasses(){
        connection = DBConnection.getConnection();
        ArrayList<String> merchandiseClasses = new ArrayList();

        try
        {
            getMerchandiseClasses = connection.prepareStatement("select class from merchandise");
            ResultSet resultSet = getMerchandiseClasses.executeQuery();
            
            while(resultSet.next()){
                if(!merchandiseClasses.contains(resultSet.getString(1))){
                    merchandiseClasses.add((String)resultSet.getString(1));
                }
            } 
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }

        return merchandiseClasses;
    }
    
    public static Merchandise addMerchandise(String cls, String type){
        connection = DBConnection.getConnection(); 
        Merchandise merchandise = null; 
        try{
            addMerchandise = connection.prepareStatement("insert into merchandise values (?,?)");
            addMerchandise.setString(1, cls);
            addMerchandise.setString(2, type);
            addMerchandise.executeUpdate();
            merchandise = new Merchandise(cls, type);
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        return merchandise; 
    }
    public static void removeMerchandise(String type){
        connection = DBConnection.getConnection(); 
        try{
            removeMerchandise = connection.prepareStatement("delete from merchandise where type = ?");
            removeMerchandise.setString(1, type);
            removeMerchandise.executeUpdate();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
    }
    
    
    public String getCls() {
        return cls;
    }

    public String getType() {
        return type;
    }
    
    
}
