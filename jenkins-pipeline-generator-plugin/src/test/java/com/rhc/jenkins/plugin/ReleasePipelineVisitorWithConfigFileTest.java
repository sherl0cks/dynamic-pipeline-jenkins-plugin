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
package com.rhc.jenkins.plugin;

import static org.mockito.Mockito.verify;

import java.io.IOException;

import com.rhc.jenkins.plugin.utils.TestUtils;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We're using mockito to stub out Jenkins interactions. These links should
 * explain the mechanism in play:
 * {@link http://site.mockito.org/mockito/docs/current/org/mockito/junit/MockitoRule.html}
 * {@link http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#15}
 */
public class ReleasePipelineVisitorWithConfigFileTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReleasePipelineVisitorWithConfigFileTest.class);

	@Mock
	private CpsScript mockScript;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Test
	public void shouldFailWhenNoConfigurationIsProvided() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("An exception should have been thrown");
		} catch (RuntimeException e) {
			// then
			Assert.assertEquals("You must provide a configuration on the classpath, or with HTTP, using withConfiguration()", e.getMessage());
		}
	}

	@Test
	public void shouldFailWhenNoApplicationNameIsProvided() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript).withConfigurationFile(TestUtils.NO_BUILD_TOOL_FILE);

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("An exception should have been thrown");
		} catch (RuntimeException e) {
			// then
			Assert.assertEquals("You must provide a name for this application using withApplicationName()", e.getMessage());
		}
	}

}
