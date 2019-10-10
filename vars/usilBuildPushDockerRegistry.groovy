def call(def dockerRegistryUrl,def dockerRegistryUser, def dockerRegistryRepoAppli){
    stage("Build image Docker : ${dockerRegistryRepoAppli}  & push DTR"){
      withCredentials([usernamePassword( credentialsId: dockerRegistryUser , usernameVariable: 'userName', passwordVariable: 'password')]) {
        docker.withRegistry(dockerRegistryUrl) {
			sh "docker login -u ${userName} -p ${password} ${dockerRegistryUrl}"
			def imageDocker= docker.build("${dockerRegistryRepoAppli}",'--no-cache --rm .')
			imageDocker.push()
        }
      }
    // connexion à la dtr via API pour changer la visibilité du repo : visibility public 
    def urlRepo= "${dockerRegistryUrl}/api/v0/repositories/${dockerRegistryRepoAppli.replaceAll(/:.*$/, "")}"
    def response = httpRequest authentication: 'DockerDTR', acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'PATCH', requestBody: '{"visibility": "public"}', url: urlRepo, ignoreSslErrors:true
    println response.status
    if (response.status != 200) {
      println "Repo sur la DTR inexistant"
      currentBuild.result = 'FAILURE'
    }
  }
}
