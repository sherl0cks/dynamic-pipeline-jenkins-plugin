# Dynamic Pipeline Jenkins Plugin

The goal of this pipeline is to provide a very simple abstraction over top of Jenkins pipeline so that users can create powerful, dynamic pipelines without having to have any programming skills. This will be done by:

1. providing a declarative data model for environment promotion. this work is currently being done [here](https://github.com/rht-labs/api-design)
2. consuming the data model Jenkins global variable called `dynamicPipeline`, which can retrieve the data model from the SCM repo or via http.
3. generating Jenkinsfile from the data model and then executing it in the same Groovy shell as the pipeline (know as a `CpsScript` in the Jenkins lingo).


## Required Jenkins Variables

This is a list of variables that must be set in Jenkins in order to ensure that the generate script executes as expected. If you are using the seed jobs in the [rht-labs s2i configuration](https://github.com/rht-labs/openshift-jenkins-s2i-config) then these variables will be exposed as require parameters in the seed jobs.

- `$OPENSHIFT_API_TOKEN`: Used to power both the OpenShift Jenkins Pipeline plugin, as well as a few `oc` commands not yet supported in the plugin. This token should align to an OpenShift service account where possible to ensure the configuration doesn't get stale.


## Inspiration

These code bases were instrumental in teaching us how to build a Jenkins plugin and extend the pipeline model:

- [the workflow job plugin](https://github.com/jenkinsci/workflow-job-plugin), which provides Jenkins with the notion of a pipeline and is built primarily by the CloudBees team. like everything in the pipeline ecosystem, we depend on this plugin.
- [the kubernetes plugin](https://github.com/jenkinsci/kubernetes-pipeline-plugin), which provides a global variable to Jenkins, same mechanism as our global variable, to perform a variety of Kubernetes based activities.
- [the OpenShift plugin](https://github.com/jenkinsci/openshift-pipeline-plugin), which provides a comprehensive set of pipeline steps to work with OpenShift Container Platform

## Contributing
See the [contributors guide](https://github.com/rht-labs/api-design/blob/master/CONTRIBUTING.md).