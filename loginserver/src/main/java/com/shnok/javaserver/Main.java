package com.shnok.javaserver;

import com.shnok.javaserver.service.LoginServerListenerService;
import com.shnok.javaserver.service.ServerShutdownService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {
       runServer(args);
    }

    public static void runServer(String... args)  {
        log.info("Starting application.");
//        try {
//            //Config.initializeLog4j();
//            ServerConfig.loadConfig();
//        } catch (Exception e) {
//            log.error("Error while loading config file.", e);
//            return;
//        }

        //ThreadPoolManagerService.getInstance().initialize();
        Runtime.getRuntime().addShutdownHook(ServerShutdownService.getInstance());

        LoginServerListenerService.getInstance().initialize();
        LoginServerListenerService.getInstance().start();
        try {
            LoginServerListenerService.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Application closed");
    }
}
