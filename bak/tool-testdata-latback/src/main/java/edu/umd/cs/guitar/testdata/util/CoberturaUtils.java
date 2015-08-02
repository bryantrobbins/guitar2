package edu.umd.cs.guitar.testdata.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.CoverageData;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.LineData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.reporting.xml.XMLReport;
import net.sourceforge.cobertura.util.FileFinder;
import net.sourceforge.cobertura.util.IOUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import edu.umd.cs.guitar.model.data.GUIMap;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testdata.TestDataManagerDefaults;

public class CoberturaUtils {

	private static Logger logger = LogManager.getLogger(CoberturaUtils.class);

	public static ProjectData getCoverageObjectFromGFS(DB db, String handle)
			throws IOException {

		// GridFS object
		GridFS gfsCoverage = new GridFS(db,
				TestDataManagerDefaults.COLLECTION_COVERAGE);

		// Coverage object from GridFS
		GridFSDBFile serOutput = gfsCoverage.findOne(handle);

		// Prepare output stream
		ByteArrayOutputStream serStreamOut = new ByteArrayOutputStream();
		serOutput.writeTo(serStreamOut);

		// Convert to ProjectData object
		return loadCoverageData(new ByteArrayInputStream(serStreamOut
						.toByteArray()));

	}

	public static String getCoverageReportFromCoverageObject(ProjectData pd)
			throws IOException {
		// Object needed for report generation
		// This method ignores sources, though we could hook source dirs in at
		// this point
		FileFinder sources = new FileFinder();

		// Need an output stream for Cobertura to use in-memory
		ByteArrayOutputStream coverageOut = new ByteArrayOutputStream();

		// The way Cobertura is written, the constructor of XMLReport writes to
		// file
		// I modified the same constructor to write to output stream, but
		// returned object is
		// still useless

		XMLReport dummy = new XMLReport(pd, coverageOut, sources,
				new ComplexityCalculator(sources));

		// Convert to String
		return coverageOut.toString(Charset.forName("UTF-8").name());
	}

	public static int addedCoveredLines(ProjectData current, ProjectData updated) {
		int lineCount = 0;

		for (Object obj : updated.getClasses()) {
			ClassData classData = (ClassData) obj;
			String name = classData.getName();
			for (Object covData : classData.getLines()) {
				LineData updatedLine = (LineData) covData;
				if (updatedLine.isCovered()) {
					int num = updatedLine.getLineNumber();
					LineData currentLine = current.getClassData(name)
							.getLineData(num);
					if (!currentLine.isCovered()) {
						lineCount += 1;
					}
				}
			}
		}

		return lineCount;
	}

	public static boolean doesCoverageMeetGoal(ProjectData goal,
			ProjectData candidate) {
		for (Object obj : goal.getClasses()) {
			ClassData classData = (ClassData) obj;
			String name = classData.getName();
			for (Object covData : classData.getLines()) {
				LineData goalLine = (LineData) covData;
				if (goalLine.isCovered()) {
					int num = goalLine.getLineNumber();
					LineData candidateLine = candidate.getClassData(name)
							.getLineData(num);
					if (!candidateLine.isCovered()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public static Set<String> getIdsForLinesCovered(ProjectData coverage) {
		Set<String> ret = new HashSet<String>();

		int count = 0;

		for (Object obj : coverage.getClasses()) {
			ClassData classData = (ClassData) obj;
			String name = classData.getName();
			for (Object covData : classData.getLines()) {
				LineData goalLine = (LineData) covData;
				if (goalLine.isCovered()) {
					count++;
					ret.add(name + ":" + goalLine.getLineNumber());
				}
			}
		}

		logger.debug("Found " + count
				+ " covered lines in getIdsForLinesCovered");

		return ret;

	}

	public static ProjectData loadCoverageData(InputStream dataFile)
			throws IOException {
		ObjectInputStream objects = null;

		try {
			objects = new ObjectInputStream(dataFile);
			ProjectData projectData = (ProjectData) objects.readObject();
			logger.info("Cobertura Utils: Loaded information on "
					+ projectData.getNumberOfClasses() + " classes.");

			return projectData;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Cobertura Utils: Error reading from object stream.",
					e);
			return null;
		} finally {
			if (objects != null) {
				try {
					objects.close();
				} catch (IOException e) {
					logger.error("Cobertura: Error closing object stream.");
				}
			}
		}
	}

	public static void saveCoverageData(ProjectData projectData,
			OutputStream dataFile) {
		ObjectOutputStream objects = null;

		try {
			objects = new ObjectOutputStream(dataFile);
			objects.writeObject(projectData);
			logger.info("Cobertura: Saved information on "
					+ projectData.getNumberOfClasses() + " classes.");
		} catch (IOException e) {
			logger.error("Cobertura: Error writing to object stream.", e);
		} finally {
			if (objects != null) {
				try {
					objects.close();
				} catch (IOException e) {
					logger.error("Cobertura: Error closing object stream.", e);
				}
			}
		}
	}

}
