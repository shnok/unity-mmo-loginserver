package com.shnok.javaserver.service.db;

import com.shnok.javaserver.db.entity.DBGameServer;
import com.shnok.javaserver.db.repository.GameServerRepository;

import java.util.List;

public class GameServerTable {
    private final GameServerRepository gameServerRepository;
    private static GameServerTable instance;
    public static GameServerTable getInstance() {
        if (instance == null) {
            instance = new GameServerTable();
        }
        return instance;
    }

    public GameServerTable() {
        gameServerRepository = new GameServerRepository();
    }

    public List<DBGameServer> getAllGameServers() {
        return gameServerRepository.getAllGameServers();
    }

    public void addGameServer(DBGameServer gameServer) {
        gameServerRepository.addGameServer(gameServer);
    }
}
