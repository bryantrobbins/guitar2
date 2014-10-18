package edu.umd.cs.guitar.util;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.LineData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.reporting.xml.XMLReport;
import net.sourceforge.cobertura.util.FileFinder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * A collection of utilities for dealing with the Cobertura code coverage
 * library and its ProjectData object.
 */
public final class CoberturaUtils {

    /**
     * This hides the default public constructor.
     */
    private CoberturaUtils() {

    }

    /**
     * A log4j logger.
     */
    private static Logger logger = LogManager.getLogger(CoberturaUtils.class);


    /**
     * Convert coverage object into coverage report string.
     *
     * @param pd the coverage object
     * @return the coverage report as a single string
     * @throws IOException if object cannot be loaded successfully
     */
    public static String getCoverageReportFromCoverageObject(
            final ProjectData pd) throws IOException {
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

       new XMLReport(pd, coverageOut, sources,
               new ComplexityCalculator(sources));

        // Convert to String
        return coverageOut.toString(Charset.forName("UTF-8").name());
    }

    /**
     * Perform a diff between two coverage objects. Computes and returns the
     * number of lines covered in the first object which are not covered in
     * the second object.
     *
     * @param current the first object
     * @param updated the second object
     * @return count of newly coved lines in the first object
     */
    public static int addedCoveredLines(final ProjectData current,
                                        final ProjectData updated) {
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

    /**
     * Compare a goal coverage object to a candidate coverage object.
     * Return true if the candidate covers all of the lines in the goal.
     *
     * @param goal      a coverage object representing the desired coverage
     * @param candidate a coverage object which may or may not achieve all
     *                  of the desired coverage
     * @return true if candidate coverage achieves goal coverage; otherwise
     * false
     */
    public static boolean doesCoverageMeetGoal(final ProjectData goal,
                                               final ProjectData candidate) {
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

    /**
     * Return a set of line numbers representing all lines covered in a
     * coverage object.
     *
     * @param coverage the coverage object
     * @return the set of lines covered (as Strings of integer values)
     */
    public static Set<String> getIdsForLinesCovered(
            final ProjectData coverage) {
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

    /**
     * Convert an input stream of binary data to a ProjectData. Obviously,
     * the data in the stream must have come from a ProjectData conversion
     * originally!
     *
     * @param dataFile the input stream of data to be converted
     * @return the coverage object
     * @throws IOException if reading the data stream fails
     */
    public static ProjectData loadCoverageData(final InputStream dataFile)
            throws IOException {
        ObjectInputStream objects = null;

        try {
            objects = new ObjectInputStream(dataFile);
            ProjectData projectData = (ProjectData) objects.readObject();
            logger.info("Cobertura Utils: Loaded information on "
                    + projectData.getNumberOfClasses() + " classes.");

            return projectData;
        } catch (IOException e) {
            logger.error("Error loading binary data", e);
            return null;
        } catch (ClassNotFoundException e) {
            logger.error("Error converting to ProjectData", e);
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

    /**
     * Utility method to save a coverage object to an output stream.
     *
     * @param projectData the coverage object
     * @param dataFile    the output stream
     */
    public static void saveCoverageData(final ProjectData projectData,
                                        final OutputStream dataFile) {
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
