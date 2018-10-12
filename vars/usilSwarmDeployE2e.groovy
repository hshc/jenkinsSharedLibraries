def call(def codeEnv,def dockerRegistryRepoAppli,def gitProjectName) {
	// gestion des certificats pour connexion UCP
	    def dockerUcp='tcp://ucp.recf.docker.si2m.tec:443'
	    def dockerCertPath='$JENKINS_HOME/docker_ucp_recf'
        
        echo 'chargement du fichier'
        //Properties properties = new Properties()
        def props = readProperties interpolate: false, file: "${codeEnv}/.env"
        def urlRecf=props.URL;
        echo urlRecf
        def prefixeUrl = sh(returnStdout: true, script: "echo '$urlRecf' | awk -F'http://' '{print \$2}' |  awk -F'.int.c-cloud' '{print \$1}'").trim()
    //    def urlE2e = "${prefixeUrl}-e2e.recf.cloud.si2m.tec"
     //   def amap=['URL':"${urlE2e}"']
     //   writeYaml file: "${codeEnv}/.env", data :amap
        
        ant.replace(file: "${codeEnv}/.env", token: "http://${prefixeUrl}", value: "http://${prefixeUrl}-e2e")
        
    //stage ('Deploiement UCP Docker ${codeEnv}') {
	//	withEnv(['DOCKER_TLS_VERIFY=1',"DOCKER_CERT_PATH=$dockerCertPath","DOCKER_HOST=${dockerCertPath}"]) {
	//		sh "export DTRIMAGE=${dockerRegistryRepoAppli} && cd ${codeEnv} && docker-compose config -e URL=${urlE2e} > docker-compose-deploy.yml"
	//		sh "docker stack deploy --prune --compose-file=${codeEnv}/docker-compose-deploy.yml ${gitProjectName}"
	//	}
    //}
}