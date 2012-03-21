package net.k3rnel.arena.server.database;

import net.k3rnel.arena.server.GameServer;
import net.k3rnel.arena.server.utils.Utils;

import org.apache.ibatis.session.SqlSession;

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

    public int release() {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        return uMapper.release(GameServer.getServerName());
    }

    public User getByUsername(String username) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        return uMapper.getByUsername(username);
    }

    public User getByEmail(String email) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        return uMapper.getByEmail(email);
    }

    public String insert(User user) {
        user.setId(new Utils().genUUID(session));
        UserMapper uMapper = session.getMapper(UserMapper.class);
        uMapper.insert(user);
        return user.getId();
    }

    public void changePassword(User user) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        uMapper.changePassword(user);
    }

    public void updateMuted(User user) {
        UserMapper uMapper = session.getMapper(UserMapper.class);
        uMapper.updateMuted(user);
    }

}
