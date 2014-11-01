/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland
 *  . Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without
 *  restriction, including without
 *  limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to
 *	do so, subject to the following
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in
  *	all copies or substantial
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *	EXPRESS OR IMPLIED, INCLUDING BUT NOT
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *	PURPOSE AND NONINFRINGEMENT. IN NO
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *	DAMAGES OR OTHER LIABILITY, WHETHER
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.replayer;

import edu.umd.cs.guitar.util.Util;
import org.kohsuke.args4j.Option;

;

/**
 * Class contains the runtime configurations of JFC GUITAR Replayer
 * <p/>
 * <p/>
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class JFCReplayerConfiguration extends GReplayerConfiguration {

    @Option(name = "-cf", usage = "configure file for the gui recorder to " +
            "recognize the terminal widgets", aliases = "--configure-file")
    public static String CONFIG_FILE = // "resources" + File.separator +
            // "config"
            // + File.separator +
            "configuration.xml";

    @Option(name = "-gs", usage = "gui state file path",
            aliases = "--gui-state")
    static String GUI_STATE_FILE = "GUITAR-Default.STA";

    @Option(name = "-l", usage = "log file name ", aliases = "--log-file")
    static String LOG_FILE = Util.getTimeStamp() + ".log";
    ;

    @Option(name = "-i", usage = "initial waiting time for the application to" +
            " get stablized before being ripped", aliases = "--wait-time")
    static int INITIAL_WAITING_TIME = 0;

    @Option(name = "-d", usage = "step delay time", aliases = "--delay")
    static int DELAY = 0;

    @Option(name = "-to", usage = "test case timeout",
            aliases = "--testcase-timeout")
    static int TESTCASE_TIMEOUT = 30000;

    @Option(name = "-so", usage = "test step timeout",
            aliases = "--teststep-timeout")
    static int TESTSTEP_TIMEOUT = 4000;

    // Application Under Test
    @Option(name = "-c", usage = "<REQUIRED> main class name for the " +
            "Application Under Test ", aliases = "--main-class")
    static String MAIN_CLASS = null;

    @Option(name = "-a", usage = "arguments for the Application Under Test, " +
            "separated by ':' ", aliases = "--arguments")
    static String ARGUMENT_LIST;

    @Option(name = "-u", usage = "URLs for the Application Under Test, " +
            "separated by ':' ", aliases = "--urls")
    static public String URL_LIST;

    @Option(name = "-p", usage = "pause after each step", aliases = "--pause")
    static boolean PAUSE = false;

    @Option(name = "-r", usage = "compare string using regular expression",
            aliases = "--regular-expression")
    static boolean USE_REG = false;

    @Option(name = "-m", usage = "use image based identification for GUI " +
            "components", aliases = "--image")
    static boolean USE_IMAGE = false;

    // Cobertura Coverage collection
    @Option(name = "-cd", usage = "cobertura coverage output dir",
            aliases = "--coverage-dir")
    static String COVERAGE_DIR = null;

    @Option(name = "-cc", usage = "cobertura coverage clean file ",
            aliases = "--coverage-clean")
    static String COVERAGE_CLEAN_FILE = null;

    @Option(name = "-jar", usage = "automatically looking for the main class " +
            "name in jar file specified by -c")
    public static boolean USE_JAR = false;

    @Option(name = "-ts", usage = "automatically searching and perform " +
            "terminal button to fully terminate the test case",
            aliases = "--terminal-search")
    public static boolean TERMINAL_SEARCH = false;

    @Option(name = "-tdi", usage = "testdata manager test id to be replayed",
            aliases = "--testdata-test-id")
    public static String TESTDATA_TEST_ID = "";

    @Option(name = "-tds", usage = "testdata manager suite id to be replayed",
            aliases = "--testdata-suite-id")
    public static String TESTDATA_SUITE_ID = "";

    @Option(name = "-tdd", usage = "testdata manager db id to use",
            aliases = "--testdata-db-id")
    public static String TESTDATA_DB_ID = "";

    @Option(name = "-tdh", usage = "testdata manager host to use",
            aliases = "--testdata-host")
    public static String TESTDATA_HOST = "";

    @Option(name = "-tdp", usage = "testdata manager port to use",
            aliases = "--testdata-port")
    public static String TESTDATA_PORT = "";

}
