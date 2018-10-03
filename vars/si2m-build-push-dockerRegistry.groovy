def call(def dockerRegistryUrl,def userDockerRegistry, def dockerRegistryRepoAppli){
    stage('Build Docker & push DTR'){
      withCredentials([usernamePassword( credentialsId: {$userDockerRegistry}, usernameVariable: 'userName', passwordVariable: 'password')]) {
        docker.withRegistry('${dockerRegistryUrl}') {
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
