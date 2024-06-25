package com.shnok.javaserver.service;

import com.shnok.javaserver.thread.GameServerThread;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class GameServerListenerService extends Thread {
    private final List<GameServerThread> gameservers = new ArrayList<>();

    private int port;
    private ServerSocket serverSocket;

    private static GameServerListenerService instance;
    public static GameServerListenerService getInstance() {
        if (instance == null) {
            instance = new GameServerListenerService();
        }
        return instance;
    }

    public void initialize() {
        try {
            port = server.gameserverPort();
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Could not create ServerSocket ", e);
        }
    }

    @Override
    public void run() {
        log.info("Listening for new gameservers on port: {}. ", port);
        while (true) {
            Socket connection = null;
            try {
                connection = serverSocket.accept();
                addGameServer(connection);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                if (isInterrupted()) {
                    try {
                        serverSocket.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public void addGameServer(Socket socket) {
        GameServerThread client = new GameServerThread(socket);
        client.start();
        gameservers.add(client);
    }

    public void removeGameServer(GameServerThread s) {
        synchronized (gameservers) {
            gameservers.remove(s);
        }
    }
}