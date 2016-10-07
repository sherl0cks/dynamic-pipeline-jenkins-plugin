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
package com.rhc.dynamic.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.rhc.automation.model.Engagement;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class DynamicPipelineFactory implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicPipelineFactory.class);
	public static final String AUTOMATION_API_VERSION = "0.2.0";

	private final CpsScript script;
	private String configFile;
	private String applicationName;
	private transient Engagement engagement;
	private transient OkHttpClient client = new OkHttpClient();

	public DynamicPipelineFactory(CpsScript script) {
		this.script = script;

	}
	
	public void generateAndExecutePipelineScript() {
		String pipelineScript = generatePipelineScript();
		script.evaluate(pipelineScript);
	}
	
	public String generatePipelineScript() {
		checkConfiguration();
		String pipelineScript = ReleasePipelineScriptGenerator.generate(engagement, applicationName);
		LOGGER.debug("{}{}{}","\n\n",pipelineScript, "\n\n");
		return pipelineScript;
	}

	public DynamicPipelineFactory withConfigurationFile(String fileName) throws IOException {

		InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
		if (is == null) {
			throw new RuntimeException("Could not find the specified configuration file: " + fileName);
		}
		this.configFile = IOUtils.toString(is);
		LOGGER.debug(this.configFile);
		
		engagement = new GsonBuilder().create().fromJson(this.configFile, Engagement.class);

		return this;
	}

	public DynamicPipelineFactory withHttpConfiguration(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = client.newCall(request).execute();
		if (response.code() != 200) {
			throw new RuntimeException("The http configuration returned a status code of " + response.code()
					+ ". We only support 200. Here is the response message: " + response.message());
		} else {
			InputStream is = response.body().byteStream();
			if (is == null) {
				throw new RuntimeException("The http configuration response body is null!");
			}
			this.configFile = IOUtils.toString(is);
			LOGGER.debug(this.configFile);
			engagement = new GsonBuilder().create().fromJson(this.configFile, Engagement.class);
		}
		return this;
	}

	public DynamicPipelineFactory withApplicationName(String appName) {
		this.applicationName = appName;
		return this;
	}
	
	public DynamicPipelineFactory withApiToken(String apiToken){
		script.getBinding().setVariable("$OPENSHIFT_API_TOKEN", apiToken);
		return this;
	}

	private void checkConfiguration() {
		if (script == null) {
			throw new RuntimeException("The CpsScript cannot be null. Mock it if you are unit testing.");
		}
		if (engagement == null) {
			throw new RuntimeException("You must provide a configuration on the classpath, or with HTTP, using withConfiguration()");
		}
		if (applicationName == null || applicationName.isEmpty()) {
			throw new RuntimeException("You must provide a name for this application using withApplicationName()");
		}
	}

}
