def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli){
docker.withRegistry(${dockerRegistryUrl}) {
    docker.image({$dockerImageName}).inside {
      stage('Build') {
        sh 'npm ci --unsafe-perm'
      }
      stage('Qualimetry') {
        parallel(
          "Tests": {
            sh 'npm run test:ci'
          },
          "Lint":{
            sh 'npm run lint'
          }
        )
      }
      if (${gitBranchName} == 'master' || ${gitBranchName} == 'develop') {
        stage('NPM Pack') {
          sh ('npm pack')
        }

        if(${gitBranchName} == 'master' && ${trigrammeAppli} == 'BPP') {
          stage('Publish Nexus') {
            sh ('npm publish --registry ${nexusRepo}')
          }
        }
      }
    }
  }
}