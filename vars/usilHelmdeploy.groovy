def call(def codeEnv,def dockerRegistryRepoAppli,def trigrammeAppli,def helmServiceName, def kubServiceName) {

stage("Déploiement kube: ${kubServiceName} environnement: ${codeEnv}"){
       // récupération de la conf dédié à l'environnement Kube
       switch("${codeEnv}") { 
       case "e2": 
              nomEnv="rect"; break;
       default:
              nomEnv="int"
       } 

       // Génération du chart.yaml
       def cmap = ['apiVersion': 'v2',
                     'name': kubServiceName,
                     'type': 'application',
                     'appVersion': '1.0.2', 
                     'version': '1.0.0']

       if (fileExists("${kubServiceName}/Chart.yaml")) {
              echo "Le fichier ${kubServiceName}/Chart.yaml existe, à supprimer"
              sh ("rm -f ${kubServiceName}/Chart.yaml")
       } else {
              echo "Le fichier ${kubServiceName}/Chart.yaml n'existe pas, à créer"
       }
       writeYaml file: "${kubServiceName}/Chart.yaml", data: cmap

       // Initialisation des variables commande
       helmTemplate = "~/helm template ${kubServiceName} " + 
              "--set ${helmServiceName}.image.repository=${dockerRegistryRepoAppli} " + 
              "--set ${helmServiceName}.environment=${codeEnv} " +
              "--set ${helmServiceName}.name=${kubServiceName} " +
              "--set serviceAccountName=${trigrammeAppli}-service-account " + 
              "--set trigrammeAppli=${trigrammeAppli} " +
              "--set secretName=${nomEnv}-mycloud-secret " + 
              "--set ${helmServiceName}.version=latest " + 
              "> ${kubServiceName}.yaml"
             
       kubeConfigUse = "~/kubectl config use-context cluster-anteprod-${trigrammeAppli}"

       KubeVerifContext= "~/kubectl config get-contexts"

       //kubeApply = "~/kubectl apply --namespace ${trigrammeAppli} -f ${kubServiceName}.yaml"
       //helmInstall = "~/helm install --skip-crds ${kubServiceName} ${kubServiceName} " + 
       helmInstall = "~/helm upgrade --install ${kubServiceName} ${kubServiceName} " + 
              "--set ${helmServiceName}.image.repository=${dockerRegistryRepoAppli} " + 
              "--set ${helmServiceName}.environment=${codeEnv} " +
              "--set ${helmServiceName}.name=${kubServiceName} " +
              "--set serviceAccountName=${trigrammeAppli}-service-account " + 
              "--set trigrammeAppli=${trigrammeAppli} " +
              "--set secretName=${nomEnv}-mycloud-secret " + 
              "--set ${helmServiceName}.version=latest "

       // Lancement des commandes
       logExec("helmTemplate", helmTemplate)

       logExec("kubeConfigUse", kubeConfigUse)

       logExec("KubeVerifContext", KubeVerifContext)

       // logExec("kubeApply", kubeApply)
       logExec("helmInstall", helmInstall)
        sleep(time:10,unit:"SECONDS")
       
       deploymentHelmStatus = sh (script : "~/helm history --max 5 ${kubServiceName}", returnStdout: true)

       deploymentKubDeployment = sh (script : "~/kubectl get deployment dyn-f7-frontend-app", returnStdout: true)
       deploymentKubStatus = sh (script : "~/kubectl get deployment dyn-f7-frontend-app -o=jsonpath={.status}", returnStdout: true)

       podLog = sh (script : "~/kubectl logs -l app=${kubServiceName}", returnStdout: true)

       // deploymentStatus = sh "~/kubectl rollout status deployment.v1.apps/${kubServiceName}"
       // usilColorLog("info", "Ci dessous la log du pod ${kubServiceName} déployé:")

       usilColorLog("log", "${deploymentHelmStatus}")
       usilColorLog("log", "${deploymentKubDeployment}")
       usilColorLog("log", "${deploymentKubStatus}")
       usilColorLog("log", "${podLog}")
}
}
def logExec(def name, def commande) {
    usilColorLog("info", "${name} commande: ${commande}")
    sh ("${commande}")
}