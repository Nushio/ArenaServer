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

import java.util.LinkedList;
import java.util.Queue;

import org.apache.ibatis.session.SqlSession;

import net.k3rnel.arena.server.GameServer;
import net.k3rnel.arena.server.backend.entity.PlayerChar;
import net.k3rnel.arena.server.backend.entity.PlayerChar.Language;
import net.k3rnel.arena.server.database.DataConnection;
import net.k3rnel.arena.server.database.User;
import net.k3rnel.arena.server.database.UserManager;
import net.k3rnel.arena.server.network.NetworkProtocols.LoginData;
import net.k3rnel.arena.server.network.NetworkProtocols.PasswordChangeData;

import com.esotericsoftware.kryonet.Connection;


/**
 * Handles logging players in
 * @author shadowkanji
 * @author Nushio
 *
 */
public class LoginManager implements Runnable {
    private Queue<LoginContainer> m_loginQueue;
    private Thread m_thread;
    private boolean m_isRunning;

    private Queue<PassChangeContainer> m_passChangeQueue;

    /**
     * Default constructor. Requires a logout manager to be passed in so the server
     * can check if player's data is not being saved as they are logging in.
     * @param manager
     */
    public LoginManager(LogoutManager manager) {
        m_loginQueue = new LinkedList<LoginContainer>();
        m_passChangeQueue = new LinkedList<PassChangeContainer>();
        m_thread = null;
    }

