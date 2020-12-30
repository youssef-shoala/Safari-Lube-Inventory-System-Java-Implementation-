/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LoginSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import safari.lube.inventory.system.DBConnection;
import safari.lube.inventory.system.MainGUI;

/**
 *
 * @author youss
 */
public class Login {
    private final String username; 
    private final String password; 
    private final String access; 

    public Login(String username, String password, String access) {
        this.username = username;
        this.password = password;
        this.access = access;
    }
    
    
    
    private static Connection connection;
    private static PreparedStatement getAllLoginDetails; 
    private static PreparedStatement checkLoginDetails;
    private static ResultSet resultSet; 

    

    public static ArrayList<Login> getAllLoginDetails(){
        connection = DBConnection.getConnection();
        ArrayList<Login> loginDetails = new ArrayList<>(); 

        try{
           getAllLoginDetails = connection.prepareStatement("select * from login order by username"); 
           resultSet = getAllLoginDetails.executeQuery(); 
           while(resultSet.next()){
               loginDetails.add(new Login(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
           }
        }
        
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        return loginDetails; 
    }
    
    public static String checkLoginDetails(String username, String password){
        ArrayList<Login> loginDetails = getAllLoginDetails(); 
        String access = null; 
        
        for(int i=0; i<loginDetails.size(); i++){
            if(username.equals(loginDetails.get(i).getUsername()) && password.equals(loginDetails.get(i).getUsername())){
                if(loginDetails.get(i).getAccess().equals("Admin")){
                    access = "Admin"; 
                }
                else if(loginDetails.get(i).getAccess().equals("Employee")){
                    access = "Employee"; 
                }
            }
        }
       return access; 
    }

    
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccess() {
        return access;
    }
    
}
