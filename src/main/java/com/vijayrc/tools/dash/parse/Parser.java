package com.vijayrc.tools.dash.parse;

import com.vijayrc.tools.dash.repo.AllConfigs;
import com.vijayrc.tools.dash.repo.AllRuns;
import com.vijayrc.tools.dash.report.Run;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.sort;
import static org.apache.commons.io.FileUtils.*;
import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
@Service
public class Parser {
    private static Logger log = Logger.getLogger(Parser.class);

    @Autowired
    private AllConfigs allConfigs;

    @Autowired
    private AllRuns allRuns;

    /**
     * should create runs by parsing new json dumps and generating data csvs
     */
    public void start() {
        listFiles(new File(allConfigs.getJsonDir()), new String[]{"txt"}, false).forEach(
                jsonFile -> {
                    final String runName = removeEnd(jsonFile.getName(), ".txt").trim();
                    if (allRuns.isNewRun(runName)) {
                        parseRun(jsonFile, runName);
                    }
                });
    }

    public void reparseRun(String runName) {
        final Run run = allRuns.getFor(runName);
        run.reset();
        parseRun(new File(allConfigs.getJsonDir() + "/" + runName + ".txt"), runName);
        log.info("reparsed " + runName);
    }

    private void parseRun(File jsonFile, String runName) {
        final Run run = allRuns.getFor(runName);

        allConfigs.getCategories().forEach(category -> {
            final String categoryName = category.name;
            final String csvName = runName + "-" + categoryName + ".csv";
            final File csvFile = new File(allConfigs.getCsvDir() + "/" + csvName);
            try {
                parseCategory(jsonFile, csvFile, split(category.fields, ","));
            } catch (Exception e) {
                log.error(getFullStackTrace(e));
            }
            run.addCategory(category);
            log.info(runName + "|" + categoryName);
        });
    }

    private void parseCategory(File jsonFile, File csvFile, String... fieldNames) throws Exception {
        sort(fieldNames);

        final List<String> lines = readLines(jsonFile);
        final List<String> fields = asList(fieldNames);
        final CsvLine csvLine = new CsvLine(fieldNames);

        fields.forEach(field -> field = field.trim());
        write(csvFile, join(fields, ",") + "\n");

        lines.forEach(line -> fields.forEach(field -> {
            if (line.contains(field.trim())) {
                if (csvLine.canAdd()) csvLine.append(field, extract(split(line, " : ")[1]));
                else flush(csvFile, csvLine);
            }
            if (line.contains("}") && csvLine.isNotEmpty()) {
                flush(csvFile, csvLine);
            }
        }));
    }

    private void flush(File csvFile, CsvLine csvLine) {
        try {
            write(csvFile, csvLine.flush(), true);
        } catch (IOException e) {
            log.error(getFullStackTrace(e));
        }
    }

    private String extract(String str) {
        String value = removeEnd(str, ",").trim();
        return value.contains("E") ? new BigDecimal(value).toPlainString() : value;
    }


}
