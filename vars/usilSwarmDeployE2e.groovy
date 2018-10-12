def call(def codeEnv,def dockerRegistryRepoAppli,def gitProjectName) {
	// gestion des certificats pour connexion UCP
	    def dockerUcp='tcp://ucp.recf.docker.si2m.tec:443'
	    def dockerCertPath='$JENKINS_HOME/docker_ucp_recf'
        
        echo 'chargement du fichier'
        Properties properties = new Properties()
        File propertiesFile = new File("${WORKSPACE}/${codeEnv}/.env")
        propertiesFile.withInputStream {
            properties.load(it)
            }
        def urlE2e=properties.URL;
        echo urlE2e
        
   // stage ('Deploiement UCP Docker ${codeEnv}') {
	//	withEnv(['DOCKER_TLS_VERIFY=1',"DOCKER_CERT_PATH=$dockerCertPath","DOCKER_HOST=${dockerCertPath}"]) {
	//		sh "export DTRIMAGE=${dockerRegistryRepoAppli} && cd ${codeEnv} && docker-compose config > docker-compose-deploy.yml"
	//		sh "docker stack deploy --prune --compose-file=${codeEnv}/docker-compose-deploy.yml ${gitProjectName}"
	//	}
   // }
}