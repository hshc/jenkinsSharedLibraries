def call(def codeEnv,def gitProjectName,def sourcePath,def destinationPath) {
	// gestion des certificats pour connexion UCP
	stage ("Deploiement UCP Docker env:${codeEnv}") {
		def dockerCertPath
		def dockerUcp
		if (codeEnv == 'e0') {
			dockerUcp="tcp://ucp.docker.si2m.tec:443"
			dockerCertPath="${JENKINS_HOME_SLAVE}/docker_ucp_prod"
			}
		else if (codeEnv == 'e1') {
			dockerUcp="tcp://ucp.pprod.docker.si2m.tec:443"
			dockerCertPath="${JENKINS_HOME_SLAVE}/docker_ucp_pprod/"
			}
		else {
			dockerUcp="tcp://ucp.recf.docker.si2m.tec:443"
			dockerCertPath="${JENKINS_HOME_SLAVE}/docker_ucp_recf/"
		}
	    withEnv(['DOCKER_TLS_VERIFY=1',"DOCKER_CERT_PATH=${dockerCertPath}","DOCKER_HOST=${dockerUcp}"])
	    	{
            dockerId = sh(returnStdout: true, script: "docker ps -f name=${gitProjectName}_${codeEnv} --format '{{.ID}}'").trim()
            sh "docker cp ${sourcePath} ${dockerId}:${destinationPath}"
            }
    }
}