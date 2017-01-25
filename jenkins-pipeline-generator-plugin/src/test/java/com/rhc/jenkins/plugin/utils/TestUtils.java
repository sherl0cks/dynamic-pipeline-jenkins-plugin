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
package com.rhc.jenkins.plugin.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO move this to a shared module
 */
public class TestUtils {

	public static final String APPLICATION_NAME = "cool-application-name";
	public static final String NO_BUILD_TOOL_FILE = "com/rhc/jenkins/plugin/engagements/singleClusterMultiProjectNoBuildTool.json";
	public static final String S2I_BUILD_FILE = "com/rhc/jenkins/plugin/engagements/singleClusterMultiProjectWithS2I.json";
	public static final String CUSTOM_BUILD_IMAGE_FILE = "com/rhc/jenkins/plugin/engagements/singleClusterMultiProjectWithCustomBuildImageCommands.json";
	public static final String MVN_BUILD_FILE = "com/rhc/jenkins/plugin/engagements/singleClusterMultiProjectWithMvn.json";
	public static final String FABRIC8_BUILD_FILE = "com/rhc/jenkins/plugin/engagements/singleClusterMultiProjectWithFabric8.json";
	public static final String UNSUPPORTED_BUILD_TOOL_FILE = "com/rhc/jenkins/plugin/engagements/singleClusterMultiProjectWithUnsupportedBuildTool.json";
	public static final String LABS_ENV_FILE = "com/rhc/jenkins/plugin/engagements/com.rhc.jenkinsfile.generator.scripts.jenkinsfile.com.rhc.jenkinsfile.generator.scripts.jenkinsfile.original.labs-env.json";

	
	public static String getPipelineScriptFromFileWithoutWhitespace(String fileName) throws IOException {
		return removeWhiteSpace(getPipelineScriptFromFile(fileName));
	}

	public static String getPipelineScriptFromFile(String fileName) throws IOException {
		String file = "com/rhc/jenkins/plugin/scripts/" + fileName;
		return IOUtils.toString(TestUtils.class.getClassLoader().getResourceAsStream(file));
	}

	public static String removeWhiteSpace(String input) {
		return input.replaceAll("\\s+", "");
	}
	
	public static String getStringFromFile(String fileName) throws IOException{
		InputStream stream = TestUtils.class.getClassLoader().getResourceAsStream( fileName);
		if (stream == null ){
			throw new RuntimeException("could not find file: " + fileName);
		}
		return IOUtils.toString(stream);
	}
	
	public static String getEmbeddedServerUrl(int port, String resource){
		StringBuilder sb = new StringBuilder();
		sb.append("http://localhost:");
		sb.append(port);
		sb.append("/");
		sb.append(resource);
		
		return sb.toString();
	}
}

