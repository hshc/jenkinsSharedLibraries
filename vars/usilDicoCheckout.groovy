def call(def gitBitbucketUrl,def codeEnv, def gitProjectDico) {
	stage("Checkout Dico ${codeEnv}"){
      sh ("rm -f -r ${codeEnv}")
      checkout([$class: 'GitSCM', branches: [[name: "*/${codeEnv}"]],
      doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${codeEnv}"]],
      submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'bitbucket-cloud', url: "${gitBitbucketUrl}${gitProjectDico}",refspec: "+refs/heads/${codeEnv}:refs/remotes/origin/${codeEnv}"]]])
    }
}