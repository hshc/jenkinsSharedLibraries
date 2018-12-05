def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli, def mvnTestOptionnalArgs){
docker.withRegistry(dockerRegistryUrl) {
    docker.image(dockerImageName).inside('-e MAVEN_CONFIG=/var/maven/.m2 -v /appli/jenkins/settings.xml:/usr/share/maven/ref/settings.xml -v /appli/jenkins/mavenrepo:/var/maven/.m2:rw') {
		stage('MVN compile') {
            sh 'mvn clean compile -DskipTests -Dconsole -Duser.home=/var/maven'
		}
		stage('Test Junit') {
            sh "mvn test ${mvnTestOptionnalArgs} -Duser.home=/var/maven"
		}
		stage('Sonar') {
			withSonarQubeEnv('SONARQUBE_USIL3') {
				sh "mvn sonar:sonar -Dsonar.projectKey=${env.gitProjectName} -Duser.home=/var/maven/.m2 -s /usr/share/maven/ref/settings.xml"
			}
		}
		stage('Quality Gate') {
			def qg = waitForQualityGate()
            if (qg.status != 'OK') {
				error "Pipeline aborted due to quality gate failure: ${qg.status}"
			}
		}	
		stage('Maven Install') {
            sh ('mvn install -DskipTests -Duser.home=/var/maven -s /usr/share/maven/ref/settings.xml')
		}
        stage('Publish Nexus') {
            sh ('mvn deploy -Duser.home=/var/maven -s /usr/share/maven/ref/settings.xml')	
		}
	}
}