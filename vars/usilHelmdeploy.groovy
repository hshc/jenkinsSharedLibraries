def call(def codeEnv,def dockerRegistryRepoAppli,def trigrammeAppli,def gitProjectName) {

stage("Déploiement kube: ${gitProjectName} environnement: ${codeEnv}"){
       // récupération de la conf dédié à l'environnement Kube
       switch("${codeEnv}") { 
       case "e2": 
              nomEnv="rect"
       default:
              nomEnv="int"
       } 

       // Initialisation des variables commande
       helmTemplate = "~/helm template ${gitProjectName} " + 
              "--set modelTemplate.image.repository=${dockerRegistryRepoAppli} " + 
              "--set modelTemplate.environment=${codeEnv} " +
              "--set modelTemplate.name=${gitProjectName} " +
              "--set serviceAccountName=\"sifront\" " +
              "--set secretName=${nomEnv}-mycloud-secret " + 
              "--set modelTemplate.version=latest " + 
              "> ${gitProjectName}.yaml"
       kubeApply = "~/kubectl apply --namespace ${trigrammeAppli} -f ${gitProjectName}.yaml"
       kubeConfig = "~/kubectl config set-context cluster--n ${trigrammeAppli}"

       // Lancement des commandes
       usilColorLog("debug", "kubeConfig commande: ${kubeConfig}")
       sh ("${kubeConfig}")

       usilColorLog("Info", "HelmTemplate commande:  ${helmTemplate}")
       sh ("${helmTemplate}")

       usilColorLog("warning", "KubeApply commande: ${kubeApply}")
       // sh ("$kubeApply")

       // podLog = sh (script : "kubectl logs -l app=${nomContainer} --tail 1", returnStdout: true)
       // deploymentStatus = sh "kubectl rollout status ${gitProjectName}"
       // echo "log ${nomContainer} ${dockerLog}"
}
}