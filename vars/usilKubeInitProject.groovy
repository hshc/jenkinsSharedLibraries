def call(def trigrammeAppli) {

stage("Initialisation d'un projet kube: ${trigrammeAppli} "){

    // Initialisation des variables commande
    kubeCreateNS = "~/kubectl create namespace ${trigrammeAppli}"
    kubeCreateSAccount = "~/kubectl apply -f serviceAccount.yaml"
    kubeCreateRBinding = "~/kubectl apply -f roleBinding.yaml"
    kubeConfigSet = "~/kubectl config set-context cluster-anteprod-${trigrammeAppli} " +
                    "--cluster=cluster-anteprod --user=sifront-tbg --namespace=${trigrammeAppli}"
    kubeConfigView = "~/kubectl config view"

    // Lancement des commandes
    logExec("kubeCreateNS", kubeCreateNS)
    serviceAccount(trigrammeAppli)
    logExec("kubeCreateSAccount", kubeCreateSAccount)
    roleBinding(trigrammeAppli)
    logExec("kubeCreateRBinding", kubeCreateRBinding)
    logExec("kubeConfigSet", kubeConfigSet)
    logExec("kubeConfigView", kubeConfigView)

}
}
def logExec(def name, def commande) {
    usilColorLog("info", "${name} commande: ${commande}")
    //sh ("${commande}")
}

def serviceAccount (def trigrammeAppli) {

    def configYaml = '''\
    ---
    apiVersion: v1
    kind: ServiceAccount
    metadata:
      name: trigrammeAppli
      namespace: trigrammeAppli
      selfLink: /api/v1/namespaces/api/serviceaccounts/sifront
    secrets:
    - name: sifront-token-mxb4l
    '''

    if (fileExists("serviceAccount.yaml")) {
        echo "Le fichier serviceAccount.yaml existe, à supprimer"
        sh ("rm -f serviceAccount.yaml")
    } else {
        echo "Le fichier serviceAccount.yaml n'existe pas, à créer"
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    echo "${configYamlTRI}"
    //def yamlFile = new File("serviceAccount.yaml")
    //yamlFile.write(configYamlTRI)

}

def roleBinding (def trigrammeAppli) {
    // Génération du roleBinding.yaml
    def configYaml = '''\
    ---
    apiVersion: rbac.authorization.k8s.io/v1
    kind: RoleBinding
    metadata:
        name: api-trigrammeAppli-rolebinding
        namespace: trigrammeAppli
        selfLink: /apis/rbac.authorization.k8s.io/v1/namespaces/api/rolebindings/api-trigrammeAppli-rolebinding
    roleRef:
        apiGroup: rbac.authorization.k8s.io
        kind: Role
        name: mh-trigrammeAppli-role
    subjects:
    - kind: ServiceAccount
        name: trigrammeAppli
        namespace: trigrammeAppli
    '''

    if (fileExists("roleBinding.yaml")) {
        echo "Le fichier servicroleBindingeAccount.yaml existe, à supprimer"
        sh ("rm -f roleBinding.yaml")
    } else {
        echo "Le fichier roleBinding.yaml n'existe pas, à créer"
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    echo "${configYamlTRI}"
    //def yamlFile = new File("roleBinding.yaml")
    //yamlFile.write(configYamlTRI)
}