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
package net.k3rnel.arena.server.backend.entity;

import java.util.HashMap;

import net.k3rnel.arena.server.GameServer;
import net.k3rnel.arena.server.backend.battle.BattleField;
import net.k3rnel.arena.server.network.TCPManager;

import com.esotericsoftware.kryonet.Connection;

/**
 * Represents a player
 * @author Nushio
 *
 */
public class PlayerChar {
	/*
	 * An enum to store request types
	 */
	public enum RequestType { BATTLE };
	/*
	 * An enum to store the player's selected language
	 */
	public enum Language { ENGLISH, PORTUGESE, ITALIAN, FRENCH, FINNISH, SPANISH, GERMAN, DUTCH }
	
	private int m_id;
	private String m_name;
	private Language m_language;
	private int m_battleId;
	private boolean m_isBattling = false;
	private BattleField m_battleField = null;
	private long m_lastLogin;
	private int m_adminLevel = 0;
	private boolean m_isMuted; 
	private Connection m_tcpSession;
	
	/*
	 * Kicking timer
	 */
	public long lastPacket = System.currentTimeMillis();
	
	/*
	 * Stores the list of requests the player has sent
	 */
	private HashMap<String, RequestType> m_requests;
	
	/**
	 * Constructor
	 * NOTE: Minimal initialisations should occur here
	 */
	public PlayerChar() {
		m_requests = new HashMap<String, RequestType>();
	}

	/**
	 * Returns this player's ip address
	 * @return
	 */
	public String getIpAddress() {
		if(getTcpSession().getRemoteAddressTCP()!=null){
			String ip = getTcpSession().getRemoteAddressTCP().toString();
			ip = ip.substring(1);
			ip = ip.substring(0, ip.indexOf(":"));
			return ip;
		} else {
			return "";
		}			
	}
	
	/**
	 * Returns the preferred language of the user
	 * @return
	 */
	public Language getLanguage() {
		return m_language;
	}
	
	/**
	 * Sets this player's preferred language
	 * @param l
	 */
	public void setLanguage(Language l) {
		m_language = l;
	}
	
	/**
	 * Stores a request the player has sent
	 * @param username
	 * @param r
	 */
	public void addRequest(String username, RequestType r) {
		m_requests.put(username, r);
	}
	
	/**
	 * Removes a request
	 * @param username
	 */
	public void removeRequest(String username) {
		m_requests.remove(username);
	}
	
	/**
	 * Called when a player accepts a request sent by this player
	 * @param username
	 */
	public void requestAccepted(String username) {
		PlayerChar otherPlayer = TCPManager.getPlayer(username);
		if(otherPlayer != null) {
			if(m_requests.containsKey(username)) {
				switch(m_requests.get(username)) {
				case BATTLE:
					m_battleField = new BattleField(this, otherPlayer);
					break;
				}
			}
		} else {
			getTcpSession().sendTCP("r!0");
		}
	}
	
	/**
	 * Clears the request list
	 */
	public void clearRequests() {
		if(m_requests.size() > 0) {
			for(String username : m_requests.keySet()) {
				if(TCPManager.containsPlayer(username)) {
					TCPManager.getPlayer(username).getTcpSession().sendTCP("rc" + this.getName());
				}
			}
			m_requests.clear();
		}
	}
	
	/**
	 * Creates a new PlayerChar
	 */
	public void createNewPlayer() {
		m_isMuted = false;
	}
	
	/**
	 * Called when a player loses a battle
	 */
	public void lostBattle() {
		
	}
	
	/**
	 * Returns true if this player is muted
	 * @return
	 */
	public boolean isMuted() {
		return m_isMuted;
	}
	
	/**
	 * Sets if this player is muted
	 * @param b
	 */
	public void setMuted(boolean b) {
		m_isMuted = b;
	}
	
	/**
	 * Returns the player name
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Sets the player Name
	 * @param b
	 */
	public void setName(String Name) {
		m_name = Name;
	}

	/**
	 * Returns the battle id of this player on the battlefield
	 */
	public int getBattleId() {
		return m_battleId;
	}
	
	/**
	 * Returns the id of this player
	 */
	public int getId() {
		return m_id;
	}
	
	/**
	 * Sets the player Id
	 * @param id
	 */
	public void setId(int Id) {
		m_id = Id;
	}

	/**
	 * Returns true if this player is battling
	 */
	public boolean isBattling() {
		return m_isBattling;
	}
	
	/**
	 * Sets if this player is battling
	 * @param b
	 */
	public void setBattling(boolean b) {
		m_isBattling = b;
	}

	/**
	 * Sets this player's battle id on a battlefield
	 */
	public void setBattleId(int battleID) {
		m_battleId = battleID;
	}

	/**
	 * Sets the last login time (used for connection downtimes)
	 * @param t
	 */
	public void setLastLoginTime(long t) {
		m_lastLogin = t;
	}
	
	/**
	 * Returns the last login time
	 * @return
	 */
	public long getLastLoginTime() {
		return m_lastLogin;
	}
	
	/**
	 * Forces the player to be logged out
	 */
	public void forceLogout() {
		if(this.getTcpSession().isConnected()) {
			this.getTcpSession().close();
		} else {
			GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(this);
		}
	}
	
	/**
	 * Sets the admin level for this player
	 * @param adminLevel
	 */
	public void setAdminLevel(int adminLevel) {
		m_adminLevel = adminLevel;
	}
	
	/**
	 * Returns the admin level of this player
	 * @return
	 */
	public int getAdminLevel() {
		return m_adminLevel;
	}

	/**
	 * @return the battleField
	 */
	public BattleField getBattleField() {
		return m_battleField;
	}

	/**
	 * @param mBattleField the BattleField to set
	 */
	public void setBattleField(BattleField mBattleField) {
		m_battleField = mBattleField;
	}
	/**
	 * Returns the current TCP Session
	 * return m_tcpSession
	 */
	public Connection getTcpSession() {
		return m_tcpSession;
	}
	/**
	 * Sets the TCP Session
	 * @param tcpSession
	 */
	public void setTcpSession(Connection mTcpSession) {
		m_tcpSession = mTcpSession;
	}
	
	
}
