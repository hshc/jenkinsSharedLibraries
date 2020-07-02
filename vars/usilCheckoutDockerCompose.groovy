def call(def codeEnv) {
      //checkout([$class: 'GitSCM', 
                  //branches: [[name: "*/master"]],
                  //doGenerateSubmoduleConfigurations: false, 
                  //extensions: [[$class: 'CleanBeforeCheckout'],[$class: 'RelativeTargetDirectory', relativeTargetDir: "${codeEnv}"]],
                  //submoduleCfg: [], 
                  //userRemoteConfigs: [[credentialsId: 'dev-jenkins', url: "https://gitlab.si2m.tec/usil/usil-dc.git"]]
                  //userRemoteConfigs: [[credentialsId: 'jenkins-bitbucket-common-creds', url: "${gitBitbucketUrl}${gitProjectDico}", refspec: "+refs/heads/master:refs/remotes/origin/master"]]])
                  
      checkout([$class: 'GitSCM', 
                  branches: [[name: '*/master']], 
                  doGenerateSubmoduleConfigurations: false, 
                  extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'e3']], 
                  submoduleCfg: [], 
                  userRemoteConfigs: [[credentialsId: 'Gitlab', url: 'ssh://git@gitlab.si2m.tec:8022/usil/usil-dc.git']]])
        ])
}