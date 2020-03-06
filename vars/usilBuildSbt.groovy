def call(def dockerRegistryUrl, def dockerImageName, def sbtOptionnalArgs=''){
	docker.withRegistry(dockerRegistryUrl) {
		docker.image(dockerImageName).inside('-v /var/lib/jenkins/.sbt:/root/.sbt -v /var/lib/jenkins/.ivy2:/root/.ivy2 ') {
			stage('SBT Compile Coverage Test') {
			     sh "${sbtOptionnalArgs} sbt -Dsbt.global.base=.sbt -Dsbt.ivy.home=/home/jenkins/.ivy2 -Divy.home=/home/jenkins/.ivy2 compile coverage jacoco:cover coverageReport coverageOff"
			}
			stage('Sonar') {
				withSonarQubeEnv('SONARQUBE_USIL3') {
				  sh "sbt sonarScan -Dsonar.projectKey=${env.gitProjectName} -Dsbt.global.base=.sbt -Dsbt.ivy.home=/home/jenkins/.ivy2 -Divy.home=/home/jenkins/.ivy2"
				}
			}
			stage('Quality Gate') {
                def qg = waitForQualityGate()
                if (qg.status != 'OK') {
                    error "Pipeline aborted due to quality gate failure: ${qg.status}"
                }
            }
			stage('SBT Package') {
				sh "${sbtOptionnalArgs} sbt -Dsbt.global.base=.sbt -Dsbt.ivy.home=/home/jenkins/.ivy2 -Divy.home=/home/jenkins/.ivy2 universal:packageZipTarball"
			}
		}
	}
}