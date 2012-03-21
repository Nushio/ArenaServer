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

import java.io.Serializable;

/**
 * The User Bean. It stores the Player Data. 
 * @author Nushio
 *
 */
public class User implements Serializable{

    private static final long serialVersionUID = -4949078689908050382L;
    private String id;
    private String username;
	private String password;
	private String email;
	private Long lastLoginTime;
	private String lastLoginServer;
	private String lastLoginIP;
	private int muteban;
	private int adminLevel;
	private int lastLanguageUsed;
	
	/**
	 * Main constructor
	 */
	public User(){
	    
	}

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the lastLoginTime
     */
    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * @param lastLoginTime the lastLoginTime to set
     */
    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * @return the lastLoginServer
     */
    public String getLastLoginServer() {
        return lastLoginServer;
    }

    /**
     * @param lastLoginServer the lastLoginServer to set
     */
    public void setLastLoginServer(String lastLoginServer) {
        this.lastLoginServer = lastLoginServer;
    }

    /**
     * @return the lastLoginIP
     */
    public String getLastLoginIP() {
        return lastLoginIP;
    }

    /**
     * @param lastLoginIP the lastLoginIP to set
     */
    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    /**
     * @return the muteban
     */
    public int getMuteban() {
        return muteban;
    }

    /**
     * @param muteban the muteban to set
     */
    public void setMuteban(int muteban) {
        this.muteban = muteban;
    }

    /**
     * @return the adminLevel
     */
    public int getAdminLevel() {
        return adminLevel;
    }

    /**
     * @param adminLevel the adminLevel to set
     */
    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    /**
     * @return the lastLanguageUsed
     */
    public int getLastLanguageUsed() {
        return lastLanguageUsed;
    }

    /**
     * @param lastLanguageUsed the lastLanguageUsed to set
     */
    public void setLastLanguageUsed(int lastLanguageUsed) {
        this.lastLanguageUsed = lastLanguageUsed;
    }
}
