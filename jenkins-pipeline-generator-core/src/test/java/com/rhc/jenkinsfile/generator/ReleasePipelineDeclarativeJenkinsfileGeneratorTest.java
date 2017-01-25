/*
 * Copyright (C) 2016 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
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

import java.io.IOException;

public class ReleasePipelineDeclarativeJenkinsfileGeneratorTest {

    @Test
    public void shouldCorrectlyCreateSingleClusterMultiProjectS2IBuild() throws IOException {
        // given
        Engagement engagement = EngagementMarshaller.getEngagementFromFileOnClasspath(TestUtils.S2I_BUILD_FILE);

        // when
        String jenkinsfile = ReleasePipelineGenerator.generate(engagement, "jenkins", PipelineDialect.JENKINSFILE_DECLARATIVE);

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptS2I.groovy", PipelineDialect.JENKINSFILE_DECLARATIVE), TestUtils.removeWhiteSpace(jenkinsfile));

    }

    @Test
    public void shouldCorrectlyCreateSingleClusterMultiProjectScriptWithMvn() throws IOException {
        // given
        Engagement engagement = EngagementMarshaller.getEngagementFromFileOnClasspath(TestUtils.MVN_BUILD_FILE);

        // when
        String jenkinsfile = ReleasePipelineGenerator.generate(engagement, TestUtils.APPLICATION_NAME, PipelineDialect.JENKINSFILE_DECLARATIVE);

        // then
        Assert.assertEquals(TestUtils.getPipelineScriptFromFileWithoutWhitespace("singleClusterScriptMvn3.groovy", PipelineDialect.JENKINSFILE_DECLARATIVE), TestUtils.removeWhiteSpace(jenkinsfile));
    }
}
