package com.quartyom.lab6.SceneControllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionsThread extends Thread{
    private static final Logger log = LoggerFactory.getLogger("scenes");
    ManageSceneController controller;
    long timeout;
    public ConnectionsThread(ManageSceneController controller) {
        this.controller = controller;

        String val = controller.application.properties.getProperty("reconnectionTimeoutMs");
        try {
            timeout = Long.parseLong(val);
        } catch (NumberFormatException e) {
            timeout = 1000;
        }
    }

    @Override
    public void run(){
        log.info("Connections thread was started");
        while (true) {
            long delta = System.currentTimeMillis() - controller.last_time_updated;
            if (delta >= timeout) {
                controller.updateConnections();
            }
            else {
                try {
                    sleep(timeout - delta);
                }
                catch (InterruptedException e) {
                    log.info("Connections thread was interrupted");
                    return;
                }
            }
        }
    }
}
