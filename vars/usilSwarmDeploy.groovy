def call(def codeEnv,def dockerRegistryRepoAppli,def gitProjectName) {
	// gestion des certificats pour connexion UCP
	if (codeEnv == 'e0') {
		def dockerUcp='tcp://ucp.docker.si2m.tec:443'
		def dockerCertPath="$JENKINS_HOME/docker_ucp_prod"
		}
	else if (codeEnv == 'e1') {
		def dockerUcp='tcp://ucp.pprod.docker.si2m.tec:443'
		def dockerCertPath="$JENKINS_HOME/docker_ucp_pprod"
		}
	else {
		def dockerUcp='tcp://ucp.recf.docker.si2m.tec:443'
		def dockerCertPath="$JENKINS_HOME/docker_ucp_recf"
	}
    stage ("Deploiement UCP Docker ${codeEnv}") {
		withEnv(['DOCKER_TLS_VERIFY=1',"DOCKER_CERT_PATH=$dockerCertPath","DOCKER_HOST=${dockerCertPath}"]) {
			sh "export DTRIMAGE=${dockerRegistryRepoAppli} && cd ${codeEnv} && docker-compose config > docker-compose-deploy.yml"
			sh "docker stack deploy --prune --compose-file=${codeEnv}/docker-compose-deploy.yml ${gitProjectName}"
		}
    }
}