    /**
     * Attempts to login a player. Upon success, it sends a packet to the player to inform them they are logged in.
     * @param session
     * @param l
     * @param username
     * @param password
     */
    private void attemptLogin(Connection conn, LoginData data) {
        SqlSession session = DataConnection.openSession();
        try {
            //Check if we haven't reach the player limit
            if(TCPManager.getPlayerCount() >= GameServer.getMaxPlayers()) {
                LoginData lo = new LoginData();
                lo.state = 1; //State 1 is Error, Player Limit Reached. 
                conn.sendTCP(lo);
                return;
            }
            //Check if there's database connection
            if(session!=null) {
                LoginData lo = new LoginData();
                lo.state = 2; //State 2 is Error, Database is AFK. 
                conn.sendTCP(lo);
                return;
            }
            //Find the member's information
            User user = new UserManager(session).getByUsername(data.username);
            if(user==null){
                LoginData lo = new LoginData();
                lo.state = 3; //State 3 is Error, Username or Password Wrong. 
                conn.sendTCP(lo);
                return;
            }
            //Check if the password is correct
            if(user.getPassword().compareTo(data.password) == 0) {
                //Now check if they are logged in anywhere else	
                this.login(data.username, data.language, conn, user);
            } else {
                //Password is wrong, say so.
                LoginData lo = new LoginData();
                lo.state = 3; //State 3 is Error, Username or Password Wrong. 
                conn.sendTCP(lo);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoginData lo = new LoginData();
            lo.state = 99; //State 99 is Error, F.U.B.A.R. Check Logs.  
            conn.sendTCP(lo);
        }finally{
            session.close();
        }
    }

    /**
     * Places a player in the login queue
     * @param session
     * @param username
     * @param password
     * @param force - true if player wants to force login
     */
    public void queuePlayer(Connection conn, LoginData data) {
        if(m_thread == null || !m_thread.isAlive()) {
            start();
        }
        m_loginQueue.offer(new LoginContainer(conn,data));
    }

    /**
     * Places a player in the queue to update their password
     * @param session
     * @param username
     * @param newPassword
     * @param oldPassword
     */
    public void queuePasswordChange(Connection conn, PasswordChangeData data) {
        if(m_thread == null || !m_thread.isAlive()) {
            start();
        }
        m_passChangeQueue.offer(new PassChangeContainer(conn,data));
    }

    /**
     * Called by Thread.start()
     */
    public void run() {
        LoginData ldata;
        PasswordChangeData pdata;
        Connection conn;
        while(m_isRunning) {
            synchronized(m_loginQueue) {
                try {
                    if(m_loginQueue.peek() != null) {
                        LoginContainer queue = m_loginQueue.poll();
                        ldata = queue.log;
                        conn = queue.conn;
                        this.attemptLogin(conn, ldata);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {}

            synchronized(m_passChangeQueue) {
                try {
                    if(m_passChangeQueue.peek() != null) {
                        pdata = m_passChangeQueue.poll().data;
                        conn = m_passChangeQueue.poll().conn;
                        this.changePass(conn,pdata);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {}
        }
        m_thread = null;
    }

    /**
     * Starts the login manager
     */
    public void start() {
        if(m_thread == null || !m_thread.isAlive()) {
            m_thread = new Thread(this);
            m_isRunning = true;
            m_thread.start();
        }
    }

    /**
     * Stops the login manager
     */
    public void stop() {
        m_isRunning = false;
    }

    /**
     * Changes the password of a player
     * @param username
     * @param newPassword
     * @param oldPassword
     * @param session
     */
    private void changePass( Connection conn,PasswordChangeData data) {
        SqlSession session = DataConnection.openSession();
        try{
            if(session!=null){
                User user = new UserManager(session).getByUsername(data.username);
                if(user!=null){
                    // if user exists, compare their old password to the one we have stored for them
                    if(user.getPassword().compareTo(data.oldPassword) == 0) {
                        // old password matches the one on file, therefore they got their old password correct, so it can be changed to their new one
                        user.setPassword(data.newPassword);
                        new UserManager(session).changePassword(user);
                        // tell them their password was changed successfully
                        data.state = 0; //State 0 means Password Changed
                        conn.sendTCP(data);
                        return;
                    }else{
                        data.state = 1; //State 1 means Old Password is wrong
                        conn.sendTCP(data);
                    }
                }
            }else{
                //tell them we failed to change their password
                data.state = 99;//What a Terrible Failure
                conn.sendTCP(data);
            }
        }catch(Exception e){
            e.printStackTrace();
            data.state = 99;//What a Terrible Failure
            conn.sendTCP(data);
        }finally{session.close();}
    }

    /**
     * Logs in a player
     * @param username
     * @param language
     * @param session
     * @param result
     */
    private void login(String username, String language, Connection conn, User user) {
        //They are not logged in elsewhere, set the current login to the current server
        long time = System.currentTimeMillis();
        /*
         * Attempt to log the player in
         */
        PlayerChar p = getPlayerObject(user);
        p.setLastLoginTime(time);
        p.setTcpSession(conn);
        p.setLanguage(Language.values()[Integer.parseInt(String.valueOf(language))]);
        conn.setName(username);
        /*
         * Send success packet to player, set their map and add them to a movement service
         */
        this.initialiseClient(p, conn);
        /*
         * Add them to the list of players
         */
        TCPManager.addPlayer(p);
        GameServer.getInstance().updatePlayerCount();
        System.out.println("INFO: " + username + " logged in.");
    }

    /**
     * Sends initial information to the client
     * @param p
     * @param session
     */
    private void initialiseClient(PlayerChar p, Connection conn) {
        LoginData lo = new LoginData();
        lo.state = 0; //State 0 is Succesfull login. Awesome. 
        conn.sendTCP(lo);

    }

    /**
     * Returns a playerchar object from a resultset of player data
     * @param data
     * @return
     */
    private PlayerChar getPlayerObject(User user) {
        try {
            PlayerChar p = new PlayerChar();

            p.setName(user.getUsername());
            p.setAdminLevel(user.getAdminLevel());
            if(user.getMuteban()==1){
                p.setMuted(true);
            }
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    class LoginContainer{
        Connection conn;
        LoginData log;

        LoginContainer(Connection connection, LoginData login){
            conn = connection;
            log = login;
        }		
    }
    class PassChangeContainer{
        Connection conn;
        PasswordChangeData data;

        PassChangeContainer(Connection connection, PasswordChangeData passChange){
            conn = connection;
            data = passChange;
        }		
    }
}
