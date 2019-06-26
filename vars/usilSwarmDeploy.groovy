@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

def call(def codeEnv,def dockerRegistryRepoAppli,def gitProjectName) {
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
			 def mydata = readYaml file: "${env.WORKSPACE}/${codeEnv}/docker-compose.yml"
			 def nomService = mydata.services.keySet()			 
			 def arrayLabels=mydata.services.get(nomService[0]).deploy.labels as String[]
			 
			for (String key : mydata.services.get(nomService[0]).deploy.labels.keySet()) {
				 println(key +"   clé  " + mydata.services.get(nomService[0]).deploy.labels(key))
			 }
			 
			 
			 mydata.services.get(nomService[0]).deploy.labels[arrayLabels.length]="com.docker.lb.backend_mode=vip"
    		 sh ("rm -f ${env.WORKSPACE}/${codeEnv}/docker-compose.yml")
			 writeYaml file: "${env.WORKSPACE}/${codeEnv}/docker-compose.yaml", data: mydata

			/* sh "export DTRIMAGE=${dockerRegistryRepoAppli} && cd ${codeEnv} && docker-compose config > docker-compose-deploy.yml"
			 sh "docker stack deploy --prune --compose-file=${codeEnv}/docker-compose-deploy.yml ${gitProjectName}_${codeEnv}"
			 sleep(time:60,unit:"SECONDS")
			 def checkService = sh(returnStdout: true, script: "docker stack services '${gitProjectName}'_'${codeEnv}' --format '{{.Replicas}}'").trim()
			 echo "Deploiement de la stack ${gitProjectName}_${codeEnv}: ${checkService}"
			 if (checkService ==~ /^0\/.*$/)
			 	{
				echo '[FAILURE] Erreur de deploiement du service ou conteneur'
        		currentBuild.result = 'FAILURE'
				}
			*/
	    	}
    }
}