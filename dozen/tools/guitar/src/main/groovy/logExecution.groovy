// args master, port, db_id_val, bundle_id_val, test_id_val, exec_id_val, suite_id_val

import edu.umd.cs.guitar.main.TestDataManager

def master=args[0]
def port=args[1]
def dbId = args[2]
def bundleId = args[3]
def testId = args[4]
def execId = args[5]
def suiteId = args[6]

println args

// TestDataManager
def manager = new TestDataManager(master, port, dbId)

// Save execution info into bundle
manager.addExecutionToBundle(execId, bundleId, testId, suiteId)
