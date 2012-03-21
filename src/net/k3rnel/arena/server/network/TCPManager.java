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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.k3rnel.arena.server.backend.entity.PlayerChar;
import net.k3rnel.arena.server.network.NetworkProtocols.LoginData;
import net.k3rnel.arena.server.network.NetworkProtocols.RegistrationData;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

/**
 * Handles packets received from players over TCP
 * @author Nushio
 *
 */
public class TCPManager {
	private Server m_server;
	private static HashMap<String, PlayerChar> m_players;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private RegistrationManager m_regManager;
	
	/**
	 * Constructor
	 * @param login
	 * @param logout
	 */
	public TCPManager(LoginManager login, LogoutManager logout){
		m_loginManager = login;
		m_logoutManager = logout;
		m_players = new HashMap<String, PlayerChar>();
		m_regManager = new RegistrationManager();
		m_regManager.start();
		
		m_server = new Server(){
			protected Connection newConnection(){
				return new PlayerConnection();
			}
		};
		//This allows us to get incoming classes and all. 
		NetworkProtocols.register(m_server);
		m_server.addListener(new Listener(){
			public void received(Connection c, Object o){
				// We know all connections for Crystalis are Players.
				PlayerConnection connection = (PlayerConnection)c;
				if(connection.name!=null){
					connection.name = "_"+connection.getRemoteAddressTCP()+new Random().nextInt(10);//
				}
				if(o instanceof LoginData){ //Someone is trying to login!
					String username = ((LoginData)o).username.trim();
					String password = ((LoginData)o).password.trim();
					if(username!=null&&username.length()>0&&password!=null&&password.length()>0){
						m_loginManager.queuePlayer(connection,(LoginData)o);
					}
				}
				if(o instanceof RegistrationData){ //Someone is trying to register!
					//Not entirely sure if these 4 lines are the best way to handle this
					String username = ((RegistrationData)o).username.trim();
					String password = ((RegistrationData)o).password.trim();
					String email = ((RegistrationData)o).email.trim();
					if(username!=null&&username.length()>0&&password!=null&&password.length()>0&&email!=null && email.length()>0){
						//Alright, made sure that he didn't feed us nulls. 
						m_regManager.queueRegistration(connection,(RegistrationData)o);
					}else{
						//They fed us nulls. Give'em a warning.
					}
				}
				
			}
			public void disconnected (Connection c) {
				/*
				 * Attempt to save the player's data
				 */
				try {
					PlayerConnection connection = (PlayerConnection)c;
					PlayerChar p = TCPManager.getPlayer(connection.name);
					if(p != null) {
						if(p.isBattling()) {
							/* If in PvP battle, the player loses */
							p.lostBattle();
						}
						m_logoutManager.queuePlayer(p);
						m_players.remove(p);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		try {
			m_server.bind(NetworkProtocols.tcp_port);
			m_server.start();
			System.out.println("TCP Manager started!");
		} catch (IOException e) {
			System.out.println("TCP Manager @ "+NetworkProtocols.tcp_port+" will not lock!");
			e.printStackTrace();
		}
	}
	public void logoutEveryone(){
		m_server.stop();
//		m_regManager.stop();
		m_loginManager.stop();
		/*
		 * Queue all players to be saved 
		 */
		Iterator<PlayerChar> it = m_players.values().iterator();
		PlayerChar p;
		while(it.hasNext()){
			p = it.next();
			m_logoutManager.queuePlayer(p);
		}
		/*
		 * Since the method is called during a server shutdown, wait for all players to be logged out
		 */
		while(m_logoutManager.getPlayerAmount() > 0);
		m_logoutManager.stop();
	}
	/**
	 * Adds a player to the player list
	 * @param p
	 */
	public static void addPlayer(PlayerChar p) {
		synchronized(m_players) {
			m_players.put(p.getName(), p);
		}
	}
	/**
	 * Removes a player from the player list
	 * @param p
	 */
	public static void removePlayer(PlayerChar p) {
		synchronized(m_players) {
			m_players.remove(p.getName());
		}
	}
	/**
	 * Returns a player
	 * @param username
	 * @return
	 */
	public static PlayerChar getPlayer(String username) {
		synchronized(m_players) {
			return m_players.get(username);
		}
	}
	/**
	 * Returns true if the player list contains a player
	 * @param username
	 * @return
	 */
	public static boolean containsPlayer(String username) {
		synchronized(m_players) {
			return m_players.containsKey(username);
		}
	}
	/**
	 * Kicks idle players
	 */
	public static void kickIdlePlayers() {
		synchronized(m_players) {
			for(PlayerChar p : m_players.values()) {
				if(System.currentTimeMillis() - p.lastPacket >= 900000)
					p.forceLogout();
			}
		}
	}
	/**
	 * Returns how many players are logged in
	 * @return
	 */
	public static int getPlayerCount() {
		synchronized(m_players) {
			return m_players.keySet().size();
		}
	}
	// This holds per connection state.
	static class PlayerConnection extends Connection {
		public String name; //Default is IP
	}
}
