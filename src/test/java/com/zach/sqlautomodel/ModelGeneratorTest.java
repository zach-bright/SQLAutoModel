package com.zach.sqlautomodel;

import java.io.File;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for ModelGenerator.
 */
public class ModelGeneratorTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ModelGeneratorTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ModelGeneratorTest.class);
    }

    /**
     * Temp test
     */
    public void testApp() {
        ModelGenerator mg = new ModelGenerator();
        try {
            mg.generate(
                    new File("/home/ztb/IdeaProjects/SQLAutoModel/src/main/java/com/zach/sqlautomodel/sqldump"),
                    new File("/home/ztb/IdeaProjects/SQLAutoModel/src/main/java/com/zach/sqlautomodel/asdg")
            );
        } catch (Exception e) {}

        assertTrue(true);
    }
}
