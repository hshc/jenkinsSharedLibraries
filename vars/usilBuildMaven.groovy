def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli){
docker.withRegistry(dockerRegistryUrl) {
    docker.image(dockerImageName).inside('-e MAVEN_CONFIG=/var/maven/.m2 -v /appli/jenkins/settings.xml:/usr/share/maven/ref/settings.xml -v /appli/jenkins/mavenrepo:/var/maven/.m2:rw') {
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
					sh "export SONAR_USER_HOME='/var/maven/.sonar' && mvn sonar:sonar -Dsonar.projectKey=${env.gitProjectName} -Duser.home=/var/maven"
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