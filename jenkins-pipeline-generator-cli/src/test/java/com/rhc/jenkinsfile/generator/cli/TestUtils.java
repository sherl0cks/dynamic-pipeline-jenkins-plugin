package com.rhc.jenkinsfile.generator.cli;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;


public class TestUtils {

    public static String getPipelineScriptFromFileWithoutWhitespace(String fileName) throws IOException {
        return removeWhiteSpace(getPipelineScriptFromFile(fileName));
    }

    public static String getPipelineScriptFromFile(String fileName) throws IOException {
        return IOUtils.toString(TestUtils.class.getClassLoader().getResourceAsStream("com/rhc/jenkinsfile/generator/cli/scripts/" + fileName));
    }

    public static String removeWhiteSpace(String input) {
        return input.replaceAll("\\s+", "");
    }

    public static String getStringFromFile(String fileName) throws IOException {
        InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (stream == null) {
            throw new RuntimeException("could not find file: " + fileName);
        }
        return IOUtils.toString(stream);
    }


}
