package com.shnok.javaserver.service;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class LoginServerListenerService extends Thread {
    private int port;
    private ServerSocket serverSocket;

    private static LoginServerListenerService instance;
    public static LoginServerListenerService getInstance() {
        if (instance == null) {
            instance = new LoginServerListenerService();
        }
        return instance;
    }

    public void initialize() {
        try {
            port = server.loginserverPort();
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Could not create ServerSocket ", e);
        }
    }

    @Override
    public void run() {
        log.info("Login server listening on port {}. ", port);
        while (true) {
            Socket connection = null;
            try {
                connection = serverSocket.accept();
                LoginServerController.getInstance().addClient(connection);
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
}
