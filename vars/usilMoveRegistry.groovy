def call(def dockerRegistrySource,def dockerRegistrySourceUser, def dockerRegistryCible, def dockerRegistryCibleUser) {
    stage("Deplacemement image Docker sur le repo de production"){
        withCredentials([usernamePassword( credentialsId: dockerRegistrySourceUser , usernameVariable: 'userName', passwordVariable: 'password')]) {
			sh "docker login -u ${userName} -p ${password} ${dockerRegistryUrl}"
			sh "docker pull dockerRegistrySource"
			def ImageId = sh ( script : "docker inspect --format='{{json .id}}'",returnStdout: true).trim()
			sh "docker tag ${ImageId} dockerRegistryCible"
            }
        withCredentials([usernamePassword( credentialsId: dockerRegistryCibleUser , usernameVariable: 'userName', passwordVariable: 'password')]) {
			sh "docker login -u ${userName} -p ${password} ${dockerRegistryUrl}"
			sh "docker push dockerRegistryCible"
            }
        }
    }