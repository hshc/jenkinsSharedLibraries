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
    serviceAccount1(trigrammeAppli)
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
// def serviceAccount (def trigrammeAppli) {
//     // Génération du serviceAccount.yaml
//        def cmap = [apiVersion 'v1'
//                     kind 'ServiceAccount', 
//                     metadata {
//                         name trigrammeAppli+'-service-account'
//                         namespace trigrammeAppli
//                         selfLink '/api/v1/namespaces/api/serviceaccounts/sifront'
//                     }, 
//                     secrets 'name' 'sifront-token-mxb4l'
//                 ]

//        if (fileExists("serviceAccount.yaml")) {
//               echo "Le fichier serviceAccount.yaml existe, à supprimer"
//               sh ("rm -f serviceAccount.yaml")
//        } else {
//               echo "Le fichier serviceAccount.yaml n'existe pas, à créer"
//        }
//        writeYaml file: "serviceAccount.yaml", data: cmap

// }
def roleBinding (def trigrammeAppli) {
    // Génération du roleBinding.yaml
    //    def cmap = [apiVersion 'rbac.authorization.k8s.io/v1'
    //                 name trigrammeAppli+'-psp-rolebinding'
    //                 kind 'RoleBinding' 
    //                 metadata {
    //                     name trigrammeAppli+'-psp-rolebinding'
    //                     namespace trigrammeAppli
    //                     selfLink '/apis/rbac.authorization.k8s.io/v1/namespaces/api/rolebindings/api-psp-rolebinding'
    //                 }
    //                 roleRef {
    //                     name 'api-psp-rolebinding', 
    //                     namespace trigrammeAppli, 
    //                     name 'mh-psp-role'
    //                 }
    //                 subjects(['ServiceAccount']) { sub ->
    //                     kind sub
    //                     name 'sifront',
    //                     namespace trigrammeAppli
    //                 }
    //             ]

    //    if (fileExists("roleBinding.yaml")) {
    //           echo "Le fichier roleBinding.yaml existe, à supprimer"
    //           sh ("rm -f roleBinding.yaml")
    //    } else {
    //           echo "Le fichier roleBinding.yaml n'existe pas, à créer"
    //    }
    //    writeYaml file: "roleBinding.yaml", data: cmap

}

// config {
//     application 'Sample App'
//     version '1.0.1'
//     autoStart true
//     // We can nest YAML content.
//     database {
//         url 'jdbc:db//localhost'   
//     }
//     // We can use varargs arguments that will
//     // turn into a list.
//     // We could also use a Collection argument.
//     services 'ws1', 'ws2'
//     // We can even apply a closure to each
//     // collection element.
//     environments(['dev', 'acc']) { env ->
//         name env.toUpperCase()
//         active true
//     }
// }
// ---
// application: "Sample App"
// version: "1.0.1"
// autoStart: true
// database:
//   url: "jdbc:db//localhost"
// services:
// - "ws1"
// - "ws2"
// environments:
// - name: "DEV"
//   active: true
// - name: "ACC"
//   active: true

def serviceAccount1 (String trigrammeAppli) {

    String configYaml = '''\
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
    def yamlFile = new File("serviceAccount.yaml")
    yamlFile.write(configYaml.replaceall("trigrammeAppli",trigrammeAppli))

}