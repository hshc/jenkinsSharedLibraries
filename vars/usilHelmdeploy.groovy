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
       // kubeRbac = "~/kubectl apply -f ${helmServiceName}/template/rbac-config.yaml"
       helmTemplate = "~/helm template ${helmServiceName} " + 
              "--set ${helmServiceName}.image.repository=${dockerRegistryRepoAppli} " + 
              "--set ${helmServiceName}.environment=${codeEnv} " +
              "--set ${helmServiceName}.name=${kubServiceName} " +
              "--set serviceAccountName=${trigrammeAppli}-service-account " + 
              "--set trigrammeAppli=${trigrammeAppli} " +
              "--set secretName=${nomEnv}-mycloud-secret " + 
              "--set ${helmServiceName}.version=latest " + 
              "> ${helmServiceName}.yaml"
       kubeConfigUse = "~/kubectl config use-context cluster-anteprod-${trigrammeAppli} --namespace ${trigrammeAppli}"

       KubeVerifContext= "~/kubectl config get-contexts"

       kubeApply = "~/kubectl apply --namespace ${trigrammeAppli} -f ${helmServiceName}.yaml"

       // Lancement des commandes
       // logExec("kuberbac", kubeRbac)

       logExec("helmTemplate", helmTemplate)

       logExec("kubeConfigUse", kubeConfigUse)

       logExec("KubeVerifContext", KubeVerifContext)

       logExec("kubeApply", kubeApply)

       // podLog = sh (script : "kubectl logs -l app=${nomContainer} --tail 1", returnStdout: true)
       // deploymentStatus = sh "kubectl rollout status ${helmServiceName}"
       // echo "log ${nomContainer} ${dockerLog}"
}
}
def logExec(def name, def commande) {
    usilColorLog("info", "${name} commande: ${commande}")
    sh ("${commande}")
}