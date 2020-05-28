def call(def trigrammeAppli) {

stage("Initialisation d'un projet kube: ${trigrammeAppli} "){
    usilColorLog("stage", "Initialisation d'un projet kube: ${trigrammeAppli}")
    // Initialisation des variables commande
    kubeCreateNS = "~/kubectl create namespace ${trigrammeAppli}"
    kubeCreateSecretIntg = "~/kubectl apply -f secret-intg.yaml"
    kubeCreateSecretRecf = "~/kubectl apply -f secret-recf.yaml"
    kubeCreateSAccount = "~/kubectl apply -f serviceAccount.yaml"
    kubeCreateRBinding = "~/kubectl apply -f roleBinding.yaml"
    kubeCreateRole = "~/kubectl apply -f role.yaml"
    kubeConfigSet = "~/kubectl config set-context cluster-anteprod-${trigrammeAppli} " +
                    "--cluster=cluster-anteprod --user=sifront-api --namespace=${trigrammeAppli}"
    kubeConfigView = "~/kubectl config view"

    // Lancement des commandes
    //logExec("kubeCreateNS", kubeCreateNS)
    serviceAccount(trigrammeAppli)
    logExec("kubeCreateSAccount", kubeCreateSAccount)
    secretIntg(trigrammeAppli)
    logExec("kubeCreateSecretIntg", kubeCreateSecretIntg)
    secretRecf(trigrammeAppli)
    logExec("kubeCreateSecretRecf", kubeCreateSecretRecf)
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
    sh ("${commande}")
}

def serviceAccount (def trigrammeAppli) {
 
    def configYaml = '''
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: trigrammeAppli-service-account
    '''

    if (fileExists("init/serviceAccount.yaml")) {
        usilColorLog("debug", "Le fichier init/serviceAccount.yaml existe, à supprimer")
        sh ("rm -f init/serviceAccount.yaml")
    } else {
        usilColorLog("debug", "Le fichier init/serviceAccount.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
    def yamlFile = new File("init/serviceAccount.yaml")
    yamlFile.write(configYamlTRI)
    writeFile file: "serviceAccount.yaml", text: configYamlTRI
}

def roleBinding (def trigrammeAppli) {
    // Génération du roleBinding.yaml
    def configYaml = '''
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
  namespace: trigrammeAppli'''

    if (fileExists("init/roleBinding.yaml")) {
        usilColorLog("debug",  "Le fichier roleBinding.yaml existe, à supprimer")
        sh ("rm -f init/roleBinding.yaml")
    } else {
        usilColorLog("debug", "Le fichier roleBinding.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
    writeFile file: "roleBinding.yaml", text: configYamlTRI
}

def role (def trigrammeAppli) {
    // Génération du role.yaml
    def configYaml = '''
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
  - use'''

    if (fileExists("init/role.yaml")) {
        usilColorLog("debug", "Le fichier init/role.yaml existe, à supprimer")
        sh ("rm -f init/role.yaml")
    } else {
        usilColorLog("debug", "Le fichier init/role.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
    writeFile file: "role.yaml", text: configYamlTRI
}

def secretIntg (def trigrammeAppli) {
    // Génération du secret-intg.yaml
    def configYaml = '''
---
apiVersion: v1
data:
  tls.crt: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlIcURDQ0JaQ2dBd0lCQWdJVEVRQUJQSnB4YjNQY0trMm9jd0FBQUFFOG1qQU5CZ2txaGtpRzl3MEJBUXNGDQpBREJmTVJNd0VRWUtDWkltaVpQeUxHUUJHUllEZEdWak1SUXdFZ1lLQ1pJbWlaUHlMR1FCR1JZRWMya3liVEVTDQpNQkFHQ2dtU0pvbVQ4aXhrQVJrV0FtRmtNUjR3SEFZRFZRUURFeFZUU1RKTklFRkRJRWx1ZEdWeWJXVmthV0ZwDQpjbVV3SGhjTk1qQXdNekUzTVRRME1EQXhXaGNOTWpJd016RTNNVFEwTURBeFdqQm9NUXN3Q1FZRFZRUUdFd0pHDQpVakVPTUF3R0ExVUVDQk1GVUVGU1NWTXhEakFNQmdOVkJBY1RCVkJCVWtsVE1Rc3dDUVlEVlFRS0V3Sk5TREVMDQpNQWtHQTFVRUN4TUNUVWd4SHpBZEJnTlZCQU1NRmlvdWFXNTBMbTE1WTJ4dmRXUXVjMmt5YlM1MFpXTXdnZ0VoDQpNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0SUJEZ0F3Z2dFSkFvSUJBUURIbEdLWlB1M0dNZjNYbnl4dnpOVTNreWtaDQpsa0ZQOUZra04xaElWWmhnYXNhUUNtTlEzSWhtaytKYW5QeHZSL3JqdzNxOGFUVURhVGxzNjhCREN0UVVIbmgvDQpZN1lVMGdBUUVnT1NmR29yMm1FamdzOHB1M2dOMDJUbXVDaWFrV0tMWXBVMUMyblJmMElWM1QxYXNlbENFQWIrDQpZYkxyN0JFM3FzeEZ2SGlvWmZYb0ViN2xkWG9CRndNc2JYV3ZyRnhzNGhKTnI2NGpoSm1xZUZQTk53NytCdmFWDQp0R0luVlpvS01Dd2pzRXFFSnVDdnM0aU1mbCtiN2NTclNqMGFScFhqeUljbGt0cjdnVGJhYm95RS9PTW5acFAvDQpIQklUUUdWMEZYZG9jVi9selRPQmlFME5MWHNJUXlReDQvQm41eFc5NkZHT3pHZDVENndjM3RoU2U4NjFBZ0lRDQpBYU9DQTFNd2dnTlBNQXNHQTFVZER3UUVBd0lGb0RBVEJnTlZIU1VFRERBS0JnZ3JCZ0VGQlFjREFUQWhCZ05WDQpIUkVFR2pBWWdoWXFMbWx1ZEM1dGVXTnNiM1ZrTG5OcE1tMHVkR1ZqTUIwR0ExVWREZ1FXQkJRNDNUZEF1dHdTDQo3aXVsNkY5ZTJQUytxR0hZUURBZkJnTlZIU01FR0RBV2dCUUxZWTl1MlJyVzZHVVVWd1M0enJ5TDZ5UWZEVENDDQpBU29HQTFVZEh3U0NBU0V3Z2dFZE1JSUJHYUNDQVJXZ2dnRVJob0hHYkdSaGNEb3ZMeTlEVGoxVFNUSk5KVEl3DQpRVU1sTWpCSmJuUmxjbTFsWkdsaGFYSmxMRU5PUFZaWFF6QkJVRkF3TVRNc1EwNDlRMFJRTEVOT1BWQjFZbXhwDQpZeVV5TUV0bGVTVXlNRk5sY25acFkyVnpMRU5PUFZObGNuWnBZMlZ6TEVOT1BVTnZibVpwWjNWeVlYUnBiMjRzDQpSRU05WVdRc1JFTTljMmt5YlN4RVF6MTBaV00vWTJWeWRHbG1hV05oZEdWU1pYWnZZMkYwYVc5dVRHbHpkRDlpDQpZWE5sUDI5aWFtVmpkRU5zWVhOelBXTlNURVJwYzNSeWFXSjFkR2x2YmxCdmFXNTBoa1pvZEhSd09pOHZVR3RwDQpTVzUwVUhKdlpDNWhaQzV6YVRKdExuUmxZeTlEWlhKMFJXNXliMnhzTDFOSk1rMGxNakJCUXlVeU1FbHVkR1Z5DQpiV1ZrYVdGcGNtVXVZM0pzTUlJQk93WUlLd1lCQlFVSEFRRUVnZ0V0TUlJQktUQ0J1d1lJS3dZQkJRVUhNQUtHDQpnYTVzWkdGd09pOHZMME5PUFZOSk1rMGxNakJCUXlVeU1FbHVkR1Z5YldWa2FXRnBjbVVzUTA0OVFVbEJMRU5PDQpQVkIxWW14cFl5VXlNRXRsZVNVeU1GTmxjblpwWTJWekxFTk9QVk5sY25acFkyVnpMRU5PUFVOdmJtWnBaM1Z5DQpZWFJwYjI0c1JFTTlZV1FzUkVNOWMya3liU3hFUXoxMFpXTS9ZMEZEWlhKMGFXWnBZMkYwWlQ5aVlYTmxQMjlpDQphbVZqZEVOc1lYTnpQV05sY25ScFptbGpZWFJwYjI1QmRYUm9iM0pwZEhrd2FRWUlLd1lCQlFVSE1BS0dYV2gwDQpkSEE2THk5UWEybEpiblJRY205a0xtRmtMbk5wTW0wdWRHVmpMME5sY25SRmJuSnZiR3d2VmxkRE1FRlFVREF4DQpNeTVoWkM1emFUSnRMblJsWTE5VFNUSk5KVEl3UVVNbE1qQkpiblJsY20xbFpHbGhhWEpsTG1OeWREQStCZ2tyDQpCZ0VFQVlJM0ZRY0VNVEF2QmljckJnRUVBWUkzRlFpQmdxUXpnZERGYllPMWlRNkhrS1lNZzVpWVBZRVNndGl1DQpWNEdtZzM4Q0FXUUNBUWN3R3dZSkt3WUJCQUdDTnhVS0JBNHdEREFLQmdnckJnRUZCUWNEQVRBTkJna3Foa2lHDQo5dzBCQVFzRkFBT0NBZ0VBRWZvdFF2dkR0ZDlHc2Y2c1l5YzNuNXhMd0NMelA4d2QzdGVFak02b3FZMXcwKzBDDQppRytBRWFXQytXOXJTK3BiMTdBNytsdjRGVjBEYWM5VEZzVndROUt2dkp2MiswVzgvblhNYlI2RGREY096MVdyDQp4YVg1R0EzbXoralJwWFljTEw3bzRLdXRyeHFINFd3QjJzK2VxY3JmRXMzV1BvU1d2VTh4NVhzZiswS29QdFVsDQpTV25VYmJ1bDBaQTJiOVE3bUNpVy9QWUNGSklkdzBtZHlGRnFvSXkwOE5FdFBxQWRxbk5FTkQxVERINWRrZFp1DQo0WE9YenRONU5vZ1psRzFVRlBCc2pvY09RMURnTy9lQkU4eVlRNjduVDh0Q0NKcHBIbEFoVVRVSUFVZlljb0tlDQpXMEwvbWN2ckZ2RjdOZS9qUTNBaDBNYmdpTHZncFRGaWQ3emtJMng5QlEzTDc4ai9NS0kwQmFuam5qeEcrR3pJDQowV2RDWkpwc1ZBQ2pUN2VML2pvcTZjYk9HUmpMdHdRaTRBTmlVV3FKekF0OGF5Nk1yR05aL0RGUmRzMmJxc3dTDQo4ZEVlamR4bmVFWW1qQVNneDNrMnFIOHFYcTFrckJrdHB3V2JqSy9sRm1PeFRmNkVPeTZFRTJZc3R4S2FRa2hPDQpYTlZtV253OE1Wb2g4SEdJZFhsVEVkSlpKZ1JRTHplNUFsQ1J2ekdFRnlNMktvOFdkSUNWU1JhMk9SQnRSeFkwDQorOXp0RzBVWmFmelhyTlJHNGpJV3FPcU9rUStGN2E4Yk04NGp6QmNzN1dtM0ZoSGxQNVhJOFlGZjF4aGI5OUlVDQpkN01ML3g4dHhETVQ0TmUwV05FdTd5Si8xNllHbTFGL3FJS0hEQWtPSnd4N1RHeVRjZWtlbWNnL1NnWT0NCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0NCi0tLS0tQkVHSU4gQ0VSVElGSUNBVEUtLS0tLQ0KTUlJR3NqQ0NCSnFnQXdJQkFnSVRIZ0FBQUFLY1Rhako3TTBHRVFBQUFBQUFBakFOQmdrcWhraUc5dzBCQVFzRg0KQURBWE1SVXdFd1lEVlFRREV3eFRTVEpOSUVGRElGSlBUMVF3SGhjTk1UY3dOREU0TVRVeU5qVTJXaGNOTWpJdw0KTkRFNE1UVXpOalUyV2pCZk1STXdFUVlLQ1pJbWlaUHlMR1FCR1JZRGRHVmpNUlF3RWdZS0NaSW1pWlB5TEdRQg0KR1JZRWMya3liVEVTTUJBR0NnbVNKb21UOGl4a0FSa1dBbUZrTVI0d0hBWURWUVFERXhWVFNUSk5JRUZESUVsdQ0KZEdWeWJXVmthV0ZwY21Vd2dnSWlNQTBHQ1NxR1NJYjNEUUVCQVFVQUE0SUNEd0F3Z2dJS0FvSUNBUUN2OFM0Lw0KcUVlZXVSTXZrZHBvSTJlazlVTGh5L0xiTnRmMmVHeEl3bkE4YytJRWxkMldmTzVOUUNmcWxlT1A3UjUrRkdTZA0KNkFZbkNybUlFSGEyQXliUFIvWjNmU290WDJqcHlnc2gyMys1MnhvcnpscXlWNTRtK1dwUnh6emZkUitjcDFiRg0KK1NtTXJpSU5SdWJMYlhRWGJSelduaWRmVmdvTnlBQ2FFNG91b3h2dGEvajhtSWdsYzVRb3ZPRHN2ZjYvVGtkcA0KQmFtZG02MWhFNXplV21Bb0RiNk5HUEpNanpRMnA4L3hsZWZ6b1Z2T0VMb0FpNG9RMXMrWFJoMkx3RUJLVnFzUQ0KcFBPWTlDZzlmZDJPU1Q5VEEwVWVSUVpodCtjY2prTXBwbUY3aTREdjdmcXcrVnhCOG1QZHNOOHBYbFI0d01Kcg0Kek4vQ1J2Rjk4SzVNTzZTVHlGZURDS281NkppbzRYVjRuUkRuVGh2ZHdNM3JkQkZRQjVJZTZnZVBBVUpISVBVTg0KZ3FYVmd3RnFKN0svczU1Z3I4SnJ5aTF3MXl0ZGhucHl1a21DYlQ3cU41YmptU0plM2NtYjNpdW5oRmRZbGNDNg0KajAwRmJESE9yQlJKOWJmYU1sd1UrcnAvM1hxbVk1amN5TjA3bVQ5NVZkaGZlenBzMG1DUld4R0pEdmZGblhFMQ0KVVlibDlOR1VtcFhSMlQ0TnFwbWtiRzdKUktheWw3SURuTzNDSmJxSkdvU3NLZGhZcDcyWE9vN0hrUDdFZk9tNA0Kdy84c0ZhR1ZYSnl4cW9yeEVhY3hkdXhLdzZpaGk0dlNwL2hySGEyOENrVEorTmc2Nkg1K0R6SnNqMHJndHJjVA0KWEJ1M2hhTFo4a2VKZDZYVk4rNk9tVHoxalIzVXBNa0wxZ2RUK1FJREFRQUJvNElCclRDQ0Fha3dFQVlKS3dZQg0KQkFHQ054VUJCQU1DQVFBd0hRWURWUjBPQkJZRUZBdGhqMjdaR3Rib1pSUlhCTGpPdkl2ckpCOE5NQmtHQ1NzRw0KQVFRQmdqY1VBZ1FNSGdvQVV3QjFBR0lBUXdCQk1Bc0dBMVVkRHdRRUF3SUJoakFQQmdOVkhSTUJBZjhFQlRBRA0KQVFIL01COEdBMVVkSXdRWU1CYUFGT1p4cGgwRG1Db09IZmJaU1JGTllhdFpJSUJTTUU0R0ExVWRId1JITUVVdw0KUTZCQm9EK0dQV2gwZEhBNkx5OVFhMmxKYm5SUWNtOWtMbUZrTG5OcE1tMHVkR1ZqTDBObGNuUkZibkp2Ykd3dg0KVTBreVRTVXlNRUZESlRJd1VrOVBWQzVqY213d2djc0dDQ3NHQVFVRkJ3RUJCSUcrTUlHN01JRzRCZ2dyQmdFRg0KQlFjd0FvYUJxMnhrWVhBNkx5OHZRMDQ5VTBreVRTVXlNRUZESlRJd1VrOVBWQ3hEVGoxQlNVRXNRMDQ5VUhWaQ0KYkdsakpUSXdTMlY1SlRJd1UyVnlkbWxqWlhNc1EwNDlVMlZ5ZG1salpYTXNRMDQ5UTI5dVptbG5kWEpoZEdsdg0KYml4RVF6MXRZV3hoYTI5bVppMXRaV1JsY21sakxFUkRQWFJsWXo5alFVTmxjblJwWm1sallYUmxQMkpoYzJVLw0KYjJKcVpXTjBRMnhoYzNNOVkyVnlkR2xtYVdOaGRHbHZia0YxZEdodmNtbDBlVEFOQmdrcWhraUc5dzBCQVFzRg0KQUFPQ0FnRUFLbU41djhQM2tBQm1RVC9YKy9xaitHUnQveCtiUzROM3ZQeldQU3oyUGNqdmoyeTFhTDJlbkZWRQ0KL0FPcUoxVExtVlhXbUxyZ2N6bHFGOC8rMmMyVkx5MVhYVXpVRmZOSE9DVGRlR2NCUXdpRDEyR0V1aWIvcThZSg0KVEQyRmg4bmRlMS9SSHpxZEFaSUFtOUhWdHBqUjlNb2RSQlNNcmhxVVZpYjdScXhtTHozT3NZRndzY0ltM0hSSA0KUEZPVHg0elhOU1NIRjF2RW9lVjJDVUU4SWVtQzlkN2xGMEViTUpQamI0Z2MyczB2aU92T21sbkRWckFtYzBvYQ0KRXpMWEtDQWx5RndyeEVEZFVhYll1aGhhNHpCbWlVdXBtVkdGRFptWGRUUXVNaEJrU2lDNUxDdldsMG8wOU1zVQ0KcWlSR0duYkZUMWFTNitiVk9hOHRDMjdCUVVOYXREVHRJaTNjQzcvMm1POFZQWSt3UzhaeEs5MWJFMVRpbWVUTg0KZmphK0RKeXFYZ1dIdWlVV0Zjb2NJRkl0aHZSa2tBdGgxRFZraUYzRHhpcE54dnYrMUgwVU01RzBoRVBGTmhmbA0Kc1BkQ0RYSDEzbGtSSEJ3K1graDVhR3VucEdkSTFma2J2S0FiN3hoNEZmd0RyMGRlcGY1WitPWUoybnZLZHhSVg0KRHZBNTlPVGwxdVBaMlk4ZU0zNkRVcUp4Njg2ZDhZOXVJSksyM1JHNzZZNkhzNU5VbUN4a2hQRXV3Mmc5R21GTw0KTmZ6WUJGWFFOUGJkLzVBRm80TXI5S2kxeGtsaWQrc3Z5Q3FCc2RUTVRESGlNeW5BVHpRWWNhOWthNEE5RTZ1Sg0KNXNtYm55ZGFJeitUOXVtUFlEcnRUSUhKQlkyMzhiL2E4MUx3bC9SdW5xOTl6YkZzOVVjPQ0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQ0KLS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlGQ1RDQ0F2R2dBd0lCQWdJUUcwa2V1dW9qV3F4TmhEVGhBSE1wbWpBTkJna3Foa2lHOXcwQkFRc0ZBREFYDQpNUlV3RXdZRFZRUURFd3hUU1RKTklFRkRJRkpQVDFRd0hoY05NVGN3TkRFNE1USTBPRFV3V2hjTk1qY3dOREU0DQpNVEkxT0RRNFdqQVhNUlV3RXdZRFZRUURFd3hUU1RKTklFRkRJRkpQVDFRd2dnSWlNQTBHQ1NxR1NJYjNEUUVCDQpBUVVBQTRJQ0R3QXdnZ0lLQW9JQ0FRRGNGN1V5bVNLeG1rNVhzQ0lwLzVoS1piMzJjZXZWREV2T3NJTjNlZVJwDQpndXlkYTNGM1NVVG93VDk4cHR0ZG00bGhPYmhRN3pSZDhyODdBOXJ6bUFtN241ZXczQXRpc2FaMXR5Wm9iS3hlDQpxT3NlMW9leS9IZUJtZmdpdzNDODYySk50WWFzcEtQbmVNWTdDNnZhT2cxWE95ZGVKd1RENHJaVjBZZHZ4MFFSDQpqbjJpcDZuR0pIRFlWdGpkdjFPajV3b1BVZXFtZGFKNWt4Ti91dmZtUkVsRlpoMlpEZUdjTFgwNXQ0cTRKcnVxDQp5aDZvem5EOFhtOTY0VEVJZ1NiUXVHMGI5ckE2Nzh0ZWl6enUzVDEwNnBtTXFRSFgyTWRieWpGaFA0eDRDZVF2DQpYaWVNNWFWVy9LU09wMWtjYUhCUG5FRDdCYWVRWTRlR2ZqQ0wzT1lpb2k1aTNLZUdGVWpENWZpbzBXazhHSnk4DQp0cUFJamg2OVF3VzVJRk5OcWh5aVBPOHJqMzFWMHF3VWJ6aTBTQ1RPN09CU3dMbTQ0TEs3ejRHKzNtR1RQSnllDQpRWmhIZnp5Ny8wSFZjR00xUWNHMGdWeGh3ZzlaZHl6dzdIZGhMUlZsN2Jqd3Mxam5MaWplWStJb2ZkSXlmSGVCDQpLV1c3U1YrZS90Ti8wSG56WjYyZDVyY0h3Mnd4SG1ETU83YXpqOVRjNU1PYzNxQzh5Q0xGZFNPU3lxa211UUFKDQpRUUxQakZLdXZ6MWlSL3FFMDFTaEtmWVp3aUdxZHR1WnliUjhRelFYVUt5eWpnMU05eVNDNlZUL3NhYWpnTFVGDQp1NTFJOGVUc2UwdGdTeFY0UXpkSjJzdGlFbDQ2SkZJV1JZQStJSlQ2Q3BURkZoa1dPS0trOU16UGF4UTJ5UXlkDQpnd0lEQVFBQm8xRXdUekFMQmdOVkhROEVCQU1DQVlZd0R3WURWUjBUQVFIL0JBVXdBd0VCL3pBZEJnTlZIUTRFDQpGZ1FVNW5HbUhRT1lLZzRkOXRsSkVVMWhxMWtnZ0ZJd0VBWUpLd1lCQkFHQ054VUJCQU1DQVFBd0RRWUpLb1pJDQpodmNOQVFFTEJRQURnZ0lCQUE3SWtDUHdFOEpsaVQwMlE4OEhYakpzYlNhVVhWdi90dkFoYkwrRjRjN0dBaFJSDQo2Snl1SVNQUjhXN3cvVDFuVGZxdElPQVRFb214SWp3WkZaVGUrRk1jMEJrVmtlbTNIbCs0Tm1PRGk0MkNnV3doDQpzVktDTlNnUFpJd1RyQkUxWUovcE9GN01oYW9rdjNENVFlY2NaNGg5ZGpIbnRsTzZ4VHpqYk0xem5DZUVXSDh0DQo4aS9lKzZxbStnMUJzRk1zWTUzWEdqc1NWek9FTmcvUndwS3k0UmpEM2p3QmZXSVF2SitnU2ZWTHcyNlhXNHRtDQpueFZrWmpGMnE5ald1dzBaQ3hVZHJDVDE5K2ZqYVQ4Q0wvdkFyZ0hWVi9MWTh5Wm5TbklaS3dGTUdnbWMra1R2DQp5ZFB2ZXZRaUFJZUthbzZhdHJZUENBUWtFZHhucnI1M1NUWkxsUWZOTnkrdmloWks3Zjg4bjc4cWpOazNaaHhLDQpkczNIYzBKVDBIUmJvNmg2Z1dCLyt3V1lJSHJuZk9XZjBmYUx6Sk0zemF5K0t6Tzl1bnpSeXArRG9KMElVbmlLDQpabjdkL2lOMjN3cm90dGpvckl0dWl3RExRa29KMGdzbGxUY0FRUkhTSW5ldUdVMURmQUtWdk16RXl3bjZyT3FUDQpIaU9QcHMxMy9EMFZhd01WVUtmUkNKd3Nlb0FiUDBvY3duWVNkSUhweE9DbUU2Y2hZb2J4enV6RFFRZlFsV1NhDQpTRHJ1Nnc3c09OMVZ2UWM4T0RtRU4veVY0bFVRWUhtczJ6anNac3FvQlBWdVlUd2FJYys4UURGWnN0QkhlZ085DQpDVUMzUGdFenA5d0xLMjE4cXMycU1NdExXVlpYeGZxakJhUHcrLzBxNCtDUk0zc043VFFZQ0ZwSjhJMjcNCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0NCg==
  tls.key: LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tCk1JSUV2Z0lCQURBTkJna3Foa2lHOXcwQkFRRUZBQVNDQktnd2dnU2tBZ0VBQW9JQkFRREhsR0taUHUzR01mM1gKbnl4dnpOVTNreWtabGtGUDlGa2tOMWhJVlpoZ2FzYVFDbU5RM0lobWsrSmFuUHh2Ui9yanczcThhVFVEYVRscwo2OEJEQ3RRVUhuaC9ZN1lVMGdBUUVnT1NmR29yMm1FamdzOHB1M2dOMDJUbXVDaWFrV0tMWXBVMUMyblJmMElWCjNUMWFzZWxDRUFiK1liTHI3QkUzcXN4RnZIaW9aZlhvRWI3bGRYb0JGd01zYlhXdnJGeHM0aEpOcjY0amhKbXEKZUZQTk53NytCdmFWdEdJblZab0tNQ3dqc0VxRUp1Q3ZzNGlNZmwrYjdjU3JTajBhUnBYanlJY2xrdHI3Z1RiYQpib3lFL09NblpwUC9IQklUUUdWMEZYZG9jVi9selRPQmlFME5MWHNJUXlReDQvQm41eFc5NkZHT3pHZDVENndjCjN0aFNlODYxQWdJUUFRS0NBUUFMZHNSb25Na2NzVlExcXIvVzd2WFF0RjZMNzVoY2NqZ21SNTdqT2JkYkZIRTIKeUlHWFZvdVlLenh0ZzBJaUYvYUt3cThDVGJ2bFU5WnZPeGZQc203SVpnOGd0UG54S0g0NUNNMjlCQld0bE5LeQpQeXEwV29wZEpZZytQWnQ3ZncwWW0wQkpQUFFQMHdmbUxkM2Q0c1ZGTEs1R284Z1Z6S2l5c2tVQzNERUE4Z2ZTCi9TMjc5Z29BNjVzajRjb3hVY2FqZ0Q3ZlRIa1lUVHI5WU95cjhjVjcvRXVsc0V3VkxMcVk4N2NWTVo4RXJ2L1gKckRzLzhJU3BaVWZ4eGJ1UUJTOGJJSlQyK1dSdEpxNW41SndobHM1amY4dGhObWFFQW5ZeHk4VzB5UHFXOXgwbwpCdElYQ05JQjBuRFJ1NkxMTjN4MkRkcHQxN0hYTGpIRkdVUXFRN1RKQW9HQkFQUldwcDR6aWJ2NlBIa015Z0t1Cis4WnZsVlFka3NDT3FuWFhONkt4d1FMaFU2NU5FN0ZOalF1Z0NFOUdzZklNYXdOZjlEZ3U1K2pLd0NXTk1uZUYKMTRwRWRYUHV1MGZDdGJMVzgxQ2RSYkVHa2JnQzJybDlab0VKcEx0UTdIc25xUkZpZjlFQXM2WUg0bEcwVm9XagozS0Z3dWE1S0hHNkFUVVE0aE04dFdxcFBBb0dCQU5FYTNlQ1lEZW9vM3ZIK1hmMUxhMEVsTTBFN1hyUk1hbmc5CmNkSVpoYkNJSHBBWXc3NVJOcHB0OTVIQ3I0UVMram1lSXA4cWhCVXBUT3hLQUdPYVc5MnoxcFoxVmRvUFE5aFAKbVZqZUM0T3dBOFRyYzh5aTV1Q0hLWDkxZ1lHcHpFQkY3R1NQQ3JOWnhLMUZIZFc3RGJqN1BYWS90WFV3aU13agpiWmFwYVdtN0FvR0JBTStLcGVVS2VOMm5QK3paa0FoSEpuYXdZbGpSa3hWbjMxVXBCelJHd1EvaGNvRnVaVlRPCkZuUDVOM3ZpMGdJRmJKU1V2TEh3dXZ5WWVMaGI4S0dDSG40UlNMVnovTjJxRXI1RnUxMUh5amxEZ0pYTkVBT1gKMFJtUUdIT2VCd294and0VWVsQnRFZEJ5cTUydzNIVERhN3h0ZnVtbFhOSVU0RmR3amMwb05nUlJBb0dCQUpTUwp2dVBmMG1aMEJMSFFtZUtWb0MrWlpvYkR0V0xzNEJ6U1hlNWlIVkZRcUM0RnVVRjl2K0ppWVB3WGl5UTYrUVhDCmRwT1ZEZTNySzJoM1VESEdaME5yeUpVRXMyaHlzaHhwUzNiZzRFRGkyWUFyRXVzUVZPL2pFTnozd3ozaXZtMEcKNFVLalBuYnZMVVFRRGVNM2JKbmRjQ2MzYzQ3QVFqUnhKcC9aQ01vSEFvR0JBTFBIbEZ5RXJKd0EzSUV6WG1qbQpLeWZ3WFBXUC9mZVhmQkcvSFFPZWdTRlZDZGVRMlVseHVTVDFUY2c1U3NZS1g0RVl3MVg5R3BCNGxtUFVLcTMwClNQM3BPNUtDZ1FBZ1JwVjFlNVQzN3NUWnJuWDJvZmdYVTVmNzMwMlcvcHlwZmtUcEJHa3lrK3BBWUZldTRPNHYKWFZlbUMvaGZnL0w0ZDNBb3RIbklGUWk5Ci0tLS0tRU5EIFBSSVZBVEUgS0VZLS0tLS0K
kind: Secret
metadata:
  name: int-mycloud-secret
  namespace: trigrammeAppli'''

    if (fileExists("init/secret-intg.yaml")) {
        usilColorLog("debug", "Le fichier init/secret-intg.yaml existe, à supprimer")
        sh ("rm -f init/secret-intg.yaml")
    } else {
        usilColorLog("debug", "Le fichier init/secret-intg.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
    writeFile file: "secret-intg.yaml", text: configYamlTRI
}

def secretRecf (def trigrammeAppli) {
    // Génération du secret-recf.yaml
    def configYaml = '''
---
apiVersion: v1
data:
  tls.crt: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlIcWpDQ0JaS2dBd0lCQWdJVEVRQUJQSnp3bk5ZbmhjZ1d2d0FBQUFFOG5EQU5CZ2txaGtpRzl3MEJBUXNGDQpBREJmTVJNd0VRWUtDWkltaVpQeUxHUUJHUllEZEdWak1SUXdFZ1lLQ1pJbWlaUHlMR1FCR1JZRWMya3liVEVTDQpNQkFHQ2dtU0pvbVQ4aXhrQVJrV0FtRmtNUjR3SEFZRFZRUURFeFZUU1RKTklFRkRJRWx1ZEdWeWJXVmthV0ZwDQpjbVV3SGhjTk1qQXdNekUzTVRRek9UVXpXaGNOTWpJd016RTNNVFF6T1RVeldqQnBNUXN3Q1FZRFZRUUdFd0pHDQpVakVPTUF3R0ExVUVDQk1GVUVGU1NWTXhEakFNQmdOVkJBY1RCVkJCVWtsVE1Rc3dDUVlEVlFRS0V3Sk5TREVMDQpNQWtHQTFVRUN4TUNUVWd4SURBZUJnTlZCQU1NRnlvdWNtVmpaaTV0ZVdOc2IzVmtMbk5wTW0wdWRHVmpNSUlCDQpJVEFOQmdrcWhraUc5dzBCQVFFRkFBT0NBUTRBTUlJQkNRS0NBUUVBeFlVU2JhaUVhVU1tV2kxZTZOUFBHR3hPDQpkcEpmc2JkSEdtS2EyWmFJVmZ4UlMrYVRxTFpvQWwrdUNPTGJ4TkQ5UGwrTGM3UW9iWUl1K0dqenVIZjFjR1lXDQpQaGo0TjNrOUFoZ0ZjR3ZQdzdHcDRuRzJMd3R6S1hCOGRtRDBuOTRqVGE4RGcvYlZaR0RjNUVtNFJyR20rdElZDQo1Qk0rUWxpcXhDS01PUlNpbi93ak1iT2kvLy9HT2plT2V5czhiUSttbXJlTDgwOEtjL0hYZ3ZZTjNXbXE1d0tWDQpyU1plREd3RWFSdG9EaXFlR2U3Qm1kZysxNlFRdWVqTTZxZ1UrVHVjeENXbTNXWlk5Vk1zMnFlZGJqMVNOeCt6DQpDNEVBTU9GOVg3VlYwcmJNbTB2dFRLb0d3L0c3ejAxckRBbFI2RmwxWTJuV2VVbndoSElOU050T1hhMkNUUUlDDQpFQUdqZ2dOVU1JSURVREFMQmdOVkhROEVCQU1DQmFBd0V3WURWUjBsQkF3d0NnWUlLd1lCQlFVSEF3RXdJZ1lEDQpWUjBSQkJzd0dZSVhLaTV5WldObUxtMTVZMnh2ZFdRdWMya3liUzUwWldNd0hRWURWUjBPQkJZRUZDdy9Md0J2DQpYRGNmenZ3emVZLzNuaThlbndWVE1COEdBMVVkSXdRWU1CYUFGQXRoajI3Wkd0Ym9aUlJYQkxqT3ZJdnJKQjhODQpNSUlCS2dZRFZSMGZCSUlCSVRDQ0FSMHdnZ0Vab0lJQkZhQ0NBUkdHZ2Nac1pHRndPaTh2TDBOT1BWTkpNazBsDQpNakJCUXlVeU1FbHVkR1Z5YldWa2FXRnBjbVVzUTA0OVZsZERNRUZRVURBeE15eERUajFEUkZBc1EwNDlVSFZpDQpiR2xqSlRJd1MyVjVKVEl3VTJWeWRtbGpaWE1zUTA0OVUyVnlkbWxqWlhNc1EwNDlRMjl1Wm1sbmRYSmhkR2x2DQpiaXhFUXoxaFpDeEVRejF6YVRKdExFUkRQWFJsWXo5alpYSjBhV1pwWTJGMFpWSmxkbTlqWVhScGIyNU1hWE4wDQpQMkpoYzJVL2IySnFaV04wUTJ4aGMzTTlZMUpNUkdsemRISnBZblYwYVc5dVVHOXBiblNHUm1oMGRIQTZMeTlRDQphMmxKYm5SUWNtOWtMbUZrTG5OcE1tMHVkR1ZqTDBObGNuUkZibkp2Ykd3dlUwa3lUU1V5TUVGREpUSXdTVzUwDQpaWEp0WldScFlXbHlaUzVqY213d2dnRTdCZ2dyQmdFRkJRY0JBUVNDQVMwd2dnRXBNSUc3QmdnckJnRUZCUWN3DQpBb2FCcm14a1lYQTZMeTh2UTA0OVUwa3lUU1V5TUVGREpUSXdTVzUwWlhKdFpXUnBZV2x5WlN4RFRqMUJTVUVzDQpRMDQ5VUhWaWJHbGpKVEl3UzJWNUpUSXdVMlZ5ZG1salpYTXNRMDQ5VTJWeWRtbGpaWE1zUTA0OVEyOXVabWxuDQpkWEpoZEdsdmJpeEVRejFoWkN4RVF6MXphVEp0TEVSRFBYUmxZejlqUVVObGNuUnBabWxqWVhSbFAySmhjMlUvDQpiMkpxWldOMFEyeGhjM005WTJWeWRHbG1hV05oZEdsdmJrRjFkR2h2Y21sMGVUQnBCZ2dyQmdFRkJRY3dBb1pkDQphSFIwY0RvdkwxQnJhVWx1ZEZCeWIyUXVZV1F1YzJreWJTNTBaV012UTJWeWRFVnVjbTlzYkM5V1YwTXdRVkJRDQpNREV6TG1Ga0xuTnBNbTB1ZEdWalgxTkpNazBsTWpCQlF5VXlNRWx1ZEdWeWJXVmthV0ZwY21VdVkzSjBNRDRHDQpDU3NHQVFRQmdqY1ZCd1F4TUM4R0p5c0dBUVFCZ2pjVkNJR0NwRE9CME1WdGc3V0pEb2VRcGd5RG1KZzlnUktDDQoySzVYZ2FhRGZ3SUJaQUlCQnpBYkJna3JCZ0VFQVlJM0ZRb0VEakFNTUFvR0NDc0dBUVVGQndNQk1BMEdDU3FHDQpTSWIzRFFFQkN3VUFBNElDQVFBbXZXMnBsTGN5dk4yK2NWNUI1aXZvREY1ZFNueTA0bjdOODYrWFpueitiZEtqDQpHMGMyZzRIS203YzZOL29CdGVDakxaUWYxRzJYSkZBcHJCUCtMZDg5ZnU2eWd6QzVIbm81V21lQzFTOExkczQ3DQpKOS9IWnYweG95dDdMcmliY0JHTFUvbWlCY1VVMlhVLyt5TlU4ZHdRSkNYVGFtN25TYU9EUWlSMzFFdkIxYW5PDQpvUFZRUVFSSWNwS3ZxQlQ2R1Q3SCtkdDFydXp6YnZNTGNUTzNMVzh4VGRQcmRIRzhaY2V5Y05YbDEvR3kvREcvDQpyVGRBUnpaOGlmNStpZmYvYjh3bGxNOXIvS3c4SkpGR1JqQlpqbHhNVHRQOFlsczNzZU9ZSnJZaDM1SzVyMUppDQpXbUhHdS9DTXhUVU9kT3NtelQzNkt6WHc3VnRHNHliRUd0VkxkL1lxeWlzVmp3eWlCaGNKRXlteGhweWg0eUFwDQphSzFHdnNHZEZiNTNNVnBYTFZvMGhJWGc5YUJUaVpWMFZSRFBjVkZ4ZEF1QTdoakUxbG5jNEpTdGVOUUhrVGxFDQpzaGRaaHV6UFFneFBaOVBmUXNMSFpVYWVYSTRDUU5IejdDWDFPbUxvQzdmNC8wMG83OXRBMkkxaUVXZlJGUHh4DQo5WnhRblBWZysyOUVBV09pUFAvTzdZWFljMjgzNWF2ZVJxVjVGL291Z29Bek1HKzNJbVVsWkkwVG53Q0lNd2VEDQp0WXZCVW1OVTVtSjkyUFA1aFFyeC8xM0xYd0RKWjFaQ3A0TjJhREdKT0FwSEU2TWY0Q3dlbUtqdWh4bjhkV2F3DQpCUy9Pc001TExaQW5CY2F0YkN6azlLZEFmSUZ2UVpHeEoybTlLZVZmQ1NVMSticjVuckNUT3ZCVDV4bGttUT09DQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tDQotLS0tLUJFR0lOIENFUlRJRklDQVRFLS0tLS0NCk1JSUdzakNDQkpxZ0F3SUJBZ0lUSGdBQUFBS2NUYWpKN00wR0VRQUFBQUFBQWpBTkJna3Foa2lHOXcwQkFRc0YNCkFEQVhNUlV3RXdZRFZRUURFd3hUU1RKTklFRkRJRkpQVDFRd0hoY05NVGN3TkRFNE1UVXlOalUyV2hjTk1qSXcNCk5ERTRNVFV6TmpVMldqQmZNUk13RVFZS0NaSW1pWlB5TEdRQkdSWURkR1ZqTVJRd0VnWUtDWkltaVpQeUxHUUINCkdSWUVjMmt5YlRFU01CQUdDZ21TSm9tVDhpeGtBUmtXQW1Ga01SNHdIQVlEVlFRREV4VlRTVEpOSUVGRElFbHUNCmRHVnliV1ZrYVdGcGNtVXdnZ0lpTUEwR0NTcUdTSWIzRFFFQkFRVUFBNElDRHdBd2dnSUtBb0lDQVFDdjhTNC8NCnFFZWV1Uk12a2Rwb0kyZWs5VUxoeS9MYk50ZjJlR3hJd25BOGMrSUVsZDJXZk81TlFDZnFsZU9QN1I1K0ZHU2QNCjZBWW5Dcm1JRUhhMkF5YlBSL1ozZlNvdFgyanB5Z3NoMjMrNTJ4b3J6bHF5VjU0bStXcFJ4enpmZFIrY3AxYkYNCitTbU1yaUlOUnViTGJYUVhiUnpXbmlkZlZnb055QUNhRTRvdW94dnRhL2o4bUlnbGM1UW92T0RzdmY2L1RrZHANCkJhbWRtNjFoRTV6ZVdtQW9EYjZOR1BKTWp6UTJwOC94bGVmem9Wdk9FTG9BaTRvUTFzK1hSaDJMd0VCS1Zxc1ENCnBQT1k5Q2c5ZmQyT1NUOVRBMFVlUlFaaHQrY2Nqa01wcG1GN2k0RHY3ZnF3K1Z4QjhtUGRzTjhwWGxSNHdNSnINCnpOL0NSdkY5OEs1TU82U1R5RmVEQ0tvNTZKaW80WFY0blJEblRodmR3TTNyZEJGUUI1SWU2Z2VQQVVKSElQVU4NCmdxWFZnd0ZxSjdLL3M1NWdyOEpyeWkxdzF5dGRobnB5dWttQ2JUN3FONWJqbVNKZTNjbWIzaXVuaEZkWWxjQzYNCmowMEZiREhPckJSSjliZmFNbHdVK3JwLzNYcW1ZNWpjeU4wN21UOTVWZGhmZXpwczBtQ1JXeEdKRHZmRm5YRTENClVZYmw5TkdVbXBYUjJUNE5xcG1rYkc3SlJLYXlsN0lEbk8zQ0picUpHb1NzS2RoWXA3MlhPbzdIa1A3RWZPbTQNCncvOHNGYUdWWEp5eHFvcnhFYWN4ZHV4S3c2aWhpNHZTcC9ockhhMjhDa1RKK05nNjZINStEekpzajByZ3RyY1QNClhCdTNoYUxaOGtlSmQ2WFZOKzZPbVR6MWpSM1VwTWtMMWdkVCtRSURBUUFCbzRJQnJUQ0NBYWt3RUFZSkt3WUINCkJBR0NOeFVCQkFNQ0FRQXdIUVlEVlIwT0JCWUVGQXRoajI3Wkd0Ym9aUlJYQkxqT3ZJdnJKQjhOTUJrR0NTc0cNCkFRUUJnamNVQWdRTUhnb0FVd0IxQUdJQVF3QkJNQXNHQTFVZER3UUVBd0lCaGpBUEJnTlZIUk1CQWY4RUJUQUQNCkFRSC9NQjhHQTFVZEl3UVlNQmFBRk9aeHBoMERtQ29PSGZiWlNSRk5ZYXRaSUlCU01FNEdBMVVkSHdSSE1FVXcNClE2QkJvRCtHUFdoMGRIQTZMeTlRYTJsSmJuUlFjbTlrTG1Ga0xuTnBNbTB1ZEdWakwwTmxjblJGYm5KdmJHd3YNClUwa3lUU1V5TUVGREpUSXdVazlQVkM1amNtd3dnY3NHQ0NzR0FRVUZCd0VCQklHK01JRzdNSUc0QmdnckJnRUYNCkJRY3dBb2FCcTJ4a1lYQTZMeTh2UTA0OVUwa3lUU1V5TUVGREpUSXdVazlQVkN4RFRqMUJTVUVzUTA0OVVIVmkNCmJHbGpKVEl3UzJWNUpUSXdVMlZ5ZG1salpYTXNRMDQ5VTJWeWRtbGpaWE1zUTA0OVEyOXVabWxuZFhKaGRHbHYNCmJpeEVRejF0WVd4aGEyOW1aaTF0WldSbGNtbGpMRVJEUFhSbFl6OWpRVU5sY25ScFptbGpZWFJsUDJKaGMyVS8NCmIySnFaV04wUTJ4aGMzTTlZMlZ5ZEdsbWFXTmhkR2x2YmtGMWRHaHZjbWwwZVRBTkJna3Foa2lHOXcwQkFRc0YNCkFBT0NBZ0VBS21ONXY4UDNrQUJtUVQvWCsvcWorR1J0L3grYlM0TjN2UHpXUFN6MlBjanZqMnkxYUwyZW5GVkUNCi9BT3FKMVRMbVZYV21McmdjemxxRjgvKzJjMlZMeTFYWFV6VUZmTkhPQ1RkZUdjQlF3aUQxMkdFdWliL3E4WUoNClREMkZoOG5kZTEvUkh6cWRBWklBbTlIVnRwalI5TW9kUkJTTXJocVVWaWI3UnF4bUx6M09zWUZ3c2NJbTNIUkgNClBGT1R4NHpYTlNTSEYxdkVvZVYyQ1VFOEllbUM5ZDdsRjBFYk1KUGpiNGdjMnMwdmlPdk9tbG5EVnJBbWMwb2ENCkV6TFhLQ0FseUZ3cnhFRGRVYWJZdWhoYTR6Qm1pVXVwbVZHRkRabVhkVFF1TWhCa1NpQzVMQ3ZXbDBvMDlNc1UNCnFpUkdHbmJGVDFhUzYrYlZPYTh0QzI3QlFVTmF0RFR0SWkzY0M3LzJtTzhWUFkrd1M4WnhLOTFiRTFUaW1lVE4NCmZqYStESnlxWGdXSHVpVVdGY29jSUZJdGh2UmtrQXRoMURWa2lGM0R4aXBOeHZ2KzFIMFVNNUcwaEVQRk5oZmwNCnNQZENEWEgxM2xrUkhCdytYK2g1YUd1bnBHZEkxZmtidktBYjd4aDRGZndEcjBkZXBmNVorT1lKMm52S2R4UlYNCkR2QTU5T1RsMXVQWjJZOGVNMzZEVXFKeDY4NmQ4WTl1SUpLMjNSRzc2WTZIczVOVW1DeGtoUEV1dzJnOUdtRk8NCk5mellCRlhRTlBiZC81QUZvNE1yOUtpMXhrbGlkK3N2eUNxQnNkVE1UREhpTXluQVR6UVljYTlrYTRBOUU2dUoNCjVzbWJueWRhSXorVDl1bVBZRHJ0VElISkJZMjM4Yi9hODFMd2wvUnVucTk5emJGczlVYz0NCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0NCi0tLS0tQkVHSU4gQ0VSVElGSUNBVEUtLS0tLQ0KTUlJRkNUQ0NBdkdnQXdJQkFnSVFHMGtldXVvaldxeE5oRFRoQUhNcG1qQU5CZ2txaGtpRzl3MEJBUXNGQURBWA0KTVJVd0V3WURWUVFERXd4VFNUSk5JRUZESUZKUFQxUXdIaGNOTVRjd05ERTRNVEkwT0RVd1doY05NamN3TkRFNA0KTVRJMU9EUTRXakFYTVJVd0V3WURWUVFERXd4VFNUSk5JRUZESUZKUFQxUXdnZ0lpTUEwR0NTcUdTSWIzRFFFQg0KQVFVQUE0SUNEd0F3Z2dJS0FvSUNBUURjRjdVeW1TS3htazVYc0NJcC81aEtaYjMyY2V2VkRFdk9zSU4zZWVScA0KZ3V5ZGEzRjNTVVRvd1Q5OHB0dGRtNGxoT2JoUTd6UmQ4cjg3QTlyem1BbTduNWV3M0F0aXNhWjF0eVpvYkt4ZQ0KcU9zZTFvZXkvSGVCbWZnaXczQzg2MkpOdFlhc3BLUG5lTVk3QzZ2YU9nMVhPeWRlSndURDRyWlYwWWR2eDBRUg0Kam4yaXA2bkdKSERZVnRqZHYxT2o1d29QVWVxbWRhSjVreE4vdXZmbVJFbEZaaDJaRGVHY0xYMDV0NHE0SnJ1cQ0KeWg2b3puRDhYbTk2NFRFSWdTYlF1RzBiOXJBNjc4dGVpenp1M1QxMDZwbU1xUUhYMk1kYnlqRmhQNHg0Q2VRdg0KWGllTTVhVlcvS1NPcDFrY2FIQlBuRUQ3QmFlUVk0ZUdmakNMM09ZaW9pNWkzS2VHRlVqRDVmaW8wV2s4R0p5OA0KdHFBSWpoNjlRd1c1SUZOTnFoeWlQTzhyajMxVjBxd1ViemkwU0NUTzdPQlN3TG00NExLN3o0RyszbUdUUEp5ZQ0KUVpoSGZ6eTcvMEhWY0dNMVFjRzBnVnhod2c5WmR5enc3SGRoTFJWbDdiandzMWpuTGlqZVkrSW9mZEl5ZkhlQg0KS1dXN1NWK2UvdE4vMEhuelo2MmQ1cmNIdzJ3eEhtRE1PN2F6ajlUYzVNT2MzcUM4eUNMRmRTT1N5cWttdVFBSg0KUVFMUGpGS3V2ejFpUi9xRTAxU2hLZllad2lHcWR0dVp5YlI4UXpRWFVLeXlqZzFNOXlTQzZWVC9zYWFqZ0xVRg0KdTUxSThlVHNlMHRnU3hWNFF6ZEoyc3RpRWw0NkpGSVdSWUErSUpUNkNwVEZGaGtXT0tLazlNelBheFEyeVF5ZA0KZ3dJREFRQUJvMUV3VHpBTEJnTlZIUThFQkFNQ0FZWXdEd1lEVlIwVEFRSC9CQVV3QXdFQi96QWRCZ05WSFE0RQ0KRmdRVTVuR21IUU9ZS2c0ZDl0bEpFVTFocTFrZ2dGSXdFQVlKS3dZQkJBR0NOeFVCQkFNQ0FRQXdEUVlKS29aSQ0KaHZjTkFRRUxCUUFEZ2dJQkFBN0lrQ1B3RThKbGlUMDJRODhIWGpKc2JTYVVYVnYvdHZBaGJMK0Y0YzdHQWhSUg0KNkp5dUlTUFI4Vzd3L1QxblRmcXRJT0FURW9teElqd1pGWlRlK0ZNYzBCa1ZrZW0zSGwrNE5tT0RpNDJDZ1d3aA0Kc1ZLQ05TZ1BaSXdUckJFMVlKL3BPRjdNaGFva3YzRDVRZWNjWjRoOWRqSG50bE82eFR6amJNMXpuQ2VFV0g4dA0KOGkvZSs2cW0rZzFCc0ZNc1k1M1hHanNTVnpPRU5nL1J3cEt5NFJqRDNqd0JmV0lRdkorZ1NmVkx3MjZYVzR0bQ0KbnhWa1pqRjJxOWpXdXcwWkN4VWRyQ1QxOStmamFUOENML3ZBcmdIVlYvTFk4eVpuU25JWkt3Rk1HZ21jK2tUdg0KeWRQdmV2UWlBSWVLYW82YXRyWVBDQVFrRWR4bnJyNTNTVFpMbFFmTk55K3ZpaFpLN2Y4OG43OHFqTmszWmh4Sw0KZHMzSGMwSlQwSFJibzZoNmdXQi8rd1dZSUhybmZPV2YwZmFMekpNM3pheStLek85dW56UnlwK0RvSjBJVW5pSw0KWm43ZC9pTjIzd3JvdHRqb3JJdHVpd0RMUWtvSjBnc2xsVGNBUVJIU0luZXVHVTFEZkFLVnZNekV5d242ck9xVA0KSGlPUHBzMTMvRDBWYXdNVlVLZlJDSndzZW9BYlAwb2N3bllTZElIcHhPQ21FNmNoWW9ieHp1ekRRUWZRbFdTYQ0KU0RydTZ3N3NPTjFWdlFjOE9EbUVOL3lWNGxVUVlIbXMyempzWnNxb0JQVnVZVHdhSWMrOFFERlpzdEJIZWdPOQ0KQ1VDM1BnRXpwOXdMSzIxOHFzMnFNTXRMV1ZaWHhmcWpCYVB3Ky8wcTQrQ1JNM3NON1RRWUNGcEo4STI3DQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0t
  tls.key: LS0tLS1CRUdJTiBQUklWQVRFIEtFWS0tLS0tDQpNSUlFdXdJQkFEQU5CZ2txaGtpRzl3MEJBUUVGQUFTQ0JLVXdnZ1NoQWdFQUFvSUJBUURGaFJKdHFJUnBReVphDQpMVjdvMDg4WWJFNTJrbCt4dDBjYVlwclpsb2hWL0ZGTDVwT290bWdDWDY0STR0dkUwUDArWDR0enRDaHRnaTc0DQphUE80ZC9Wd1poWStHUGczZVQwQ0dBVndhOC9Ec2FuaWNiWXZDM01wY0h4MllQU2YzaU5OcndPRDl0VmtZTnprDQpTYmhHc2FiNjBoamtFejVDV0tyRUlvdzVGS0tmL0NNeHM2TC8vOFk2TjQ1N0t6eHRENmFhdDR2elR3cHo4ZGVDDQo5ZzNkYWFybkFwV3RKbDRNYkFScEcyZ09LcDRaN3NHWjJEN1hwQkM1Nk16cXFCVDVPNXpFSmFiZFpsajFVeXphDQpwNTF1UFZJM0g3TUxnUUF3NFgxZnRWWFN0c3liUysxTXFnYkQ4YnZQVFdzTUNWSG9XWFZqYWRaNVNmQ0VjZzFJDQoyMDVkcllKTkFnSVFBUUtDQVFBVTdubGtlVmZlTlIxOE1lYXpwL2tHdVBvTUsyYUk5ZFgwQ2cyNUQzRWh5eVMyDQpZdDFJOVlaVFJkL3gxMWtiU0ZKOVlnVXZ3MW5JdndOUHI3cEVmQVMyZHBWczQ1SUJBVHh4Q2NDTTVIU1JKOVVuDQpPNG5rV1BraXVrNE1kaVRoQkFPTndITzFmdVNxMjh3Q2tvZENuM3F6WlpRUGdVZHM5dHZzRU1ockpqV080WGxqDQpCUHJFSENucVZjNEU0QXN6c1AzNE5IVk1RN0pReE1HSUZSbWZuYXE4cHRWOFl4VzhTVjA2Nk96REduRnkrNnFODQpyUVV0RFY0STRzck9aOWpFOS9FQXFJa09yWXEzSS9HVmFqNTRuc3FVdjVaVUFUbW1tcGFMcS82RXgzTWdEQzBCDQpMYmUrTnladTA0alJKWVJlcTJXWXdMZHpFSStOQUx0b1daQ1VOV3FEQW9HQkFPb3JRV0NJZWpJcTFmNkw0eEkyDQpLeDhsR1J2VURlUFpCd1owSHM5MWtyT3hRZDhKWDU0Y2hacnA1UGRidnZZci94Vno3cjZ1emZXRm9FUzFlV3VzDQpoQzExNmpBMUFhMXNDQkdpb1piMStkNFlwY0F1T0xYT3NSNGwxVGhHYjRYeXppOVJ2N1A5UEoyOExzYlNZVzM5DQoydkRNZEZyc2g3K0FNZVVXcUh6K3h6OW5Bb0dCQU5mdkl5S1YzMXkvSC96ejQwcjVVUlVSUEF2QVo0ZlpXaVFrDQpEc0h5blJ4SW4yYjVxTlFTVFlnSVZHT25xU2lYRG5JbnFUc1NoWkI5TFMwWUJGVklQR0VRL0x0SHBzeVFHTDkyDQpaSkpYWjFxRUorZVBnYmRpZnZ0OUhjaXFFSFI3S0FoUzJqV0cwUTVPU1krR2dnRWtWQTJFTkNabTFhQzZsbWxNDQpvWEVSSGNRckFvR0FiYTdKQkptbkpXNmF1RVlsVUFOQW45MXpuRC9sUTV0RUJsV2gxVFRTSGUzODdKK3FRWDlrDQo0ZlhWV0U0RzV3Yk1aVmFyUDhJSmZaOXd6eVBUVmF6SVlJWm1BWlc4SFczMktHQ2JpdkV1clpMUytvbi9FYnhxDQpWR3FTNjk3Qjg3STJWZVgrWUgxc3YxcGtnLzN3cG0yMzU3Sk9ubTlKNUtuVXd2Z2VEaTY2alpzQ2dZQWFUTTFLDQo4Zk1HN0JuMEFiWWxUK3FTRitnQ0xsMjNBVnFWbmNxdmxjVW5qNS94WEhSNkRsbnNZNzJNQ3p3MzNta3I5WFdXDQppT2tXK05CajZCNkZPMmtrZ1BtNmRTTlAxdENXeWMvRGw4eHFrRkNYZkZQWVNjczdqNVVYa0w3VlEyeTV0L3gxDQpuZ0szd3ExclZYMmVxYkppUDJjaisyZHdEYnBYVHBFQjVpZHQzd0tCZ0ZwS3VrZzljMDRBTjloOS93WWYxY1pUDQpIcVkvWGpWR0xaMUQwRm9aRGZNNWJyR25EbFZqUzBzNisxRENEMkg4TW85M1VYOEJzWWRpYUFNVmt3TDNwQ3FDDQo2dWdXZXFJL0pJV01TZ1RiNmZ1VXBtQkVhV2FJNVFUODhoYm9LYndhWWYvdTY1VmFoSDd6TmZUQjI2YWhVR2swDQp6c2RpSDY3RDBSdGVrc2hicjE1Tw0KLS0tLS1FTkQgUFJJVkFURSBLRVktLS0tLQ==
kind: Secret
metadata:
  name: recf-mycloud-secret
  namespace: trigrammeAppli'''

    if (fileExists("init/secret-recf.yaml")) {
        usilColorLog("debug", "Le fichier init/secret-recf.yaml existe, à supprimer")
        sh ("rm -f init/secret-recf.yaml")
    } else {
        usilColorLog("debug", "Le fichier init/secret-recf.yaml n'existe pas, à créer")
    }
    def configYamlTRI = configYaml.replaceAll("trigrammeAppli","${trigrammeAppli}")
    usilColorLog("debug", "${configYamlTRI}")
    writeFile file: "secret-recf.yaml", text: configYamlTRI
}