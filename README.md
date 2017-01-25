# Jenkins Pipeline Generator

This repository is evolving. Originally, it was focused on the development of a Jenkins plugin that generate pipeline code in the form of `Jenkinsfile.` The core generation code has been broken out into it's own module, and we now plan to expose it's functionality via CLI and REST API, in addition to Jenkins plugin.

## Building From Source

`mvn clean install/deploy -Denforcer.skip=true` from the root directory. The Jenkins Plugin currently has some issues, thus the `-Denforcer.skip=true`.

## Core Generator

The core generation code base, which transforms the API This module builds normally right now e.g. `mvn clean install/deploy` is fine. The basics are:

- `ReleasePipelineGenerator` which is the main point of entry and provides a single client interface regardless of the dialect of Jenkinsfile
- `PipelineDialect` defines the dialects the generator supports, which currently include the original syntax and declarative syntax (experimental). Overtime, this may be extended to support non-Jenkins dialects like GoCD / Drone / etc, but we haven't crossed that bridge.
- `EngagementDAO` which is used to manipulate the `Engagement` object hierarchy defined in our [Automation API](https://github.com/rht-labs/api-design) into a format more convenient for Jenkinsfile generation.

### S2I Support

OpenShift S2I is a multifaceted beast. Here are the ways the generated pipelines integrate:

1. All images builds are assumed to be done via S2I
2. By default, the image builds will using [git repository source builds](https://docs.openshift.com/container-platform/3.3/dev_guide/builds.html#source-code)
3. S2I binary builds can switched on by ensuring your application has one of the following labels:
  * `provider=fabric8`
  * `s2i=binary`

## REST API

## CLI

Simple fat JAR to provide a command line interface to generate Jenkinsfile from the engagement. You can download a version from the [Jenkins Build](https://jenkins.core.rht-labs.com/job/jenkins-pipeline-generator-ci/) (download the `*-jar-with-dependencies.jar` version).

### CLI Usage

- Use the CLI itself to get the latest usage. Download the `*-jar-with-dependencies.jar` version and then `java -jar jenkins-pipeline-generator-cli-jar-with-dependencies.jar`

### CLI Key Classes
- `JenkinsfileGeneratorCli` uses [Apache CLI](https://commons.apache.org/proper/commons-cli/usage.html) to provide a testable component
- `CliApdater` provides a Java `main` 

## REST API

**TODO**

## Jenkins Plugin

While this is work has been deprecated. See the v0.3.0 tag for the code.

## Inspiration

These code bases were instrumental in teaching us how to build a Jenkins plugin and extend the pipeline model:

- [the workflow job plugin](https://github.com/jenkinsci/workflow-job-plugin), which provides Jenkins with the notion of a pipeline and is built primarily by the CloudBees team. like everything in the pipeline ecosystem, we depend on this plugin.
- [the kubernetes plugin](https://github.com/jenkinsci/kubernetes-pipeline-plugin), which provides a global variable to Jenkins, same mechanism as our global variable, to perform a variety of Kubernetes based activities.
- [the OpenShift plugin](https://github.com/jenkinsci/openshift-pipeline-plugin), which provides a comprehensive set of pipeline steps to work with OpenShift Container Platform

## Contributing
See the [contributors guide](https://github.com/rht-labs/api-design/blob/master/CONTRIBUTING.md).