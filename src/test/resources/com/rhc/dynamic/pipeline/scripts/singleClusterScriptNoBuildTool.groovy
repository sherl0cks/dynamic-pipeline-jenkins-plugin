node {
	stage ('Code Checkout') { 
		git url: 'https://foo.bar.com/justin.git', branch: 'master'
	}

	stage ('Build App') {
		dir( 'build-home-dir' ) {
			echo 'Using build tool: sh'
			sh "customBuildAppCommand"
			sh "customBuildAppCommand with arguments"
		}
	}

	stage ('Build Image and Deploy to Dev') {
		echo 'No buildImageCommands, using default OpenShift image build and deploy'
		openshiftBuild apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, bldCfg: 'cool-application-name', checkForTriggeredDeployments: 'true', namespace: 'dev-project', showBuildLogs: 'true'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'cool-application-name', namespace: 'dev-project'
	}

	stage ('Deploy to stage-project') {
		input 'Deploy to stage-project?'
		openshiftTag apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, destStream: 'cool-application-name', destTag: 'latest', destinationAuthToken: $OPENSHIFT_API_TOKEN, destinationNamespace: 'stage-project', namespace: 'dev-project', srcStream: 'cool-application-name', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'cool-application-name', namespace: 'stage-project'
	}

	stage ('Deploy to prod-project') {
		input 'Deploy to prod-project?'
		openshiftTag apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, destStream: 'cool-application-name', destTag: 'latest', destinationAuthToken: $OPENSHIFT_API_TOKEN, destinationNamespace: 'prod-project', namespace: 'stage-project', srcStream: 'cool-application-name', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'cool-application-name', namespace: 'prod-project'
	}
}
