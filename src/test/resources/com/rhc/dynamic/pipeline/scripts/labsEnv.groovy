node {
	stage ('Code Checkout'){
		checkout scm
	}

	stage ('Build App'){
		sh 'oc whoami -t > apiTokenOutput.txt'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		sh 'oc login 10.1.2.2:8443 --insecure-skip-tls-verify=true --username=admin --password=$OPENSHIFT_PASSWORD'

		dir( 'website' ) {
			echo 'Using build tool: node-4'
			def toolHome = tool 'node-4'
			sh "${toolHome}/bin/npm install"
			sh "${toolHome}/bin/npm run-script ci"
		}
	}

	stage ('Build Image and Deploy to Dev'){
		echo 'No buildImageCommands, using default OpenShift image build and deploy'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		openshiftBuild apiURL: '10.1.2.2:8443', authToken: apiToken, bldCfg: 'infographic', checkForTriggeredDeployments: 'true', namespace: 'ci-dev', showBuildLogs: 'true'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: apiToken, depCfg: 'infographic', namespace: 'ci-dev'
	}

	stage ('Deploy to ci-stage') {
		input 'Deploy to ci-stage?'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		openshiftTag apiURL: '10.1.2.2:8443', authToken: apiToken, destStream: 'infographic', destTag: 'latest', destinationAuthToken: apiToken, destinationNamespace: 'ci-stage', namespace: 'ci-dev', srcStream: 'infographic', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: apiToken, depCfg: 'infographic', namespace: 'ci-stage'
	}

	stage ('Deploy to ci-prod') {
		input 'Deploy to ci-prod?'
		String apiToken = readFile( 'apiTokenOutput.txt' ).trim()
		openshiftTag apiURL: '10.1.2.2:8443', authToken: apiToken, destStream: 'infographic', destTag: 'latest', destinationAuthToken: apiToken, destinationNamespace: 'ci-prod', namespace: 'ci-stage', srcStream: 'infographic', srcTag: 'latest'
		openshiftVerifyDeployment apiURL: '10.1.2.2:8443', authToken: apiToken, depCfg: 'infographic', namespace: 'ci-prod'
	}
}