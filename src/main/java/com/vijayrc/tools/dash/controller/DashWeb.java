package com.vijayrc.tools.dash.controller;

import com.vijayrc.tools.dash.Dash;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Vijay Chakravarthy@vijayrc.com
 * @version %I% %G%
 * @since 1.0
 */
@Controller
@EnableAutoConfiguration
public class DashWeb {

    Logger logger = Logger.getRootLogger();

    @Autowired
    private Dash dash;

    @RequestMapping("/")
    public String home(Model model) throws Exception {
        try {
            dash.home(model);
        } catch (Exception e) {
            model.addAttribute("msg", "Dash failed to start: " + getFullStackTrace(e));
        }
        return "home";
    }

    @RequestMapping(value = "/start", method = POST)
    public String start(@RequestParam("build") String runName, Model model) throws Exception {
        model.addAttribute("msg", "Dash started successfully");
        try {
            dash.start(runName, model);
            model.addAttribute("msg", "Dash start: ");
            logger.debug("Dash start");
        } catch (Exception e) {
            model.addAttribute("msg", "Dash failed to start: " + getFullStackTrace(e));
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/stop", method = POST)
    public String stop(Model model) throws Exception {
        model.addAttribute("msg", "Dash stopped successfully");
        try {
            dash.stopAll(model);
            model.addAttribute("msg", "Dash stopAll: ");
        } catch (Exception e) {
            model.addAttribute("msg", "Dash failed to stopAll: " + getFullStackTrace(e));
        }
        return "redirect:/";
    }

    @RequestMapping("/runs/view")
    public String fetchRun(@RequestParam("runName") String runName, Model model) throws Exception {
        try {
            dash.report(runName, model);
        } catch (Exception e) {
            model.addAttribute("msg", "Dash failed to publish report: " + getFullStackTrace(e));
        }
        return "report";
    }

    @RequestMapping("/runs/redo")
    public String redoRun(@RequestParam("runName") String runName, Model model) throws Exception {
        try {
            dash.redoReport(runName, model);
        } catch (Exception e) {
            model.addAttribute("msg", "Dash failed to publish report: " + getFullStackTrace(e));
        }
        return "report";
    }

    @RequestMapping("/csvs")
    public void fetchCSV(@RequestParam("csvFile") String csvFile, HttpServletResponse response) throws Exception {
        String content = dash.dataFor(csvFile);
        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.flush();
        writer.close();
    }

}
