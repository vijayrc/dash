package com.vijayrc.tools.dash.repo;

import com.vijayrc.tools.dash.report.Run;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.lang.StringUtils.*;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
@Repository
public class AllRuns {

    private static Logger log = Logger.getLogger(AllRuns.class);

    @Autowired
    private AllConfigs allConfigs;

    private Map<String, Run> runs = new TreeMap<>();

    public Run getFor(String name) {
        return runs.get(name);
    }

    /**
     * will recreate runs from json dump files and the csv files.
     */
    public Collection<Run> loadOldRuns() {
        try {
            File jsonDir = new File(allConfigs.getJsonDir());
            File csvDir = new File(allConfigs.getCsvDir());
            listFiles(jsonDir, new String[]{"txt"}, false).forEach(
                    jsonFile -> {

                        final String runName = removeEnd(jsonFile.getName(), ".txt").trim();
                        if (!runs.keySet().contains(runName)) {
                            String[] split = split(runName, "-");
                            String sourceName = split[1];

                            final Run run = new Run(runName, allConfigs.getSourceFor(sourceName), Run.State.Stopped);

                            listFiles(csvDir, getFileFilter(runName), null).forEach(csv -> {
                                String categoryName = remove(remove(csv.getName(), runName + "-"), ".csv");
                                run.addCategory(allConfigs.getCategoryFor(categoryName));
                            });
                            runs.put(runName, run);
                            log.info("loaded " + run);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        log.info("existing runs: " + runs.size());
        return runs.values();
    }

    private IOFileFilter getFileFilter(final String runName) {
        return new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().contains(runName);
            }

            @Override
            public boolean accept(File file, String s) {
                return file.getName().contains(runName);
            }
        };
    }

    public boolean isNewRun(String runName) {
        return !runs.keySet().contains(runName);
    }

    public void startNew(String runName) {
        allConfigs.getSources().forEach(source -> {
            final String runComboName = runName + "-" + source.getName();
            if (runs.containsKey(runComboName))
                throw new RuntimeException("Run already exists - " + runComboName);
            runs.put(runComboName, new Run(runComboName, source).start());
        });

    }

    public void stopAll() {
        runs.keySet().forEach(key -> {
            if (runs.get(key).isNew()) runs.get(key).stop();
        });
    }
}
