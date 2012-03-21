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

import net.k3rnel.arena.server.GameServer;
import net.k3rnel.arena.server.utils.Utils;

import org.apache.ibatis.session.SqlSession;

/**
 * UserManager manages the database queries related to the Misc Table
 * Using MyBatis, we can set all queries in an XML file, and make managing
 * queries an easier task. 
 * 
 * This requires the UserMapper.xml, and a UserMapper.java Interface. 
 * 
 * @author Nushio
 *
 */
public class UserManager {

    private SqlSession session;

    /**
     * Initializes the UserManager
     * @param session
     */
    public UserManager(SqlSession session) {
        super();
        if(session != null) {
            this.session = session; 
        } else {
            this.session = DataConnection.openSession();
        }
    }

    /**
     * Sets all users logged in to this server, as logged out. 
     * @return
     */
    public int release() {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        return uMapper.release(GameServer.getServerName());
    }

    /**
     * Gets a user by username
     * @param username
     * @return
     */
    public User getByUsername(String username) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        return uMapper.getByUsername(username);
    }

    /**
     * Gets a user by its email
     * @param email
     * @return
     */
    public User getByEmail(String email) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        return uMapper.getByEmail(email);
    }

    /**
     * Inserts a new user
     * @param user
     * @return
     */
    public String insert(User user) {
        user.setId(new Utils().genUUID(session));
        UserMapper uMapper = session.getMapper(UserMapper.class);
        uMapper.insert(user);
        return user.getId();
    }

    /**
     * Changes a user's password
     * @param user
     */
    public void changePassword(User user) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        uMapper.changePassword(user);
    }

    /**
     * Updates the user's status to muteban status
     * @param user
     */
    public void updateMuted(User user) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        uMapper.updateMuted(user);
    }

}
