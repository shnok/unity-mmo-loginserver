package com.shnok.javaserver;

import com.shnok.javaserver.service.*;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.Configurator;

@Log4j2
public class Main {
    public static void main(String[] args) {
       runServer(args);
    }

    public static void runServer(String... args)  {
        log.info("Starting application.");

        Configurator.initialize(null, "conf/log4j2.properties");

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
