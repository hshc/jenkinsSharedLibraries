def call(def dockerRegistryUrl,def dockerRegistryUser, def dockerRegistryRepoAppli){
    stage("Build image Docker : ${dockerRegistryRepoAppli}  & push DTR"){
      withCredentials([usernamePassword( credentialsId: dockerRegistryUser , usernameVariable: 'userName', passwordVariable: 'password')]) {
        docker.withRegistry(dockerRegistryUrl) {
			sh "docker login -u ${userName} -p ${password} ${dockerRegistryUrl}"
			def imageDocker= docker.build("${dockerRegistryRepoAppli}",'--no-cache --rm .')
			imageDocker.push()
        }
      }
    }
  }
