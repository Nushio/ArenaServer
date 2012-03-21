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

import java.sql.ResultSet;

import net.k3rnel.arena.server.GameServer;

/**
 * Handles THE Database
 * @author Nushio
 * @since Today
 */
public class DatabaseManager {
	private MySqlManager m_database;
	private final static int DATABASE_VERSION = -1;
	private final static String SCHEMA = GameServer.getDatabaseName();

	public void start(){
		m_database = new MySqlManager();
		if(m_database.connect(GameServer.getDatabaseHost(), GameServer.getDatabaseName(),GameServer.getDatabaseUsername(), GameServer.getDatabasePassword())){
			if(m_database.selectDatabase(GameServer.getDatabaseName())){
				if (DATABASE_VERSION > getDbVersion()){
					onUpgrade(getDbVersion());
				}
			}else{
				onCreate();
			}
		}else{
			System.out.println("Could not connect to database.");
		}

	}

	/**
	 * Creates the database.
	 * @return
	 */
	public void onCreate() {
		m_database.query("CREATE SCHEMA `"+SCHEMA+"` DEFAULT CHARACTER SET latin1;");
		m_database.query("CREATE TABLE `"+SCHEMA+"`.`users` ("+
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
		m_database.query("CREATE  TABLE `"+SCHEMA+"`.`misc` (" +
							"`version` INT NOT NULL DEFAULT 0 )" +
							"ENGINE = MyISAM;");
		m_database.query("INSERT INTO `"+SCHEMA+"`.`misc` (" +
				"`version`) VALUES ("+DATABASE_VERSION+")");
		setDbVersion(DATABASE_VERSION);
	}
	/**
	 * Upgrades the database. Use sparringly.
	 * @return
	 */
	public void onUpgrade(int version) {
		while(version < DATABASE_VERSION){
			version++;
			switch(version){
			case 2:
				setDbVersion(2);
				break;
			}
		}
		setDbVersion(DATABASE_VERSION);
	}
	
	/**
	 * Nukes the database. Don't use this. 
	 * @return
	 */
	public void goNuclear(){
		m_database.query("DROP TABLE IF EXISTS TABLE_NAME");
	}
	
	/**
	 * Gets the Database Version.
	 * @return dbVersion
	 */
	public int getDbVersion(){
		ResultSet rs = m_database.query("SELECT version FROM misc");
		try{
			if(rs.next()){
				return rs.getInt("version");
			}else{
				return 0;
			}
		}catch(Exception e){
			return 0;
		}
	}
	/**
	 * Sets the database version.
	 * @return
	 */
	public void setDbVersion(int version){
		m_database.query("UPDATE FROM misc SET version = "+version);
	}
}
