def call(def chartTemplateName,def gitBitbucketUrl) {
	stage("Checkout Template Helm : ${chartTemplateName}"){
      // suppression du répertoire 
      sh ("rm -f -r ${chartTemplateName}")
      // récupération du modèle chart Helm sur le repo GIT et stockage dans le dossier avec le nom du modèle 
      checkout([$class: 'GitSCM', branches: [[name: '*/master']], 
      doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout'],[$class: 'RelativeTargetDirectory', relativeTargetDir: "${chartTemplateName}"]], submoduleCfg: [], userRemoteConfigs: 
      [[credentialsId: 'jenkins-bitbucket-common-creds', url: "${gitBitbucketUrl}${chartTemplateName}"]]])
    }
}
