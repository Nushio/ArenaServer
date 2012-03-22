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
package net.k3rnel.arena.server.database;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;

import net.k3rnel.arena.server.GameServer;

/**
 * Handles THE Database
 * 
 * @author Nushio
 */
public class DatabaseManager {
    private final static int DATABASE_VERSION = 1;
    private final static String SCHEMA = GameServer.getDatabaseName();

    public void start(){

        //Opens the database once, just to check if everything's a-ok
        SqlSession session = DataConnection.openSession();
        try{
            if(session!=null){
                if (DATABASE_VERSION > getDbVersion()){
                    //OOh, Shiny!
                    onUpgrade(getDbVersion());
                }else{
                    //Create all tables!
                    onCreate();
                }
            }else{
                System.out.println("Could not connect to database.");
            }
        }catch(Exception e){
            System.out.println("Could not connect to database.");
        }finally{session.close();}

    }

    /**
     * Creates the database.
     * @return
     */
    public void onCreate() {
        SqlSession session = DataConnection.openSession();
        try {
            session.insert("CREATE SCHEMA `"+SCHEMA+"` DEFAULT CHARACTER SET latin1;");
            
            session.insert("CREATE TABLE `"+SCHEMA+"`.`users` ("+
                    "`id` int(11) NOT NULL AUTO_INCREMENT,"+
                    "`username` varchar(12) DEFAULT NULL,"+
                    "`password` varchar(255) DEFAULT NULL,"+
                    "`email` varchar(54) DEFAULT NULL,"+
                    "`lastLoginTime` datetime DEFAULT NULL,"+
                    "`lastLoginServer` varchar(32) DEFAULT NULL,"+
                    "`lastLoginIP` varchar(32) DEFAULT NULL,"+
                    "`muteban` tinyint(1) DEFAULT '0',"+
                    "`adminLevel` tinyint(1) DEFAULT '0',"+
                    "`lastLanguageUsed` tinyint(1) NOT NULL DEFAULT '0',"+
                    "PRIMARY KEY (`id`),"+
                    "UNIQUE KEY `uniq_username` (`username`),"+
                    "UNIQUE KEY `uniq_email` (`email`),"+
                    "KEY `username` (`username`),"+
                    "KEY `id` (`id`) USING BTREE"+
                    ") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8");
            
            session.insert("CREATE  TABLE `"+SCHEMA+"`.`misc` (" +
                    "`name` VARCHAR(20) NOT NULL ," +
                    "`value` VARCHAR(45) NULL ," +
                    " PRIMARY KEY (`name`) ," +
                    " UNIQUE INDEX `uq_name` (`name` ASC) ," +
                    "  INDEX `in_name` (`name` ASC) )" +
                    "ENGINE = MyISAM;");
            new MiscManager(session).insert(new Misc("version", DATABASE_VERSION));
            
            session.commit();
        } catch (SQLException e) {
            System.out.println("Failed to create the database");
        }finally{
            session.close();
        }

    }
    /**
     * Upgrades the database. Use sparingly.
     * @return
     */
    public void onUpgrade(int version) {
        System.out.println("Upgrading Database...");
        System.out.println("Current version: "+version);
        System.out.println("Latest version: "+DATABASE_VERSION+"\n");
        while(version < DATABASE_VERSION){
            version++;
            switch(version){
                case 2:{
                    System.out.println("Upgrading to version 2");
                    setDbVersion(2);
                    break;
                }
            }
        }
        setDbVersion(DATABASE_VERSION);
    }

    /**
     * Nukes the database. Don't use this. 
     * @return
     */
    public void goNuclear(){
        //        m_database.openSession().insert(("DROP TABLE IF EXISTS TABLE_NAME");
    }

    /**
     * Gets the Database Version.
     * @return dbVersion
     */
    public int getDbVersion(){
        SqlSession session = DataConnection.openSession();
        try{
            String version = new MiscManager(session).getByName("version").getValue();
            return Integer.parseInt(version);
        }catch(Exception e){
            System.out.println("Warning, failed to get current database version");
            System.out.println("Using 0 as a default");
            return 0;
        }finally{session.close();}
    }
    /**
     * Sets the database version.
     * @return
     */
    public void setDbVersion(int version){
        SqlSession session = DataConnection.openSession();
        try {
            new MiscManager(session).update(new Misc("version", version));
        } catch (Exception e) {
            System.out.println("Danger! Danger! Could not update DB Version!");
        }finally{session.close();}
    }
}
