def call(def codeEnv,def dockerRegistryRepoAppli,def trigrammeAppli,def helmServiceName, def kubServiceName, def tempsAtteDepl) {

stage("Déploiement kube: ${kubServiceName} env: ${codeEnv}"){
       usilColorLog("stage", "Déploiement kube: ${kubServiceName} env: ${codeEnv}")
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
       if (!tempsAtteDepl.toString().isNumber()) { 
              usilColorLog("warning", "Le temps d'attente en entrée de la méthode n'est pas numérique: < ${tempsAtteDepl} >")
              tempsAtteDepl=15
        }
       sleep(time:tempsAtteDepl,unit:"SECONDS")
       
       deploymentHelmStatus = sh (script : "~/helm history --max 5 ${kubServiceName}", returnStdout: true)
       deploymentKubDeployment = sh (script : "~/kubectl get deployment ${kubServiceName}", returnStdout: true)
       deploymentKubStatus = sh (script : "~/kubectl get deployment ${kubServiceName} -o=jsonpath={.status}", returnStdout: true)
       podLog = sh (script : "~/kubectl logs -l app=${kubServiceName}", returnStdout: true)
       //deploymentHetlmTest = sh (script : "~/helm test ${kubServiceName}", returnStdout: true)

       usilColorLog("log", "${deploymentHelmStatus}")
       usilColorLog("log", "${deploymentKubDeployment}")
       usilColorLog("log", "${deploymentKubStatus}")
       usilColorLog("log", "${podLog}")
       //usilColorLog("log", "${deploymentHetlmTest}")
       if (deploymentKubDeployment.contains("${kubServiceName}   0/1")) {
              usilColorLog("error", "le déploiement a rencontré des problèmes")
              currentBuild.result = 'FAILURE';
              sh "exit 1"
              return '404';
      } else {
             usilColorLog("success", "le déploiement est ok")
             currentBuild.result = 'SUCCESS';
             sh "exit 0"
      }
}
}
def logExec(def name, def commande) {
    usilColorLog("info", "${name} commande: ${commande}")
    sh ("${commande}")
}