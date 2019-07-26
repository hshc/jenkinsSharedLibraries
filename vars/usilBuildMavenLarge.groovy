def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli, def mvnOptionnalArgs=''){
	docker.withRegistry(dockerRegistryUrl) {
		docker.image(dockerImageName).inside('-e MAVEN_CONFIG=/var/maven/.m2 -v /appli/jenkins/settings.xml:/usr/share/maven/ref/settings.xml -v /appli/jenkins/mavenrepo:/var/maven/.m2:rw') {
			stage('MVN compile') {
				sh "mvn clean compile ${mvnOptionnalArgs} -DskipTests -Dconsole -Duser.home=/var/maven"
			}
			stage('Test Junit') {
				sh "mvn test ${mvnOptionnalArgs} -Duser.home=/var/maven"
			}
			stage('Sonar (Memory)') {
				withSonarQubeEnv('SONARQUBE_USIL3') {
					sh """
					export MAVEN_BATCH_ECHO=on
					echo '-Xmx4096m -Xms2048m -XX:MaxPermSize=256m' > .mvn/jvm.config
					mvn sonar:sonar -Dsonar.projectKey=${env.gitProjectName} -Duser.home=/var/maven/.m2 -s /usr/share/maven/ref/settings.xml -e
					"""
				}
			}
			stage('Quality Gate') {
				def qg = waitForQualityGate()
				if (qg.status != 'OK') {
					error "Pipeline aborted due to quality gate failure: ${qg.status}"
				}
			}
			stage('Publish Nexus') {
				sh "mvn deploy ${mvnOptionnalArgs} -Duser.home=/var/maven -s /usr/share/maven/ref/settings.xml"
			}
		}
	}
}
