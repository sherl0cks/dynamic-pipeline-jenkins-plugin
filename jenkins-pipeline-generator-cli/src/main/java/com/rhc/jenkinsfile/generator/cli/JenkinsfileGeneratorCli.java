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

import com.rhc.automation.model.Engagement;
import com.rhc.jenkinsfile.generator.EngagementMarshaller;
import com.rhc.jenkinsfile.generator.ReleasePipelineJenkinsfileGenerator;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * largely copied from https://commons.apache.org/proper/commons-cli/usage.html.
 */
public class JenkinsfileGeneratorCli {

    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsfileGeneratorCli.class);

    public String process(final String[] args) {
        // create the parser
        CommandLineParser parser = new DefaultParser();
        Options options = createOptions();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.getArgList().size() == 0 || line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("jfg [ENGAGEMENT] [APPLICATION-NAME]", createUsageHeader(), options, "");
            } else if (line.getArgList().size() != 2) {
                LOGGER.error(String.format("You passed in '%s' arguments. You must pass in 2 and only 2. Use -h or --help for usage", line.getArgList().size()));
            } else {
                return processParsedArguments(line);
            }

        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
        return null;
    }

    private String processParsedArguments(final CommandLine line) {
        Engagement engagement = getEngagement(line);
        String applicationName = line.getArgList().get(1);

        String jenkinsfileContents = ReleasePipelineJenkinsfileGenerator.generate( engagement, applicationName);

        System.out.println( jenkinsfileContents );

        return jenkinsfileContents;
    }

    private Engagement getEngagement(final CommandLine line) {
        Engagement engagement = null;
        String engagementObjectLocation = line.getArgList().get(0);
        if (engagementObjectLocation.startsWith("http")) {
            engagement = EngagementMarshaller.getEngagementFromUrl( engagementObjectLocation );
        } else {
            engagement = EngagementMarshaller.getEngagementFromFileInWorkingDirectory(engagementObjectLocation);
        }
        return engagement;
    }

    private Options createOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "prints this help message");
        return options;
    }

    private String createUsageHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Generate a Jenkinsfile for the APPLICATION-NAME in the ENGAGEMENT\n\n");
        sb.append("Example for engagement.json as a local file:\n");
        sb.append("   jfg path/to/engagement.json your-application-name\n\n");
        sb.append("Example for engagement.json at a url:\n");
        sb.append("   jfg http://www.host.com/engagement.json your-application-name\n\n");

        sb.append("OPTIONS:");

        return sb.toString();
    }

}
