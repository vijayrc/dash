package com.vijayrc.tools.dash.report;

import com.vijayrc.tools.dash.repo.AllConfigs;
import com.vijayrc.tools.dash.repo.AllRuns;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FileUtils.readFileToString;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
@Service
public class Report {

    private static Logger log = Logger.getLogger(Report.class);

    private AllConfigs allConfigs;

    private AllRuns allRuns;

    @Autowired
    public Report(AllConfigs allConfigs, AllRuns allRuns) {
        this.allConfigs = allConfigs;
        this.allRuns = allRuns;
    }

    public void publish(String runName, Model model) {
        Run run = allRuns.getFor(runName);
        if (run == null)
            throw new RuntimeException("Run " + runName + " not found");

        model.addAttribute("run", run);
        model.addAttribute("categories", run.getCategories());
        model.addAttribute("csvDir", allConfigs.getCsvDir());
        model.addAttribute("today", LocalDateTime.now().toString());
        log.info("published " + runName);
    }

    public String getCsvFor(String csvFile) throws IOException {
        File dataFile = getFile(new File(allConfigs.getCsvDir()), csvFile + ".csv");
        return readFileToString(dataFile);
    }

}
