def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli){
docker.withRegistry(dockerRegistryUrl) {
    docker.image(dockerImageName).inside {
		stage('MVN install') {
            sh 'mvn install -Dconsole'
			}
		stage('Test Junit Maven') {
			parallel(
				"JUnit test": {
					sh ('mvn test')
					},
				"Sonar":{
					withSonarQubeEnv('SONARQUBE_USIL3') {
					sh "mvn sonar:sonar -Dsonar.projectKey=${env.gitProjectName}"
					}
                }
			)
		}
		stage('Quality Gate') {
			def qg = waitForQualityGate()
            if (qg.status != 'OK') {
				error "Pipeline aborted due to quality gate failure: ${qg.status}"
				}
            }	
		stage('Maven Pack') {
            sh ('mvn clean package -DskipTests=true')
            }
        stage('Publish Nexus') {
            sh ('mvn deploy')	
			}
		}
	}
}