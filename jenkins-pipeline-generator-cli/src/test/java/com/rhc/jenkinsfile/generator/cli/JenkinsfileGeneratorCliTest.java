package com.rhc.jenkinsfile.generator.cli;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class JenkinsfileGeneratorCliTest {

    /**
     * This test requires access to Github REST API. We could load stuff up via embedded jetty, but this was the quick and dirty way to get going.
     */
    @Test
    public void shouldGenerateJenkinsfileFromUrl() throws IOException {
        // given
        String[] args = {"https://raw.githubusercontent.com/rht-labs/api-design/master/examples/delivery_env.json", "automation-api"};
        JenkinsfileGeneratorCli cli = new JenkinsfileGeneratorCli();

        // when
        String jenkinsfileContent = cli.process(args);

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("automation-api.groovy"), TestUtils.removeWhiteSpace(jenkinsfileContent));
    }
}
