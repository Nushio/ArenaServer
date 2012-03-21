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

import org.apache.ibatis.session.SqlSession;

import net.k3rnel.arena.server.database.DataConnection;
import net.k3rnel.arena.server.database.UserManager;
import net.k3rnel.arena.server.feature.ChatManager;

/**
 * Handles all networking. Database included. 
 * @author Nushio
 * @author shadowkanji
 */
public class NetworkService  {
	private TCPManager m_tcpManager;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private ChatManager [] m_chatManager;
	
	/**
	 * Default constructor
	 */
	public NetworkService() {
		m_logoutManager = new LogoutManager();
		m_loginManager = new LoginManager(m_logoutManager);
		m_chatManager = new ChatManager[3];
		m_tcpManager = new TCPManager(m_loginManager, m_logoutManager);
	}
	
	/**
	 * Returns the login manager
	 * @return
	 */
	public LoginManager getLoginManager() {
		return m_loginManager;
	}
	
	/**
	 * Returns the logout manager
	 * @return
	 */
	public LogoutManager getLogoutManager() {
		return m_logoutManager;
	}
	
	/**
	 * Returns the chat manager with the least amount of processing to be done
	 * @return
	 */
	public ChatManager getChatManager() {
		int smallest = 0;
		for(int i = 1; i < m_chatManager.length; i++) {
			if(m_chatManager[i].getProcessingLoad() < m_chatManager[smallest].getProcessingLoad())
				smallest = i;
		}
		return m_chatManager[smallest];
	}
	
	/**
	 * Returns the connection manager (packet handler)
	 * @return
	 */
	public TCPManager getConnectionManager() {
		return m_tcpManager;
	}
	
	/**
	 * Start this network service by starting all threads.
	 */
	public void start() {
		//Load MySQL JDBC Driver
        try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
		    System.out.println("Could not load the MySQL JDBC Driver :( "); 
			e.printStackTrace();
		}
		/*
		 * Ensure anyone still marked as logged in on this server
		 * is unmarked
		 */
        SqlSession session = DataConnection.openSession();
        try{
            int released = new UserManager(session).release();
		    System.out.println("Released "+released+" users");
        }catch(Exception e){
           System.out.println("Could not release users. Somethign is wrong :( "); 
        }finally{session.close();}
		/*
		 * Start the login/logout managers
		 */
		m_logoutManager.start();
		m_loginManager.start();
		/*
		 * Start the chat managers
		 */
		for(int i = 0; i < m_chatManager.length; i++) {
			m_chatManager[i] = new ChatManager();
			m_chatManager[i].start();
		}
		System.out.println("INFO: Network Service started.");
	}
	
	/**
	 * Stop this network service by stopping all threads.
	 */
	public void stop() {
		//Stop all threads (do not use thread.stop() )
		//Unbind network address
		for(int i = 0; i < m_chatManager.length; i++)
			m_chatManager[i].stop();
		m_tcpManager.logoutEveryone();
	}
}
