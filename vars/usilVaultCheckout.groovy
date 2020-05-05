// la fonction reçoie en entrée l'URL Vault, l'ID Vault, le code environnement, le trigramme applicatif, 
// nom du repo git, le tag git, le path ou est stocké le fichier récupéré sur Vault, la clé vaultKey par défaut prend la valeur value.yaml, 
def call(def vaultUrl, def vaultId,def codeEnv,def trigrammeAppli, def gitProjectName, def gitTag, def vaultKeyPath, def vaultKey='values.yaml' ) {
stage("Récupération env Vault env:${codeEnv} version:${gitTag}"){
      usilColorLog("stage", "Récupération env Vault env:${codeEnv} version:${gitTag}")
      if (gitTag == '')
        {
          def vaultPath="kv/${trigrammeAppli.toUpperCase()}/${gitProjectName.toLowerCase()}/${codeEnv}"
        }
      else {
         def vaultPath="kv/${trigrammeAppli.toUpperCase()}/${gitProjectName.toLowerCase()}/${codeEnv}/${gitTag}"
      }
      println vaultPath
      // Utilisation du plugin Vault pour aller récupérer la valeur dans kv/TRIGRAMME/trigramme_codeappli_description/tag la clé par défaut est value.yaml
      withVault(configuration: [timeout: 60, vaultCredentialId: vaultId, vaultUrl: vaultUrl], 
      vaultSecrets: [[path: "${vaultPath}", 
      secretValues: [[envVar: 'vaultValue', vaultKey: "${vaultKey}"]]]]) {
        // véirification si le path contient un / à la fin si oui on ne fait sinon on l'ajoute
        vaultKeyPath += vaultKeyPath?.endsWith('/') ? '' : '/'
        sh "rm -f ${vaultKeyPath}${vaultKey}"
        // écriture du fichier à partie de la clé lue sur Vault
        writeFile file: "${env.WORKSPACE}/${vaultKeyPath}${vaultKey}", text: vaultValue
        def mydata = readYaml file: "${env.WORKSPACE}/${vaultKeyPath}${vaultKey}"
        String nomService = mydata.keySet()
        usilColorLog("info", "Nom du Service : ${nomService}")
        if (nomService.indexOf('-') > 0) {
          usilColorLog("error", "Problème de nommage du service qui ne peut pas inclure - dans le nom: ${nomService}")
          currentBuild.result = 'FAILURE'
          }
      }
  }
}
