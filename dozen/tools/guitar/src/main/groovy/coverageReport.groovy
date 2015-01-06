// ARGS: DB_ID, TEST_ID

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.CoverageProcessor
import net.sourceforge.cobertura.coveragedata.ProjectData
import edu.umd.cs.guitar.util.CoberturaUtils
import edu.umd.cs.guitar.artifacts.ArtifactCategory

// TestDataManager
println args

def manager = new TestDataManager("192.168.59.103", "37017", args[0])

// Get coverage data as binary artifact
ArtifactCategory cat = ArtifactCategory.TEST_OUTPUT
CoverageProcessor cp = new CoverageProcessor(manager.getDb())
println("Category=" + cat)
println("ExecID=" + args[1])
println("Processor=" + cp)

ProjectData pd = (ProjectData) manager.getArtifactByCategoryAndOwnerId(cat, args[1], cp)
println("Data=" + pd)

// Convert to coverge report String
def report = CoberturaUtils.getCoverageReportFromCoverageObject(pd)

// Print report
println(report)
