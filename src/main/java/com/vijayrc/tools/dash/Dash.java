package com.vijayrc.tools.dash;

import com.vijayrc.tools.dash.parse.Parser;
import com.vijayrc.tools.dash.repo.AllRuns;
import com.vijayrc.tools.dash.report.Report;
import com.vijayrc.tools.dash.report.Run;
import com.vijayrc.tools.dash.source.Poller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
@Service
public class Dash {

    @Autowired
    private Parser parser;

    @Autowired
    private Report report;

    @Autowired
    private Poller poller;

    @Autowired
    private AllRuns allRuns;

    private int runsCount;

    public void home(Model model) {
        Collection<Run> runs = allRuns.loadOldRuns();
        model.addAttribute("runs", runs);
        model.addAttribute("existCount", runs.size());
        model.addAttribute("runsCount", runsCount);
    }

    public void start(String runName, Model model) throws Exception {
        allRuns.startNew(runName);
        poller.start(runName);
        model.addAttribute("runsCount", ++runsCount);
    }

    public void stopAll(Model model) throws Exception {
        allRuns.stopAll();
        runsCount = 0;
        model.addAttribute("runsCount", runsCount);
        poller.stop();
        parser.start();
    }

    public void report(String runName, Model model) {
        report.publish(runName, model);
    }

    public String dataFor(String csvFile) throws IOException {
        return report.getCsvFor(csvFile);
    }

    public void redoReport(String runName, Model model) {
        parser.reparseRun(runName);
        report.publish(runName, model);
    }
}
