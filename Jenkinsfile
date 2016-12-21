node {
    stage('Checkout') {
        checkout scm
    }
    stage('Build and Publish Artifacts') {
        echo 'Using build tool: mvn-3'
        def toolHome = tool 'mvn-3'
        env.JAVA_HOME = tool 'java-1.8'
        sh "${toolHome}/bin/mvn clean deploy -Denforcer.skip=true"
    }
    stage('Archive Artifacts For Public Download'){
        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
    }
}