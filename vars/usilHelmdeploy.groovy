def call(def codeEnv,def dockerRegistryRepoAppli,def trigrammeAppli,def gitProjectName) {

stage("Déploiement kube: ${gitProjectName} environnement: ${codeEnv}"){
       // récupération de la conf dédié à l'environnement Kub
       switch("${codeEnv}") { 
       case "e2": 
              nomEnv="rect"
       default:
              nomEnv="int"
       } 
       // Lancement de l'image docker Helm / kub 

       
       // il peut être inétressant de stocker le fichier template
       helmTemplate = "./helm template ${gitProjectName} " + 
              "--set modelTemplate.image.repository=${dockerRegistryRepoAppli} " + 
              "--set modelTemplate.environment=${codeEnv} " +
              "--set modelTemplate.name=${gitProjectName} " +
              "--set serviceAccountName=\"sifront\" " +
              "--set secretName=${nomEnv}-mycloud-secret " + 
              "--set modelTemplate.version=latest " + 
              "> ${gitProjectName}.yaml"
              
       wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
         echo "\033[1;32m[Info] HelmTemplate commande:  $helmTemplate\033[0m" }
       sh ("$helmTemplate")

       kubeApply = "kubectl apply --namespace ${trigrammeAppli} -f ${gitProjectName}.yaml"
       wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
         echo "\033[1;32m[Info] kubectl config set-context cluster--n ${trigrammeAppli}\033[0m" }
       sh ("kubectl config set-context cluster--n ${trigrammeAppli}")



       wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
         echo "\033[1;32m[Info] KubeApply commande: $kubeApply\033[0m" }
       // sh ("$kubeApply")

       // podLog = sh (script : "kubectl logs -l app=${nomContainer} --tail 1", returnStdout: true)
       // deploymentStatus = sh "kubectl rollout status ${gitProjectName}"
       // echo "log ${nomContainer} ${dockerLog}"
}
}