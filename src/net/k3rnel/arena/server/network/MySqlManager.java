/**
 *  Copyright (C) 2012 K3RNEL Developer Team
 *  
 *  This file is part of Distro Wars (Server).
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.k3rnel.arena.server.network;

/*
 * Simple MySQL Java Class
 * Makes it similair to PHP
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Handles MySql connections
 * @author Daniel Morante
 * @author Nushio ( parseSQL() )
 */
public class MySqlManager {
    private Connection mysql_connection;
    private ResultSet mysql_result;    
    private String mysql_connectionURL;
    
    /**
     * Connects to the server. Returns true on success.
     * @param server
     * @param username
     * @param password
     * @return
     */
    public boolean connect(String server, String db, String username, String password) {
        try {
            //Open Connection
            mysql_connectionURL = "jdbc:mysql://" + server+":3306/"+db+"?autoReconnect=true";
            mysql_connection = DriverManager.getConnection(mysql_connectionURL, username, password);
            if(!mysql_connection.isClosed())
            	return true;
            else
            	return false;
        } catch( Exception x ) {
          x.printStackTrace();
          return false;
        }
    }
    
    /**
     * Selects the current database. Returns true on success
     * @param database
     * @return
     */
    public boolean selectDatabase(String database) {
    	try {
        	Statement stm = mysql_connection.createStatement();
        	stm.executeQuery("USE " + database);
        	return true;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * Closes the connection to the mysql server. Returns true on success.
     * @return
     */
    public boolean close(){
        try{
            mysql_connection.close();
            mysql_connection = null;
            return true;
        }
        catch (Exception x) {
             x.printStackTrace();
             return false;
        }
    }
    
    /**
     * Returns a result set for a query
     * @param query
     * @return
     */
    public ResultSet query(String query){
        //Create Statement object
        Statement stmt;
        
        /*
         * We want to keep things simple, so...
         *
         * Detect whether this is an INSERT, DELETE, or UPDATE statement      
         * And use the executeUpdate() function
         *
         * Or...
         * 
         * Detect whether this is a SELECT statement and use the executeQuery()
         * Function. 
         * 
        */  
        
        if (query.startsWith("SELECT")) {
            //Use the "executeQuery" function because we have to retrieve data
            //Return the data as a resultset
            try{
                //Execute Query
                stmt = mysql_connection.createStatement();
                mysql_result = stmt.executeQuery(query);
            }
            catch(Exception x) {
                x.printStackTrace();
            }
            
            //Return Result
            return mysql_result;
        }
        else {
            //It's an UPDATE, INSERT, or DELETE statement
            //Use the"executeUpdaye" function and return a null result
            try{
                //Execute Query
                stmt = mysql_connection.createStatement();
                stmt.executeUpdate(query);
            }
            catch(Exception x) {
                x.printStackTrace();
            }
            
            //Return nothing
            return null;
        }
    }    
    
    public static String parseSQL(String text)
	{
		try {
			if(text == null) text = "";
			text = text.replace("'", "''");
			text = text.replace("\\", "\\\\");
		} catch (Exception e) {}
		return  text;
	}
}
