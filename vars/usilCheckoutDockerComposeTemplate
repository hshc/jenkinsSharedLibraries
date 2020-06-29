def call(def codeEnv) {
      checkout([$class: 'GitSCM', 
                  branches: [[name: "*/master"]],
                  doGenerateSubmoduleConfigurations: false, 
                  extensions: [[$class: 'CleanBeforeCheckout'],[$class: 'RelativeTargetDirectory', relativeTargetDir: "${codeEnv}"]],
                  submoduleCfg: [], 
                  userRemoteConfigs: [[credentialsId: 'dev-jenkins', url: "https://gitlab.si2m.tec/j19f210/usil.gitt"]]
                  //userRemoteConfigs: [[credentialsId: 'jenkins-bitbucket-common-creds', url: "${gitBitbucketUrl}${gitProjectDico}", refspec: "+refs/heads/master:refs/remotes/origin/master"]]])
        ])
}