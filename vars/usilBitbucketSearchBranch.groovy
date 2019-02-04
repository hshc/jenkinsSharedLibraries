import groovy.json.JsonSlurper
def call(def repo) { 	
		script {
			def response = httpRequest authentication: 'jenkins-bitbucket-common-creds', url: "https://api.bitbucket.org/2.0/repositories/si2m/${repo}_dico/refs/branches", ignoreSslErrors:true
			echo '***************'
			echo response.status)
			echo '***************'
			def json = new JsonSlurper().parseText(response.content)
			def ListEnv = []
			for (result in json.values) {
                ListEnv.add(result.name)
                }
            return ListEnv
			}
		}