def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli,def isBuildRun=false){
//echo dockerRegistryUrl
docker.withRegistry(dockerRegistryUrl) {
    docker.image(dockerImageName).inside("--entrypoint=''") {
      stage('Build') {
        sh 'sudo mkdir -p /home/siddharthsaha/tmp'
        sh 'npm cache clear'
        sh 'npm ci'
        if (isBuildRun == true) {
          sh 'npm run build'
        }
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
      if (gitBranchName == 'master' || gitBranchName == 'develop') {
        stage('NPM Pack') {
          sh ('npm pack')
        }

        if(gitBranchName == 'master' && trigrammeAppli == 'BPP') {
          stage('Publish Nexus') {
            sh ('npm publish --registry ${nexusRepo}')
          }
        }
      }
    }
  }
}