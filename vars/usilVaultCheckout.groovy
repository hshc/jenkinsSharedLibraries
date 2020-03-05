// la fonction reçoie en entrée l'URL Vault, l'ID Vault, le code environnement, le trigramme applicatif, 
// nom du repo git, le tag git, la clé vaultKey par défaut prend la valeur value.yaml
def call(def vaultUrl, def vaultId,def codeEnv,def trigrammeAppli, def gitProjectName, def gitTag, def vaultKey='value.yaml' ) {
stage("Récupération env Vault env:${codeEnv} version:${gitTag}"){
      // Utilisation du plugin Vault pour aller récupérer la valeur dans kv/TRIGRAMME/trigramme_codeappli_description/tag la clé par défaut est value.yaml
      withVault(configuration: [timeout: 60, vaultCredentialId: vaultId, vaultUrl: vaultUrl], vaultSecrets: [[path: "kv/${trigrammeAppli.toUpperCase()}/${gitProjectName.toLowerCase()}/${gitTag}", secretValues: [[envVar: 'vaultValue', vaultKey: "${vaultKey}"]]]]) {
        sh "rm -f ${vaultKey}"
        // ecriture du fichier à partie de la clé lue sur Vault
        writeFile file: "${env.WORKSPACE}/${vaultKey}", text: vaultValue
    }
  }
}