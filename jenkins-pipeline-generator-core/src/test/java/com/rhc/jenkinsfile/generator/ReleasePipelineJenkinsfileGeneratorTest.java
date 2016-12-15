/*
 * Copyright (C) 2016 Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rhc.jenkinsfile.generator;

import com.rhc.automation.model.Engagement;
import com.rhc.jenkinsfile.generator.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ReleasePipelineJenkinsfileGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleasePipelineJenkinsfileGeneratorTest.class);


    @Test
    public void shouldCorrectlyCreateSingleClusterMultiProjectS2IBuild() throws IOException {
        // given
        Engagement engagement = TestUtils.getEngagementFromFile(TestUtils.S2I_BUILD_FILE);

        // when
        String jenkinsfile = ReleasePipelineJenkinsfileGenerator.generate(engagement, "jenkins");

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptS2I.groovy"), TestUtils.removeWhiteSpace(jenkinsfile));
    }

    @Test
    public void shouldCorrectlyCreateSingleClusterMultiProjectScriptWithCustomBuildImageAndCustomDeployCommands() throws IOException {
        // given
        Engagement engagement = TestUtils.getEngagementFromFile(TestUtils.CUSTOM_BUILD_IMAGE_FILE);

        // when
        String jenkinsfile = ReleasePipelineJenkinsfileGenerator.generate(engagement, TestUtils.APPLICATION_NAME);

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptCustomCommands.groovy"), TestUtils.removeWhiteSpace(jenkinsfile));
    }

    @Test
    public void shouldCorrectlyCreateSingleClusterMultiProjectScriptWithMvn() throws IOException {
        // given
        Engagement engagement = TestUtils.getEngagementFromFile(TestUtils.MVN_BUILD_FILE);

        // when
        String jenkinsfile = ReleasePipelineJenkinsfileGenerator.generate(engagement, TestUtils.APPLICATION_NAME);

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptMvn3.groovy"), TestUtils.removeWhiteSpace(jenkinsfile));
    }


    @Test
    public void shouldCorrectlyCreateSingleClusterMultiProjectScriptWithFabric8() throws IOException {
        // given
        Engagement engagement = TestUtils.getEngagementFromFile(TestUtils.FABRIC8_BUILD_FILE);

        // when
        String jenkinsfile = ReleasePipelineJenkinsfileGenerator.generate(engagement, TestUtils.APPLICATION_NAME);

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptFabric8.groovy"), TestUtils.removeWhiteSpace(jenkinsfile));
    }


    @Test
    public void shouldCorrectlyCreateAutomationApi() throws IOException {
        // given
        Engagement engagement = TestUtils.getEngagementFromFile(TestUtils.LABS_ENV_FILE);

        // when
        String jenkinsfile = ReleasePipelineJenkinsfileGenerator.generate(engagement, "automation-api");

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("labs-env.groovy"), TestUtils.removeWhiteSpace(jenkinsfile));
    }


    @Test
    public void shouldThrowExceptionForUnsupportedBuildTool() throws IOException {
        // given
        Engagement engagement = TestUtils.getEngagementFromFile(TestUtils.UNSUPPORTED_BUILD_TOOL_FILE);

        // when
        try {
            ReleasePipelineJenkinsfileGenerator.generate(engagement, TestUtils.APPLICATION_NAME);
            Assert.fail("did not throw error");
        } catch (RuntimeException e) {
            // then
            if (e.getMessage() != null && e.getMessage().contains("gradle-3 is currently unsupported")) {
                // do nothing, this is desired behavior
            } else {
                Assert.fail("this is the wrong exception " + e.getMessage());
            }
        }
    }

    @Test
    public void shouldThrowExceptionForNoBuildTool() throws IOException {
        // given
        Engagement engagement = TestUtils.getEngagementFromFile(TestUtils.NO_BUILD_TOOL_FILE);

        // when
        try {
            ReleasePipelineJenkinsfileGenerator.generate(engagement, TestUtils.APPLICATION_NAME);
            Assert.fail("did not throw error");
        } catch (RuntimeException e) {
            // then
            if (e.getMessage() != null && e.getMessage().contains("A build tool must be set")) {
                // do nothing, this is desired behavior
            } else {
                Assert.fail("this is the wrong exception " + e.getMessage());
            }
        }
    }
}
