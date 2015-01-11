// ARGS: HOST, PORT, DB_ID, EXEC_ID, FILE_LOC

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.CoverageProcessor
import net.sourceforge.cobertura.coveragedata.ProjectData
import edu.umd.cs.guitar.util.CoberturaUtils
import edu.umd.cs.guitar.artifacts.ArtifactCategory

// Grab args
println args
host=args[0]
port=args[1]
dbId=args[2]
execId=args[3]
fileLoc=args[4]

// Create a TestDataManager
def manager = new TestDataManager(host, port, dbId)

// Prepare args
ArtifactCategory cat = ArtifactCategory.TEST_OUTPUT
CoverageProcessor cp = new CoverageProcessor(manager.getDb())
opts = [CoverageProcessor.FILE_PATH_OPTION: fileLoc ]

// Save artifact
manager.saveArtifact(cat, cp, opts, execId)
