// ARGS: HOST, PORT, DB_ID, EXEC_ID, COVERAGE_DIR

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.CoverageProcessor
import edu.umd.cs.guitar.processors.guitar.LogProcessor
import net.sourceforge.cobertura.coveragedata.ProjectData
import edu.umd.cs.guitar.util.CoberturaUtils
import edu.umd.cs.guitar.artifacts.ArtifactCategory
import groovy.io.FileType

// Grab args
println args
host=args[0]
port=args[1]
dbId=args[2]
execId=args[3]
coverageDir=args[4]

// Create a TestDataManager
def manager = new TestDataManager(host, port, dbId)

// Prepare args
ArtifactCategory cat = ArtifactCategory.TEST_OUTPUT
CoverageProcessor cp = new CoverageProcessor(manager.getDb())
LogProcessor lp = new LogProcessor()

// Get list of coverage files from coverageDir
fileList = []
indexList = []
def logFile = null

def dir = new File(coverageDir)
dir.eachFileRecurse (FileType.FILES) { file ->
  def splits = file.getName().toString().split("\\.")
  if(splits[-1].equals("ser") && (! (splits[0].charAt(0) == 't'))){
    indexList << file.getName().toString().split("\\.")[0]
    fileList << file.getAbsolutePath()
  }
	if(splits[-1].equals("log")){
        logFilePath = file.getAbsolutePath()
	}
}

//println fileList

// Save each coverage artifact
//fileList.eachWithIndex(){ path, i ->
//	def opts = [(CoverageProcessor.FILE_PATH_OPTION): path, (CoverageProcessor.INDEX_OPTION): indexList[i] ]
//	manager.saveArtifact(cat, cp, opts, execId)
//}

// Save log artifact
def opts = [(LogProcessor.FILE_PATH_OPTION): logFilePath]
manager.saveArtifact(cat, lp, opts, execId)
