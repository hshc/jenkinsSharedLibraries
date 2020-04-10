def call(def trigrammeAppli,def helmServiceName, def kubServiceName) {

stage("Initialisation d'un projet kube: ${trigrammeAppli} "){

       // Initialisation des variables commande
       kubeCreateNS = "kubectl create namespace ${trigrammeAppli}"
       kubeConfigSet = "~/kubectl config set-context cluster-anteprod-${trigrammeAppli} " +
                        "--cluster=cluster-anteprod --user=sifront-tbg --namespace=${trigrammeAppli}"
       kubeConfigView = "~/kubectl config view"

       // Lancement des commandes
       usilColorLog("info", "kubeCreateNS commande: ${kubeCreateNS}")
       sh ("${kubeCreateNS}")

       usilColorLog("info", "kubeConfigSet commande: ${kubeConfigSet}")
       sh ("${kubeConfigSet}")
        
       usilColorLog("info", "kubeConfigView commande: ${kubeConfigView}")
       sh ("${kubeConfigView}")

}
}
def logExec(def commande) {
    usilColorLog("info", commande.getName() + " commande: ${commande}")
    sh ("${commande}")
}