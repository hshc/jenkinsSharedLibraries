import groovy.json.JsonSlurper
def call(def trigramme) { 	
		script {
		    def request= trigramme.toLowerCase()
			def response = httpRequest authentication: 'DockerDTR', url: "https://dtr.docker.si2m.tec/api/v0/index/dockersearch?q=${request}", ignoreSslErrors:true
			def json = new JsonSlurper().parseText(response.content)
			def listRepo = []
			for (result in json.results) {
			    if (result.name =~ /^repo-store.*$/) {
                    listRepo.add(result.name)
			        }
                }
			if (listRepo.isEmpty())
				{
				return 'Pas de repo disponible'
				}
			else
				{
				return listRepo
				}
			}
		}