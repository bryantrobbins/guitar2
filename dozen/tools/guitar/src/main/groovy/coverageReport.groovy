// ARGS: DB_ID, TEST_ID

import edu.umd.cs.guitar.main.TestDataManager
import edu.umd.cs.guitar.processors.guitar.CoverageProcessor
import net.sourceforge.cobertura.coveragedata.ProjectData
import edu.umd.cs.guitar.util.CoberturaUtils

// TestDataManager
def manager = new TestDataManager("mongo", "27017", args[1])

// Get coverage data as binary artifact
ProjectData pd = manager.getArtifactByCategoryAndOwnerId(ArtifactCategory.TEST_OUTPUT,
                                        argv[1],
                                        new CoverageProcessor(argv[0]))

// Convert to coverge report String
def report = CoberturaUtils.getCoverageReportFromCoverageObject(pd)

// Print report
println(report)
