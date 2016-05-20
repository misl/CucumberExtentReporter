package com.cucumber.listener;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import cucumber.runtime.CucumberException;
import cucumber.runtime.io.URLOutputStream;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Cucumber custom format listener which generates ExtentsReport html file
 */
public class ExtentCucumberFormatter implements Reporter, Formatter {

    private ExtentReports extent;
    private ExtentTest featureTest;
    private ExtentTest scenarioTest;
    private LinkedList<Step> testSteps;
    private File htmlReportDir;

    private static final Map<String, String> MIME_TYPES_EXTENSIONS = new HashMap() {
        {
            this.put("image/bmp", "bmp");
            this.put("image/gif", "gif");
            this.put("image/jpeg", "jpg");
            this.put("image/png", "png");
            this.put("image/svg+xml", "svg");
            this.put("video/ogg", "ogg");
        }
    };

    public ExtentCucumberFormatter(File filePath) {

        if (!filePath.getPath().equals("")) {
            String reportPath = filePath.getPath();
            this.htmlReportDir = new File(reportPath);
            this.extent = new ExtentReports(reportPath);
        } else {
            String reportDir = "output/Run_" + System.currentTimeMillis();
            this.htmlReportDir = new File(reportDir);
            this.extent = new ExtentReports(reportDir + "/report.html");
        }

        this.testSteps = new LinkedList<Step>();
    }

    public void before(Match match, Result result) {

    }

    public void result(Result result) {
        if ("passed".equals(result.getStatus())) {
            scenarioTest.log(LogStatus.PASS, testSteps.poll().getName(), "PASSED");
        } else if ("failed".equals(result.getStatus())) {
            scenarioTest.log(LogStatus.FAIL, testSteps.poll().getName(), result.getErrorMessage());
        } else if ("skipped".equals(result.getStatus())) {
            scenarioTest.log(LogStatus.SKIP, testSteps.poll().getName(), "SKIPPED");
        } else if ("undefined".equals(result.getStatus())) {
            scenarioTest.log(LogStatus.UNKNOWN, testSteps.poll().getName(), "UNDEFINED");
        }
    }

    public void after(Match match, Result result) {

    }

    public void match(Match match) {

    }

    public void embedding(String s, byte[] bytes) {
        String extension = (String)MIME_TYPES_EXTENSIONS.get(s);
        String fileName = "screenshot-" + System.currentTimeMillis() + "." + extension;
        this.writeBytesAndClose(bytes, this.reportFileOutputStream(fileName));
        scenarioTest.log(LogStatus.INFO, scenarioTest.addScreenCapture(fileName));
    }

    public void write(String s) {
        scenarioTest.log(LogStatus.INFO, s);
    }

    public void syntaxError(String s, String s1, List<String> list, String s2, Integer integer) {

    }

    public void uri(String s) {

    }

    public void feature(Feature feature) {
        featureTest = extent.startTest("Feature: " + feature.getName());
    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {

    }

    public void examples(Examples examples) {

    }

    public void startOfScenarioLifeCycle(Scenario scenario) {
        scenarioTest = extent.startTest("Scenario: " + scenario.getName());

        for (Tag tag : scenario.getTags()) {
            scenarioTest.assignCategory(tag.getName());
        }
    }

    public void background(Background background) {

    }

    public void scenario(Scenario scenario) {

    }

    public void step(Step step) {
        testSteps.add(step);
    }

    public void endOfScenarioLifeCycle(Scenario scenario) {
        extent.endTest(scenarioTest);
        featureTest.appendChild(scenarioTest);
    }

    public void done() {

    }

    public void close() {
        extent.close();
    }

    public void eof() {
        extent.endTest(featureTest);
        extent.flush();
    }

    private OutputStream reportFileOutputStream(String fileName) {
        try {
            return new URLOutputStream(new URL(this.htmlReportDir.toURI().toURL(), fileName));
        } catch (IOException var3) {
            throw new CucumberException(var3);
        }
    }

    private void writeBytesAndClose(byte[] buf, OutputStream out) {
        try {
            out.write(buf);
        } catch (IOException var4) {
            throw new CucumberException("Unable to write to report file item: ", var4);
        }
    }

}
