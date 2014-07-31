/*  
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer.experiment;

import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.JFCDefaultIDGeneratorSimple;
import edu.umd.cs.guitar.model.data.EventMapType;
import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.data.WidgetMapType;
import edu.umd.cs.guitar.replayer.experiment.monitor.GStateMonitorExp;
import edu.umd.cs.guitar.replayer.monitor.CoberturaCoverageMonitor;
import edu.umd.cs.guitar.replayer.monitor.GTestMonitor;
import edu.umd.cs.guitar.replayer.monitor.TimeMonitor;
import edu.umd.cs.guitar.testdata.TestDataManager;
import edu.umd.cs.guitar.util.GUITARLog;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

//import edu.umd.cs.guitar.replayer.monitor.JFCTerminalMonitor;

public class JFCSMUTReplayerMain {
    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("JFCSMUTReplayer is starting...");

        // Note: We don't actually use this configuration object
        // we only use this object to initialize the static fields of
        // JFCReplayerConfiguration
        JFCReplayerConfigurationExp configuration = new JFCReplayerConfigurationExp();
        CmdLineParser parser = new CmdLineParser(configuration);
        try {
            parser.parseArgument(args);

            GReplayerExp sReplayer = null;
            if (JFCReplayerConfigurationExp.TESTDATA_TEST_ID != null) {
                sReplayer = setUpReplayerFromDB(configuration);
            } else {
                sReplayer = setUpReplayer(configuration);
            }
            sReplayer.execute();
            GUITARLog.log.info("NORMALLY TERMINATED");
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println();
            System.err.println("Usage: java [JVM options] "
                    + JFCReplayerConfigurationExp.class.getName()
                    + " [Relayer options] \n");
            System.err.println("where [Replayer options] include:");
            System.err.println();
            parser.printUsage(System.err);

        } catch (GException e) {
            GUITARLog.log.error("GUITAR Exception thrown", e);

        } catch (Exception e) {
            GUITARLog.log.error("General Exception thrown", e);
        }
        System.exit(0);
    }

    private static GReplayerExp setUpReplayer(JFCReplayerConfigurationExp configuration) {

        // ---------------------------------
        // Parsing configuration file

        // Event Map
        GUIMap guiMap = (GUIMap) IO.readObjFromFile(configuration.GUI_MAP_FILE,
                GUIMap.class);

        // Test case
        TestCase testCase = (TestCase) IO.readObjFromFile(
                configuration.TESTCASE, TestCase.class);

        // Finish
        return finishReplayerSetup(configuration, guiMap, testCase);

    }

    private static GReplayerExp finishReplayerSetup(JFCReplayerConfigurationExp configuration, GUIMap guiMap, TestCase testCase) {

        WidgetMapType widgetMap = guiMap.getWidgetMap();
        EventMapType eventMap = guiMap.getEventMap();
        GReplayerExp sReplayer = new GReplayerExp(widgetMap, eventMap, testCase);

        // Platform-specific replayer monitor
        GReplayerMonitorExp jfcsReplayerMonitor = new JFCReplayerMonitorExp(
                configuration.MAIN_CLASS, configuration.INITIAL_WAITING_TIME);

        ((JFCReplayerMonitorExp) jfcsReplayerMonitor)
                .setUseReg(configuration.REG_USED);
        sReplayer.setReplayerMonitor(jfcsReplayerMonitor);

        // -------------------------
        // Test monitors

        // TimeOut
        GTestMonitor timeoutMonitor = new TimeMonitor(
                configuration.TESTSTEP_TIMEOUT, configuration.TESTCASE_TIMEOUT);

        sReplayer.addTestMonitor(timeoutMonitor);

        // State
        GTestMonitor stateMonitor = new GStateMonitorExp(
                configuration.GUI_STATE_FILE, configuration.DELAY);

        // GIDGenerator idGenerator = JFCDefaultIDGenerator.getInstance();
        GIDGenerator idGenerator = JFCDefaultIDGeneratorSimple.getInstance();
        ((GStateMonitorExp) stateMonitor).setIdGenerator(idGenerator);

        sReplayer.addTestMonitor(stateMonitor);

        // Add a Cobertura code coverage collector
        boolean isMeasureCoverage = (configuration.COVERAGE_DIR != null && configuration.COVERAGE_CLEAN_FILE != null);
        if (isMeasureCoverage) {
            GTestMonitor coverageMonitor = new CoberturaCoverageMonitor(
                    configuration.COVERAGE_CLEAN_FILE,
                    configuration.COVERAGE_DIR);
            sReplayer.addTestMonitor(coverageMonitor);

        }

        return sReplayer;

    }

    private static GReplayerExp setUpReplayerFromDB(JFCReplayerConfigurationExp configuration) {

        // ---------------------------------
        // Load objects from testdata DB

        TestDataManager tdm = new TestDataManager(JFCReplayerConfigurationExp.TESTDATA_HOST, Integer.parseInt(JFCReplayerConfigurationExp.TESTDATA_PORT));

        TestCase testCase = tdm.getTestCase(JFCReplayerConfigurationExp.TESTDATA_DB_ID,JFCReplayerConfigurationExp.TESTDATA_TEST_ID);
        GUIMap guiMap = tdm.getGUIMap(JFCReplayerConfigurationExp.TESTDATA_DB_ID, JFCReplayerConfigurationExp.TESTDATA_TEST_ID);

        return finishReplayerSetup(configuration, guiMap, testCase);
    }


}
