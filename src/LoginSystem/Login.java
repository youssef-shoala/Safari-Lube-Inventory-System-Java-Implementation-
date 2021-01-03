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

/**
 *
 * @author youss
 */
public class Login {
    private final String username; 
    private final String password; 
    private final String access; 
    private final String name; 

    public Login(String username, String password, String access, String name) {
        this.username = username;
        this.password = password;
        this.access = access;
        this.name = name;
    }
    
    
    
    private static Connection connection;
    private static PreparedStatement addLogin;
    private static PreparedStatement removeLogin;
    private static PreparedStatement getAllLoginDetails; 
    private static ResultSet resultSet; 

    public static void addLogin(String username, String password, String access, String name){
        connection = DBConnection.getConnection();
        try{
            
            addLogin = connection.prepareStatement("insert into Login values (?,?,?,?)");
            addLogin.setString(1, username);
            addLogin.setString(2, password); 
            addLogin.setString(3, access);
            addLogin.setString(4, name);
            addLogin.executeUpdate();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
    }
    
    public static void removeLogin(String username){
        connection = DBConnection.getConnection();
        
        try{
            
            removeLogin = connection.prepareStatement("delete from Login where username = ?");
            removeLogin.setString(1, username);
            removeLogin.executeUpdate();
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
    }

    public static ArrayList<Login> getAllLoginDetails(){
        connection = DBConnection.getConnection();
        ArrayList<Login> loginDetails = new ArrayList<>(); 

        try{
           getAllLoginDetails = connection.prepareStatement("select * from login order by username"); 
           resultSet = getAllLoginDetails.executeQuery(); 
           while(resultSet.next()){
               loginDetails.add(new Login(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)));
           }
        }
        
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        
        return loginDetails; 
    }
    
    public static Login checkLoginDetails(String username, String password){
        ArrayList<Login> loginDetails = getAllLoginDetails(); 
        Login currentLogin = null; 
        
        for(int i=0; i<loginDetails.size(); i++){
            if(username.equals(loginDetails.get(i).getUsername()) && password.equals(loginDetails.get(i).getUsername())){
                currentLogin = loginDetails.get(i);
                break;
            }
        }
       return currentLogin; 
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

    public String getName() {
        return name;
    }
    
    
}
