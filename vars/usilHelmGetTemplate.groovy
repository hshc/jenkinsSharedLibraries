def call(def chartTemplateName,def gitBitbucketUrl) {
	stage("Checkout Template Helm"){
      sh ("rm -f -r ${chartTemplateName}")
      checkout([$class: 'GitSCM', branches: [[name: '*/master']], 
      doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: 
      [[credentialsId: 'jenkins-bitbucket-common-creds', url: 'https://bitbucket.org/si2m/chart_nodejs']]])
      // checkout([$class: 'GitSCM', branches: [[name: "*/${chartTemplateName}"]],
      // doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${chartTemplateName}"]],
      // submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jenkins-bitbucket-common-creds', url: "${gitBitbucketUrl}${chartTemplateName}"]]])
    }
}