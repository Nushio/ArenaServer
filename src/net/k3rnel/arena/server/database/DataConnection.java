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

import java.io.Reader;
import java.util.Properties;

import net.k3rnel.arena.server.GameServer;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author Nushio
 */
public class DataConnection {

    private static final SqlSessionFactory factory;

    static{
        try {
            Properties properties = new Properties();
            
            properties.put("url", "jdbc:mysql://" + GameServer.getDatabaseHost()+":3306/"+GameServer.getDatabaseName()+"?autoReconnect=true");
            properties.put("username", GameServer.getDatabaseUsername());
            properties.put("password", GameServer.getDatabasePassword());
            Reader reader = Resources.getResourceAsReader("MySQLConfig.xml");
            factory = new SqlSessionFactoryBuilder().build(reader,properties);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Fatal Error.  Cause: " + e, e);
        }
    }

    public static SqlSession openSession(){
        SqlSession session = factory.openSession();
        return session;

    }
}