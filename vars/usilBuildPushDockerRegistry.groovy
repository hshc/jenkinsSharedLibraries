def call(def dockerRegistryUrl,def dockerRegistryUser, def dockerRegistryRepoAppli){
    stage('Build Docker & push DTR'){
      echo dockerRegistryUser
      withCredentials([usernamePassword( credentialsId: ${dockerRegistryUser} , usernameVariable: 'userName', passwordVariable: 'password')]) {
        docker.withRegistry(dockerRegistryUrl) {
			sh "docker login -u ${userName} -p ${password} ${dockerRegistryUrl}"
			def imageDocker= docker.build("${dockerRegistryRepoAppli}",'--no-cache --rm .')
			imageDocker.push()
			// 
			// if (env.BRANCH_NAME == 'master') {
			// 		imageDocker.push('latest') }
        }
      }
    }
  }
