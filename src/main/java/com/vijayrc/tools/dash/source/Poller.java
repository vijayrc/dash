package com.vijayrc.tools.dash.source;

import com.vijayrc.tools.dash.repo.AllConfigs;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Timer;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
@Service
public class Poller {
    private static Logger log = Logger.getLogger(Poller.class);

    @Autowired
    private AllConfigs allConfigs;

    private Timer timer;

    public Poller() throws Exception {
        this.timer = new Timer(true);
    }

    public void start(String runName) {
        timer.cancel();
        timer.purge();
        timer = new Timer(true);

        allConfigs.getSources().forEach(source -> timer.schedule(new PollerTask(source, allConfigs.getJsonDir(), runName), 0, source.getPoll() * 1000));
        log.info("all pollers started");
    }

    public void stop() {
        timer.cancel();
        timer.purge();
        log.info("all pollers stopped");
    }

}
