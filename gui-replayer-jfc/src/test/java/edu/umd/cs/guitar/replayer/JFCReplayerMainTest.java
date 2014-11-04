package edu.umd.cs.guitar.replayer;

import org.junit.Test;

public class JFCReplayerMainTest {

    @Test
    public void testMain() throws Exception {
        String aut_mainclass_val = "";
        String test_id_val = "t_e802842950_e103778092";
        String suite_id_val = "amalga_JabRef_sq_l_2";
        String db_id_val = "amalga_jenkins-generate-sl2-52";

        String[] args = {"-c", aut_mainclass_val, "-tdi", test_id_val,
                "-tds", suite_id_val, "-tdd", db_id_val, "-tdh", "mongo",
                "-tdp", "27017"};
//        JFCReplayerMain.main(args);
    }
}