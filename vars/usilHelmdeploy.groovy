def call(def codeEnv,def dockerRegistryRepoAppli,def trigrammeAppli,def helmServiceName, def kubServiceName) {

stage("Déploiement kube: ${helmServiceName} environnement: ${codeEnv}"){
       // récupération de la conf dédié à l'environnement Kube
       switch("${codeEnv}") { 
       case "e2": 
              nomEnv="rect"; break;
       default:
              nomEnv="int"
       } 

       // Génération du chart.yaml
       def cmap = ['apiVersion': 'v2',
                     'name': helmServiceName,
                     'type': 'application',
                     'appVersion': '1.0.2', 
                     'version': '1.0.0']

       if (fileExists("${helmServiceName}/Chart.yaml")) {
              echo "Le fichier ${helmServiceName}/Chart.yaml existe, à supprimer"
              sh ("rm -f ${helmServiceName}/Chart.yaml")
       } else {
              echo "Le fichier ${helmServiceName}/Chart.yaml n'existe pas, à créer"
       }
       writeYaml file: "${helmServiceName}/Chart.yaml", data: cmap

       // Initialisation des variables commande

       helmTemplate = "~/helm template ${helmServiceName} " + 
              "--set ${helmServiceName}.image.repository=${dockerRegistryRepoAppli} " + 
              "--set ${helmServiceName}.environment=${codeEnv} " +
              "--set ${helmServiceName}.name=${kubServiceName} " +
              "--set serviceAccountName=\"sifront\" " +
              "--set secretName=${nomEnv}-mycloud-secret " + 
              "--set ${helmServiceName}.version=latest " + 
              "> ${helmServiceName}.yaml"
       // kubeConfig = "~/kubectl config set-context cluster--n ${trigrammeAppli}"
       kubeConfig = "~/kubectl config set-context cluster--n pca"
       kubeApply = "~/kubectl apply --namespace ${trigrammeAppli} -f ${helmServiceName}.yaml"

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
       // deploymentStatus = sh "kubectl rollout status ${helmServiceName}"
       // echo "log ${nomContainer} ${dockerLog}"
}
}
