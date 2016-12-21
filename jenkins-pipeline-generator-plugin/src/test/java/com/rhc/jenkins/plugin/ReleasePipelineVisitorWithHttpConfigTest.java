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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhc.jenkins.plugin.utils.TestUtils;
import com.squareup.okhttp.MediaType;

/**
 * We're using mockito to stub out Jenkins interactions. These links should
 * explain the mechanism in play:
 * {@link http://site.mockito.org/mockito/docs/current/org/mockito/junit/MockitoRule.html}
 * {@link http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html#15}
 * 
 * We're also using Jetty to create a simple file server so this build can run
 * without dependencies
 */
public class ReleasePipelineVisitorWithHttpConfigTest {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final Logger LOGGER = LoggerFactory.getLogger(ReleasePipelineVisitorWithHttpConfigTest.class);

	private static Server server;
	private static int serverPort;

	@Mock
	private CpsScript mockScript;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Test
	public void shouldFailWhenNoConfigurationIsProvided() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript)
				.withApplicationName(TestUtils.APPLICATION_NAME);

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("An exception should have been thrown");
		} catch (RuntimeException e) {
			// then
			Assert.assertEquals(
					"You must provide a configuration on the classpath, or with HTTP, using withConfiguration()",
					e.getMessage());
		}
	}

	@Test
	public void shouldFailWhenNoApplicationNameIsProvided() throws IOException {
		// given
		DynamicPipelineFactory factory = new DynamicPipelineFactory(mockScript)
				.withHttpConfiguration(TestUtils.getEmbeddedServerUrl(serverPort, TestUtils.NO_BUILD_TOOL_FILE));

		// when
		try {
			factory.generatePipelineScript();
			Assert.fail("An exception should have been thrown");
		} catch (RuntimeException e) {
			// then
			Assert.assertEquals("You must provide a name for this application using withApplicationName()",
					e.getMessage());
		}
	}

	@BeforeClass
	public static void server() throws Exception {
		server = new Server(0);
		server.setStopAtShutdown(true);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "pom.html" });

		resource_handler.setResourceBase("src/test/resources");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		serverPort = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
		LOGGER.info("Server port for Jetty: {}", serverPort);

	}

	@AfterClass
	public static void shutdownServer() throws Exception {
		server.stop();
	}
}
