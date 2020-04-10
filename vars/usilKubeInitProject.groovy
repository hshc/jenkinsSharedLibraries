def call(def trigrammeAppli,def helmServiceName, def kubServiceName) {

stage("Initialisation d'un projet kube: ${trigrammeAppli} "){

    // Initialisation des variables commande
    kubeCreateNS = "kubectl create namespace ${trigrammeAppli}"
    kubeConfigSet = "~/kubectl config set-context cluster-anteprod-${trigrammeAppli} " +
                    "--cluster=cluster-anteprod --user=sifront-tbg --namespace=${trigrammeAppli}"
    kubeConfigView = "~/kubectl config view"

    // Lancement des commandes
    logExec("kubeCreateNS", kubeCreateNS)

    logExec("kubeConfigSet", kubeConfigSet)

    logExec("kubeConfigView", kubeConfigView)

}
}
def logExec(def name, def commande) {
    usilColorLog("info", "${name} commande: ${commande}")
    sh ("${commande}")
}