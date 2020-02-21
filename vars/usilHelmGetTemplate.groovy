def call(def chartTemplateName,def gitBitbucketHelmChartUrl,def gitProjectName) {
	stage("Checkout Template Helm"){
      sh ("rm -f -r ${chartTemplateName}")
      checkout([$class: 'GitSCM', branches: [[name: "*/${chartTemplateName}"]],
      doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${chartTemplateName}"]],
      submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jenkins-bitbucket-common-creds', url: "${gitBitbucketUrl}${chartTemplateName}",refspec: "+refs/heads/${chartTemplateName}:refs/remotes/origin/${chartTemplateName}"]]])
    }
}