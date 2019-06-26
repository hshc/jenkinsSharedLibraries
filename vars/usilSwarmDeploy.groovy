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
		     
			 println mydata.getClass()
			 println mydata.services.getClass()
			 println mydata.services
			 

    		  //modify

			  //println mydata
			  // println mydata.version
			  // println mydata.services
			  // println (mydata.services =~ /(?<=\{)(.*?)(?={image)/ )
			  // println (mydata.services =~ /(?<=\{)(.*?)(?=\{image)/)
			  //(?<={)(.*)(?=\={image)
			  
			  //def regexPattern = /(?<=\{)(.*)(?=\{image)/
			  //def nomService = mydata.services =~/(?<=\{)(.*?)(?=\{image)/
			  //def nomService = mydata.service as String
			 
  			//	println nomService[0]
			 
			// s = "This is a simple string 234 something else here as well 4334"
			//regexPattern = /([0-9]{3})/
			//def matcher = ( nomService =~ regexPattern )
			//def p = "[hello],[1,2],[(1,2),(2,4)]"
			//def nomService = mydata.services.findAll(/(?<=\{)(.*)(?=\{image)/){match, group -> group }
			// println nomService[0]
		    




			// def matcher = (mydata.services =~ regexPattern)
			 println "TEST !!!"
			// println matcher
			 //println matcher.count
			 //println mydata.services


			//if (matcher.matches()) {
    		//	println(matcher.getCount()+ " occurrence of the regular expression was found in the string.");
    			//println(matcher[0][1] + " found!")
			//}
			//else
			//{
			//	println "marche pas !!! "
			//}


			 // println mydata.services.appli.image
			 // println mydata.services.appli.deploy.labels
			
			// println mydata.services.key

			 //println mydata.services.key.value

			 
		//	 def arrayLabels=mydata.services.appli.deploy.labels as String[]
			 
			 // println arrayLabels.length
		//	 mydata.services.appli.deploy.labels[arrayLabels.length]="com.docker.lb.backend_mode=vip"

			 // println arrayLabels.length
    	//	 writeYaml file: "${env.WORKSPACE}/${codeEnv}/docker-compose-modif.yaml", data: mydata


		
    	//return noOfLines
		//}






			// sh "export DTRIMAGE=${dockerRegistryRepoAppli} && cd ${codeEnv} && docker-compose config > docker-compose-deploy.yml"
			// sh "docker stack deploy --prune --compose-file=${codeEnv}/docker-compose-deploy.yml ${gitProjectName}_${codeEnv}"
			// sleep(time:60,unit:"SECONDS")
			// def checkService = sh(returnStdout: true, script: "docker stack services '${gitProjectName}'_'${codeEnv}' --format '{{.Replicas}}'").trim()
			// echo "Deploiement de la stack ${gitProjectName}_${codeEnv}: ${checkService}"
			// if (checkService ==~ /^0\/.*$/)
			// 	{
			//	echo '[FAILURE] Erreur de deploiement du service ou conteneur'
        	//	currentBuild.result = 'FAILURE'
			//	}

	    	}
    }
}