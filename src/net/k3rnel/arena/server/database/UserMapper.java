package net.k3rnel.arena.server.database;

public interface UserMapper {

    int release(String serverName);

    User getByUsername(String username);

    User getByEmail(String email);

    void insert(User user);

    void changePassword(User user);

    void updateMuted(User user);

}
