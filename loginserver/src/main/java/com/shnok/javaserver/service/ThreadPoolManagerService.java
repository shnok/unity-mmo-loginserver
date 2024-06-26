package com.shnok.javaserver.service;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ThreadPoolManagerService {
    private ThreadPoolExecutor packetsThreadPool;
    private boolean shutdown = false;

    private static ThreadPoolManagerService instance;
    public static ThreadPoolManagerService getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManagerService();
        }
        return instance;
    }

    public void initialize() {
        log.info("Initializing thread pool manager service.");
        packetsThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void handlePacket(Thread cph) {
        packetsThreadPool.execute(cph);
    }

    public void shutdown() {
        shutdown = true;

        packetsThreadPool.shutdown();

        purge();

        log.info("All ThreadPools are now purged.");
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void purge() {
        packetsThreadPool.purge();
    }
}
