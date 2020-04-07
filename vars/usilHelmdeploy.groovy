def call(def codeEnv,def dockerRegistryRepoAppli,def trigrammeAppli,def helmProjectName) {

stage("Déploiement kube: ${helmProjectName} environnement: ${codeEnv}"){
       // récupération de la conf dédié à l'environnement Kube
       switch("${codeEnv}") { 
       case "e2": 
              nomEnv="rect"; break;
       default:
              nomEnv="int"
       } 

       // Génération du chart.yaml
       def cmap = ['apiVersion': 'v2',
                     'name': helmProjectName,
                     'type': 'application',
                     'appVersion': '1.0.2', 
                     'version': '1.0.0']

       if (fileExists("${helmProjectName}/Chart.yaml")) {
              echo "Le fichier ${helmProjectName}/Chart.yaml existe, à supprimer"
              sh ("rm -f ${helmProjectName}/Chart.yaml")
       } else {
              echo "Le fichier ${helmProjectName}/Chart.yaml n'existe pas, à créer"
       }
       writeYaml file: "${helmProjectName}/Chart.yaml", data: cmap

       // Initialisation des variables commande
       helmProjectNameTiret = helmProjectName.replaceAll("-","_")
       helmTemplate = "~/helm template ${helmProjectName} " + 
              "--set ${helmProjectName}.image.repository=${dockerRegistryRepoAppli} " + 
              "--set ${helmProjectName}.environment=${codeEnv} " +
              "--set ${helmProjectName}.name=${helmProjectNameTiret} " +
              "--set serviceAccountName=\"sifront\" " +
              "--set secretName=${nomEnv}-mycloud-secret " + 
              "--set ${helmProjectName}.version=latest " + 
              "> ${helmProjectName}.yaml"
       kubeConfig = "~/kubectl config set-context cluster--n ${trigrammeAppli}"
       kubeApply = "~/kubectl apply --namespace ${trigrammeAppli} -f ${helmProjectName}.yaml"

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
       // deploymentStatus = sh "kubectl rollout status ${helmProjectName}"
       // echo "log ${nomContainer} ${dockerLog}"
}
}