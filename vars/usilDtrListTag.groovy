def dockerListTag(def repo) { 	
		script {
			def response = httpRequest authentication: 'DockerDTR', url: "https://dtr.docker.si2m.tec/api/v0/repositories/${repo}/tags", ignoreSslErrors:true
			def json = new JsonSlurper().parseText(response.content)
			def listTag = []
			for (result in json) {
                listTag.add(result.name)
                }
            return listTag
			}
		}