def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli){
docker.withRegistry(dockerRegistryUrl) {
    docker.image(dockerImageName).inside('-v /appli/jenkins/settings.xml:/usr/share/maven/ref/settings.xml -v /appli/jenkins/mavenrepo:/var/maven/.m2' ) {
		stage('MVN install') {
            sh 'mvn install -Dconsole -Duser.home=/var/maven'
			}
		stage('Test Junit Maven') {
			parallel(
				"JUnit test": {
					sh ('mvn test -Duser.home=/var/maven')
					},
				"Sonar":{
					withSonarQubeEnv('SONARQUBE_USIL3') {
					sh "mvn sonar:sonar -X -Dsonar.projectKey=${env.gitProjectName} -Duser.home=/var/maven"
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
            sh ('mvn clean package -DskipTests=true -Duser.home=/var/maven')
            }
        stage('Publish Nexus') {
            sh ('mvn deploy -Duser.home=/var/maven')	
			}
		}
	}
}