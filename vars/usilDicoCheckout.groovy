def call(def gitBitbucketUrl,def codeEnv, def gitProjectDico) {
	stage("Checkout Dico env:${codeEnv}"){
      sh ("rm -f -r ${codeEnv}")
      checkout([$class: 'GitSCM', branches: [[name: "*/${codeEnv}"]],
      doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout'],[$class: 'RelativeTargetDirectory', relativeTargetDir: "${codeEnv}"]],
      submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jenkins-bitbucket-common-creds', url: "${gitBitbucketUrl}${gitProjectDico}",refspec: "+refs/heads/${codeEnv}:refs/remotes/origin/${codeEnv}"]]])
    }
}