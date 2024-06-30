package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBGameServer;

import java.util.List;

public interface GameServerDao {
    public List<DBGameServer> getAllGameServers();
    public void addGameServer(DBGameServer gameServer);
}
