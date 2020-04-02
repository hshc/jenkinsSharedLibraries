def call(def codeEnv,def dockerRegistryRepoAppli,def trigrammeAppli,def gitProjectName) {

stage("Déploiement kube: ${gitProjectName} environnement: ${codeEnv}"){
       // récupération de la conf dédié à l'environnement Kube
       switch("${codeEnv}") { 
       case "e2": 
              nomEnv="rect"; break;
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
       kubeConfig = "~/kubectl config set-context cluster--n ${trigrammeAppli}"
       kubeApply = "~/kubectl apply --namespace ${trigrammeAppli} -f ${gitProjectName}.yaml"

       // Lancement des commandes
       ansiColor('xterm') {
            echo "HelmTemplate commande:  ${helmTemplate}"
        }
       //usilColorLog("info", "HelmTemplate commande:  ${helmTemplate}")
       sh ("${helmTemplate}")

       //usilColorLog("debug", "kubeConfig commande: ${kubeConfig}")
       sh ("${kubeConfig}")

       //usilColorLog("warning", "KubeApply commande: ${kubeApply}")
       // sh ("$kubeApply")

       // podLog = sh (script : "kubectl logs -l app=${nomContainer} --tail 1", returnStdout: true)
       // deploymentStatus = sh "kubectl rollout status ${gitProjectName}"
       // echo "log ${nomContainer} ${dockerLog}"
}
}