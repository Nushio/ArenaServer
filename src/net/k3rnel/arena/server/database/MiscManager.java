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

/**
 * MiscManager manages the database queries related to the Misc Table
 * Using MyBatis, we can set all queries in an XML file, and make managing
 * queries an easier task. 
 * 
 * This requires the MiscMapper.xml, and a MiscMapper.java Interface. 
 * 
 * @author Nushio
 *
 */
public class MiscManager {

    private SqlSession session;

    /**
     * Initializes the MiscManager
     * @param session
     */
    public MiscManager(SqlSession session) {
        super();
        if(session != null) {
            this.session = session; 
        } else {
            this.session = DataConnection.openSession();
        }
    }
        
    /**
     * Updates the version to whatever number is set here. 
     * @param version
     * @throws Exception
     */
    public Misc getByName(String name) throws SQLException {
        MiscMapper mMapper = session.getMapper(MiscMapper.class);
        return mMapper.getByName(name);
    }
    
    /**
     * Inserts the name-value pair. 
     * @return version
     * @throws Exception
     */
    public void insert(Misc misc) throws SQLException {
        MiscMapper mMapper = session.getMapper(MiscMapper.class);
        mMapper.insert(misc);
    }
    
    /**
     * Updates the name-value pair. 
     * @return version
     * @throws Exception
     */
    public void update(Misc misc) throws SQLException {
        MiscMapper mMapper = session.getMapper(MiscMapper.class);
        mMapper.update(misc);
    }

}
