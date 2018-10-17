def call(def codeEnv,def dockerRegistryRepoAppli,def gitProjectName) {
	// gestion des certificats pour connexion UCP
    stage ("Deploiement UCP Docker E2E env:${codeEnv}") {
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
        //Chargement du fichier dico
        def props = readProperties interpolate: false, file: "${codeEnv}/.env"
        def urlRecf=props.URL;
        def prefixeUrl = sh(returnStdout: true, script: "echo '$urlRecf' | awk -F'http://' '{print \$2}' |  awk -F'.int.c-cloud' '{print \$1}'").trim()       
        sh "sed -i 's/${prefixeUrl}.recf.c-cloud/${prefixeUrl}-e2e.recf.c-cloud/g' ${codeEnv}/.env"
	    withEnv(['DOCKER_TLS_VERIFY=1',"DOCKER_CERT_PATH=${dockerCertPath}","DOCKER_HOST=${dockerUcp}"])
	    	{
			sh "export DTRIMAGE=${dockerRegistryRepoAppli} && cd ${codeEnv} && docker-compose config > docker-compose-deploy.yml"
			sh "docker stack deploy --prune --compose-file=${codeEnv}/docker-compose-deploy.yml ${gitProjectName}-e2e"
	    	}
    }    
}