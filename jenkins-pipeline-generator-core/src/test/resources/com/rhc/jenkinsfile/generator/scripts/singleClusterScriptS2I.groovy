node {
	stage ('Code Checkout') { 
		git url: 'https://github.com/sherl0cks/openshift-jenkins-s2i-config.git', branch: 'fixing-seed-jobs'
	}

	stage ('Build App') {
		echo 'Using build tool: s2i'
	}

	stage ('Build Image and Deploy to Dev') {
		echo 'No buildImageCommands, using default OpenShift image build and deploy'
		openshiftBuild apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, bldCfg: 'jenkins', checkForTriggeredDeployments: 'true', namespace: 'pipeline-dev', showBuildLogs: 'true'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'jenkins', namespace: 'pipeline-dev'
	}

	stage ('Deploy to pipeline-uat') {
		input 'Deploy to pipeline-uat?'
		openshiftTag apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, destStream: 'jenkins', destTag: 'latest', destinationAuthToken: $OPENSHIFT_API_TOKEN, destinationNamespace: 'pipeline-uat', namespace: 'pipeline-dev', srcStream: 'jenkins', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'jenkins', namespace: 'pipeline-uat'
	}

	stage ('Deploy to pipeline-delivery') {
		input 'Deploy to pipeline-delivery?'
		openshiftTag apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, destStream: 'jenkins', destTag: 'latest', destinationAuthToken: $OPENSHIFT_API_TOKEN, destinationNamespace: 'pipeline-delivery', namespace: 'pipeline-uat', srcStream: 'jenkins', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'jenkins', namespace: 'pipeline-delivery'
	}
}
