package com.shnok.javaserver;

import com.shnok.javaserver.service.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {
       runServer(args);
    }

    public static void runServer(String... args)  {
        log.info("Starting application.");

        ThreadPoolManagerService.getInstance().initialize();
        Runtime.getRuntime().addShutdownHook(ServerShutdownService.getInstance());

        LoginServerListenerService.getInstance().initialize();
        LoginServerListenerService.getInstance().start();

        GameServerController.getInstance();
        GameServerListenerService.getInstance().initialize();
        GameServerListenerService.getInstance().start();
        try {
            LoginServerListenerService.getInstance().join();
            GameServerListenerService.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Application closed");
    }
}
