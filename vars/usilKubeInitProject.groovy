def call(def trigrammeAppli) {

stage("Initialisation d'un projet kube: ${trigrammeAppli} "){
    usilColorLog("stage", "Initialisation d'un projet kube: ${trigrammeAppli}")
    // Initialisation des variables commande
    kubeCreateNS = "~/kubectl create namespace ${trigrammeAppli}"
    kubeCreateSAccount = "~/kubectl apply -f serviceAccount.yaml"
    kubeCreateRBinding = "~/kubectl apply -f roleBinding.yaml"
    kubeCreateRole = "~/kubectl apply -f role.yaml"
    kubeConfigSet = "~/kubectl config set-context cluster-anteprod-${trigrammeAppli} " +
                    "--cluster=cluster-anteprod --user=sifront-api --namespace=${trigrammeAppli}"
    kubeConfigView = "~/kubectl config view"

    // Lancement des commandes
    logExec("kubeCreateNS", kubeCreateNS)
    serviceAccount(trigrammeAppli)
    logExec("kubeCreateSAccount", kubeCreateSAccount)
    roleBinding(trigrammeAppli)
    logExec("kubeCreateRBinding", kubeCreateRBinding)
    role(trigrammeAppli)
    logExec("kubeCreateRole", kubeCreateRole)
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
      name: trigrammeAppli-service-account
    '''

    if (fileExists("serviceAccount.yaml")) {
        usilColorLog("debug", "Le fichier serviceAccount.yaml existe, à supprimer")
        sh ("rm -f serviceAccount.yaml")
    } else {
        usilColorLog("debug", "Le fichier serviceAccount.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
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
      name: trigrammeAppli-psp-rolebinding
      namespace: trigrammeAppli
    roleRef:
      apiGroup: rbac.authorization.k8s.io
      kind: Role
      name: mh-psp-role
    subjects:
    - kind: ServiceAccount
      name: trigrammeAppli-service-account
      namespace: trigrammeAppli
    '''

    if (fileExists("roleBinding.yaml")) {
        usilColorLog("debug",  "Le fichier roleBinding.yaml existe, à supprimer")
        sh ("rm -f roleBinding.yaml")
    } else {
        usilColorLog("debug", "Le fichier roleBinding.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
    //def yamlFile = new File("roleBinding.yaml")
    //yamlFile.write(configYamlTRI)
}

def role (def trigrammeAppli) {
    // Génération du role.yaml
    def configYaml = '''\
    ---
    apiVersion: rbac.authorization.k8s.io/v1
    kind: Role
    metadata:
      name: mh-psp-role
      namespace: trigrammeAppli
    rules:
    - apiGroups:
      - policy
      resourceNames:
      - mh-restricted
      resources:
      - podsecuritypolicies
      verbs:
      - use
    '''

    if (fileExists("role.yaml")) {
        usilColorLog("debug", "Le fichier role.yaml existe, à supprimer")
        sh ("rm -f role.yaml")
    } else {
        usilColorLog("debug", "Le fichier role.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
    //def yamlFile = new File("role.yaml")
    //yamlFile.write(configYamlTRI)
}