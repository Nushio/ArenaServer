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

    public void start(){

        //Opens the database once, just to check if everything's a-ok
        SqlSession session = DataConnection.openSession();
        try{
            if(session!=null){
                int dbVersion = getDbVersion();
                if (DATABASE_VERSION > dbVersion){
                    //OOh, Shiny!
                    onUpgrade(dbVersion);
                }
            }else{
                System.out.println("Could not connect to database.");
            }
        }catch(Exception e){
            e.printStackTrace();
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
            System.out.println("Warning... Schema "+GameServer.getDatabaseName()+" must already be created!");
            DatabaseMapper dMapper = session.getMapper(DatabaseMapper.class);
            dMapper.create_table_users();
            dMapper.create_table_misc();
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
                case 1:{
                    onCreate();
                    break;
                }
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
