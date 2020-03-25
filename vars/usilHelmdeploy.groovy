def call(def codeEnv,def dockerRegistryRepoAppli,def gitProjectName) {
 // A coder
 // récupération de la conf dédié à l'environnement Kub

 // Lancement de l'image docker Helm / kub 

 
 // il peut être inétressant de stocker le fichier template
 helmTemplate = "helm template ${gitProjectName} " + 
       "--set modelTemplate.image.repository=${dockerRegistryRepoAppli} " + 
       "--set modelTemplate.environment=${codeEnv} " +
       "--set modelTemplate.name=${gitProjectName} " +
       "--set serviceAccountName=\"sifront\" " +
       "--set secretName=${codeEnv}-mycloud-secret " + 
       "--set modelTemplate.version=latest " + 
       "> ${gitProjectName}.yaml"
 kubeApply = "kubectl apply --namespace ${gitProjectName}.substring(0,2) -f ${gitProjectName}.yaml"
 sh "$helmTemplate"
 sh "$helmTemplate"
 
 // la ligne de commande pour installer 
 // "helm install ${gitProjectName} --namespace ${gitProjectName} --set modelTemplate.image.repository="${dockerRegistryRepoAppli}" --set modelTemplate.environment="${codeEnv}" --set modelTemplate.name=${gitProjectName}"

}