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
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;

import net.k3rnel.arena.server.GameServer;
import net.k3rnel.arena.server.database.DataConnection;
import net.k3rnel.arena.server.database.User;
import net.k3rnel.arena.server.database.UserManager;
import net.k3rnel.arena.server.network.NetworkProtocols.RegistrationData;

import com.esotericsoftware.kryonet.Connection;


/**
 * Handles registrations
 * @author shadowkanji
 *
 */
public class RegistrationManager implements Runnable {
    private Queue<RegistrationContainer> m_queue;
    private Thread m_thread;
    private boolean m_isRunning;

    /**
     * Constructor
     */
    public RegistrationManager() {
        m_thread = null;
        m_queue = new LinkedList<RegistrationContainer>();
    }

    /**
     * Queues a registration
     * @param session
     * @param packet
     */
    public void queueRegistration(Connection connection, RegistrationData data) {
        if(m_thread == null || !m_thread.isAlive())
            start();
        if(!m_queue.contains(data)) {
            m_queue.offer(new RegistrationContainer(connection,data));
        }
    }

    /**
     * Registers a new player
     * @param session
     */
    public void register(Connection conn, RegistrationData reg) throws Exception {
        if(!conn.isConnected()) {
            return;
        }
        SqlSession session = DataConnection.openSession();
        try{
            if(session!=null) {
                reg.username = reg.username.trim();
                /*
                 * Check if username is valid
                 */
                if(reg.username.length()<4 || reg.username.length()>12 ||
                        (!Pattern.matches("[a-zA-Z]+", reg.username)) ){
                    RegistrationData ro = new RegistrationData();
                    ro.state = 1; //State 1 is Error, username exists or is 'forbidden'.
                    conn.sendTCP(ro);
                    return;
                }

                /*
                 * Check if the user exists
                 */
                User user = new UserManager(session).getByUsername(reg.username);
                try {				
                    if(user != null ) {
                        RegistrationData ro = new RegistrationData();
                        ro.state = 1; //State 1 is Error, username exists or is 'forbidden'.
                        conn.sendTCP(ro);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    RegistrationData ro = new RegistrationData();
                    ro.state = 1; //State 1 is Error, username exists or is 'forbidden'.
                    conn.sendTCP(ro);
                    return;
                }
                /*
                 * Check if email is valid
                 */
                reg.email = reg.email.trim();
                Pattern pattern = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$",Pattern.CASE_INSENSITIVE);
                if(!pattern.matcher(reg.email).matches()){
                    RegistrationData ro = new RegistrationData();
                    ro.state = 2; //State 2 is Error, EMail taken.   
                    conn.sendTCP(ro);
                    return;
                }


                /*
                 * Check if an account is already registered with the email
                 */
                user = new UserManager(session).getByEmail(reg.email);
                try {				
                    if(user != null) {
                        RegistrationData ro = new RegistrationData();
                        ro.state = 2; //State 2 is Error, EMail taken.   
                        conn.sendTCP(ro);
                        return;
                    }
                    if(reg.email.length() > 52) {
                        RegistrationData ro = new RegistrationData();
                        ro.state = 3; //State 3 is Error, email's too long.   
                        conn.sendTCP(ro);
                        return;
                    }
                } catch (Exception e) {
                    RegistrationData ro = new RegistrationData();
                    ro.state = 99; //State 99 is Error, What a Terrible Failure.   
                    conn.sendTCP(ro);
                    e.printStackTrace();
                }
                /*
                 * Create the player in the database
                 */
                user = new User();
                user.setUsername(reg.username);
                user.setPassword(reg.password);
                user.setEmail(reg.email);
                user.setLastLoginServer(GameServer.getServerName());
                try{
                    user.setLastLoginIP(conn.getRemoteAddressTCP().getAddress().getHostAddress());
                }catch(Exception e){
                    user.setLastLoginIP("0.0.0.0");
                }
                new UserManager(session).insert(user);
                /*
                 * Finish
                 */			
                RegistrationData ro = new RegistrationData();
                ro.state = 0; //State 0 is Success. Registration's done and awesome.   
                conn.sendTCP(ro);
            } else {
                RegistrationData ro = new RegistrationData();
                ro.state = 4; //State 4 is Error, Database is offline.   
                conn.sendTCP(ro);
            }
        }catch(Exception e){
            e.printStackTrace();
            RegistrationData ro = new RegistrationData();
            ro.state = 99; //State 4 is Error, Database is offline.   
            conn.sendTCP(ro);
        }finally{session.close();}
    }

    /**
     * Called by m_thread.start()
     */
    public void run() {
        RegistrationData reg;
        Connection conn;
        while(m_isRunning) {
            try{
                synchronized(m_queue) {
                    if(m_queue.peek() != null) {
                        RegistrationContainer queue = m_queue.poll();
                        reg = queue.reg;
                        conn = queue.conn;
                        try {
                            this.register(conn,reg);
                        } catch (Exception e) {
                            e.printStackTrace();
                            RegistrationData ro = new RegistrationData();
                            ro.state = 5; //State 5 is Error, Something went wrong.    
                            conn.sendTCP(ro);
                        }
                    }
                }
                try {
                    Thread.sleep(250);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the registration manager
     */
    public void start() {
        if(m_thread == null || !m_thread.isAlive()) {
            m_thread = new Thread(this);
            m_isRunning = true;
            m_thread.start();
        }
    }

    /**
     * Stop the registration manager
     */
    public void stop() {
        m_isRunning = false;
    }

    class RegistrationContainer{
        Connection conn;
        RegistrationData reg;

        RegistrationContainer(Connection connection, RegistrationData registration){
            conn = connection;
            reg = registration;
        }		
    }
}

