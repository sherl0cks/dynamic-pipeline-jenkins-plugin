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
package com.rhc.jenkinsfile.generator.cli;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class JenkinsfileGeneratorCliTest {

    /**
     * This test requires access to Github REST API. We could load stuff up via embedded jetty, but this was the quick and dirty way to get going.
     */
    @Test
    public void shouldGenerateOriginalJenkinsfileDialectFromUrl() throws IOException {
        // given
        String[] args = {"https://raw.githubusercontent.com/rht-labs/api-design/master/examples/delivery_env.json", "automation-api"};
        JenkinsfileGeneratorCli cli = new JenkinsfileGeneratorCli();

        // when
        String jenkinsfileContent = cli.process(args);

        // then
        // we're only testing the CLI elements. the contents of the scripts are tested in core
        Assert.assertTrue(jenkinsfileContent != null);
        Assert.assertFalse(jenkinsfileContent.isEmpty());
        Assert.assertTrue(jenkinsfileContent.startsWith("node"));
    }

    @Test
    public void shouldGenerateDeclarativeJenkinsfileDialectFromUrl() throws IOException {
        // given
        String[] args = {"https://raw.githubusercontent.com/rht-labs/api-design/master/examples/delivery_env.json", "automation-api", "-d"};
        JenkinsfileGeneratorCli cli = new JenkinsfileGeneratorCli();

        // when
        String jenkinsfileContent = cli.process(args);

        // then
        // we're only testing the CLI elements. the contents of the scripts are tested in core
        Assert.assertTrue(jenkinsfileContent != null);
        Assert.assertFalse(jenkinsfileContent.isEmpty());
        Assert.assertTrue(jenkinsfileContent.startsWith("pipeline"));
    }

    @Test
    public void shouldGenerateUsage() throws IOException {
        // given
        String[] args = {"https://raw.githubusercontent.com/rht-labs/api-design/master/examples/delivery_env.json", "automation-api", "-h"};
        JenkinsfileGeneratorCli cli = new JenkinsfileGeneratorCli();

        // when
        String jenkinsfileContent = cli.process(args);

        // then
        // we're only testing the CLI elements. the contents of the scripts are tested in core
        Assert.assertTrue(jenkinsfileContent == null);
    }
}
