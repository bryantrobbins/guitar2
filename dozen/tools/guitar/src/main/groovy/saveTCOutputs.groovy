// ARGS: HOST, PORT, DB_ID, EXEC_ID, COVERAGE_DIR

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.CoverageProcessor
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

// Get list of coverage files from coverageDir
fileList = []
indexList = []
def dir = new File(".")
dir.eachFileRecurse (FileType.FILES) { file ->
  def splits = file.getName().toString().split("\\.")
  if(splits[-1].equals("ser")){
    indexList << file.getName().toString().split("\\.")[0]
    fileList << file.getName()
  }
}

// Save each artifact
def index = 0
fileList.each(){
	opts = [CoverageProcessor.FILE_PATH_OPTION: it, CoverageProcessor.INDEX_OPTION: index]
	manager.saveArtifact(cat, cp, opts, execId)
}
