node {
    stage ('Code Checkout') {
        git url: 'https://github.com/rht-labs/automation-api.git', branch: 'master'
    }

    stage ('Build App') {
        echo 'Using build tool: mvn-3'
        def toolHome = tool 'mvn-3'
        env.JAVA_HOME = tool 'java-1.8'
        sh "${toolHome}/bin/mvn clean install"
    }


    stage ('Build Image and Deploy to Dev') {
        echo 'Found label "provider=fabric8", we are generating the s2i binary build commands'
        sh 'oc login null --token=$OPENSHIFT_API_TOKEN --insecure-skip-tls-verify'
        sh 'oc start-build automation-api --from-dir=. --follow -n labs-api-dev'  }

    stage ('Deploy to labs-api-uat') {
        input 'Deploy to labs-api-uat?'
        openshiftTag apiURL: 'null', authToken: $OPENSHIFT_API_TOKEN, destStream: 'automation-api', destTag: 'latest', destinationAuthToken: $OPENSHIFT_API_TOKEN, destinationNamespace: 'labs-api-uat', namespace: 'labs-api-dev', srcStream: 'automation-api', srcTag: 'latest'
        openshiftVerifyDeployment apiURL: 'null', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'automation-api', namespace: 'labs-api-uat'
    }

    stage ('Deploy to labs-api-production') {
        input 'Deploy to labs-api-production?'
        openshiftTag apiURL: 'null', authToken: $OPENSHIFT_API_TOKEN, destStream: 'automation-api', destTag: 'latest', destinationAuthToken: $OPENSHIFT_API_TOKEN, destinationNamespace: 'labs-api-production', namespace: 'labs-api-uat', srcStream: 'automation-api', srcTag: 'latest'
        openshiftVerifyDeployment apiURL: 'null', authToken: $OPENSHIFT_API_TOKEN, depCfg: 'automation-api', namespace: 'labs-api-production'
    }
}