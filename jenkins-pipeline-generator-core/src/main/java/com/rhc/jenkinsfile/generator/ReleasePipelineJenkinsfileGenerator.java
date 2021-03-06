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

import com.rhc.automation.model.Application;
import com.rhc.automation.model.Engagement;
import com.rhc.automation.model.OpenShiftCluster;
import com.rhc.automation.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReleasePipelineJenkinsfileGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReleasePipelineJenkinsfileGenerator.class);
    private static final Set<String> SUPPORTED_BUILD_TOOLS = new HashSet<String>(
            Arrays.asList("node-0.10", "node-4", "mvn-3", "sh", "s2i"));

    public static String generate(final Engagement engagement, final String applicationName) {
        StringBuilder script = new StringBuilder();
        validateInput(engagement, applicationName);
        script.append(initializeScript());
        script.append(generateCodeCheckoutStage(engagement, applicationName));
        script.append(generateBuildAppStage(engagement, applicationName));
        script.append(generateBuildImageAndDeployToDevStage(engagement, applicationName));
        script.append(generateAllPromotionStages(engagement, applicationName));
        script.append(finalizeScript());
        return script.toString();
    }

    private static void validateInput(final Engagement engagement, final String applicationName) {
        if (applicationName == null || applicationName.isEmpty()) {
            throw new RuntimeException("applicationName cannot be null or empty");
        }
    }

    private static String initializeScript() {
        StringBuilder script = new StringBuilder();
        script.append("node {\n");
        return script.toString();
    }

    private static String generateCodeCheckoutStage(final Engagement engagement, final String applicationName) {
        StringBuilder script = new StringBuilder();
        script.append("  stage ('Code Checkout') {\n ");
        Application app = EngagementDAO.getApplicationFromBuildProject(engagement, applicationName);
        if (app == null) {
            throw new RuntimeException(String.format("Unable to find application '%s' in your Engagement object. Double check your data and configuration. Be aware that search is currently case sensitive.", applicationName));
        }
        String branch = (app.getScmRef() == null || app.getScmRef().isEmpty()) ? "master" : app.getScmRef();
        script.append(String.format("    git url: '%s', branch: '%s'%n", app.getScmUrl(), branch));
        script.append("  }\n\n");
        return script.toString();
    }

    private static String generateBuildAppStage(final Engagement engagement, final String applicationName) {
        StringBuilder script = new StringBuilder();
        script.append("  stage ('Build App') {\n");

        Application app = EngagementDAO.getApplicationFromBuildProject(engagement, applicationName);
        if (app.getContextDir() == null || app.getContextDir().isEmpty()) {
            script.append(createBuildCommands(app));
        } else {
            script.append("  dir( '").append(app.getContextDir()).append("' ) {\n  ");
            script.append(createBuildCommands(app));
            script.append("  }\n");
        }

        script.append("  }\n\n");
        return script.toString();
    }

    private static String generateBuildImageAndDeployToDevStage(final Engagement engagement, final String applicationName) {
        StringBuilder script = new StringBuilder();
        script.append("\n  stage ('Build Image and Deploy to Dev') {\n");
        Application app = EngagementDAO.getApplicationFromBuildProject(engagement, applicationName);
        if (app.getLabels().containsKey("provider") && app.getLabels().get("provider").equals("fabric8")) {
            script.append("    echo 'Found label \"provider=fabric8\", we are generating the s2i binary build commands'\n");
            script.append(String.format("    sh 'oc login %s --token=$OPENSHIFT_API_TOKEN --insecure-skip-tls-verify'%n", EngagementDAO.getBuildCluster(engagement).getOpenshiftHostEnv()));
            script.append(String.format("    sh 'oc start-build %s --from-dir=. --follow -n %s'", applicationName, EngagementDAO.getBuildProjectForApplication(engagement, applicationName).getName()));
        } else if (app.getBuildImageCommands() == null || app.getBuildImageCommands().isEmpty()) {
            script.append("    echo 'No buildImageCommands, using default OpenShift image build and deploy'\n");
            script.append(createDefaultOpenShiftBuildAndDeployScript(engagement, applicationName));
        } else {
            script.append("    echo 'Found buildImageCommands, executing in shell'\n");
            List<String> commands = app.getBuildImageCommands();
            for (String command : commands) {
                script.append("  sh '").append(command).append("' \n");
            }
        }
        script.append("  }\n");

        return script.toString();
    }

    private static String generateAllPromotionStages(final Engagement engagement, final String applicationName) {
        StringBuilder script = new StringBuilder();
        OpenShiftCluster srcCluster = EngagementDAO.getBuildCluster(engagement);
        Project srcProject = EngagementDAO.getBuildProjectForApplication(engagement, applicationName);

        for (OpenShiftCluster cluster : engagement.getOpenshiftClusters()) {
            for (Project project : EngagementDAO.getPromotionProjectsForApplication(engagement, applicationName)) {
                script.append(generatePromoteImageStage(engagement, cluster, project, srcCluster, srcProject, applicationName));
                srcProject = project;
            }
            srcCluster = cluster;
        }
        return script.toString();
    }

    private static String generatePromoteImageStage(final Engagement engagement, final OpenShiftCluster destCluster,
                                                    final Project destProject, final OpenShiftCluster srcCluster, final Project srcProject, final String applicationName) {
        StringBuilder script = new StringBuilder();
        script.append("\n  stage ('Deploy to ").append(destProject.getName()).append("') {\n");
        script.append("    input 'Deploy to ").append(destProject.getName()).append("?'\n");

        Application app = EngagementDAO.getApplicationFromBuildProject(engagement, applicationName);

        if (app.getDeployImageCommands() == null || app.getDeployImageCommands().isEmpty()) {

            // TODO this where we can support image tags that aren't latest
            script.append(String.format(
                    "    openshiftTag apiURL: '%s', authToken: $OPENSHIFT_API_TOKEN, destStream: '%s', destTag: 'latest', destinationAuthToken: $OPENSHIFT_API_TOKEN, destinationNamespace: '%s', namespace: '%s', srcStream: '%s', srcTag: 'latest'%n",
                    srcCluster.getOpenshiftHostEnv(), applicationName, destProject.getName(), srcProject.getName(),
                    applicationName));
            script.append(String.format(
                    "    openshiftVerifyDeployment apiURL: '%s', authToken: $OPENSHIFT_API_TOKEN, depCfg: '%s', namespace: '%s'%n",
                    srcCluster.getOpenshiftHostEnv(), applicationName, destProject.getName()));

        } else {
            for (String command : app.getDeployImageCommands()) {
                script.append("  sh '").append(command).append("' \n");
            }
        }

        script.append("  }\n");

        return script.toString();
    }

    private static String finalizeScript() {
        StringBuilder script = new StringBuilder();

        script.append("}\n");
        return script.toString();
    }

    private static String createBuildCommands(final Application app) {
        StringBuilder script = new StringBuilder();

        if (app.getBuildTool() == null || app.getBuildTool().isEmpty()) {
            throw new RuntimeException("A build tool must be set for the application. Currently support tools are: "
                    + SUPPORTED_BUILD_TOOLS);
        } else if (SUPPORTED_BUILD_TOOLS.contains(app.getBuildTool())) {
            script.append("    echo 'Using build tool: ").append(app.getBuildTool()).append("'\n");
            script.append(createListOfShellCommandsScript(app, app.getBuildTool()));
        } else {
            throw new RuntimeException(
                    app.getBuildTool() + " is currently unsupported. Please select one of " + SUPPORTED_BUILD_TOOLS);
        }

        return script.toString();
    }

    private static String createListOfShellCommandsScript(final Application app, final String tool) {
        StringBuilder script = new StringBuilder();

        if (app.getBuildApplicationCommands() == null || app.getBuildApplicationCommands().isEmpty()) {
            if (app.getBuildTool().equals("s2i")) {
                // do nothing. s2i should not have build commands
                // worth mentioning here that I realize s2i is broad. we're
                // covering the case of just s2i assemble, like with Jenkins. if
                // you have a better naming convention, feel free to open a PR
            } else {
                // but every other build tool should
                throw new RuntimeException("app.buildApplicationCommands cannot be empty");
            }

        } else {
            if (app.getBuildTool().equals("s2i")) {
                throw new RuntimeException("s2i builds should not have build commands");
            }
            if (!tool.equals("sh")) {
                script.append("    def toolHome = tool '").append(tool).append("'\n");
                if (tool.contains("mvn")) {
                    script.append("    env.JAVA_HOME = tool 'java-1.8'\n");
                }
            }
            List<String> commands = app.getBuildApplicationCommands();
            for (String command : commands) {
                script.append("    sh \"");
                if (!tool.equals("sh")) {
                    script.append("${toolHome}/bin/");
                }
                script.append(command).append("\"\n");
            }

        }

        return script.toString();
    }

    private static String createDefaultOpenShiftBuildAndDeployScript(final Engagement engagement, final String applicationName) {
        StringBuilder script = new StringBuilder();

        Project buildProject = EngagementDAO.getBuildProjectForApplication(engagement, applicationName);
        OpenShiftCluster buildProjectCluser = EngagementDAO.getClusterWithBuildProject(engagement);

        script.append(String.format(
                "    openshiftBuild apiURL: '%s', authToken: $OPENSHIFT_API_TOKEN, bldCfg: '%s', checkForTriggeredDeployments: 'true', namespace: '%s', showBuildLogs: 'true'%n",
                buildProjectCluser.getOpenshiftHostEnv(), applicationName, buildProject.getName()));

        script.append(String.format(
                "    openshiftVerifyDeployment apiURL: '%s', authToken: $OPENSHIFT_API_TOKEN, depCfg: '%s', namespace: '%s'%n",
                buildProjectCluser.getOpenshiftHostEnv(), applicationName, buildProject.getName()));

        return script.toString();
    }

}
