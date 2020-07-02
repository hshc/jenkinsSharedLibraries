// la fonction reçoie en entrée l'URL Vault, l'ID Vault, le code environnement, le trigramme applicatif, 
// nom du repo git, le tag git, le path ou est stocké le fichier récupéré sur Vault, la clé vaultKey par défaut prend la valeur value.yaml, 
def call(def codeEnv,def trigrammeAppli, def gitProjectName, def gitTag, def vaultKeyPath, def vaultKey='values.yaml' ) {
stage("Récupération env Vault env:${codeEnv} version:${gitTag} fichier:${vaultKey}"){
      usilColorLog("stage", "Récupération env Vault env:${codeEnv} version:${gitTag} fichier:${vaultKey}")
      if (vaultKeyPath == 'e1' || vaultKeyPath == 'e0') {
        vaultUrl="https://keymaster.si2m.tec"
        vaultId="jenkins-vault-prod"
      }
      else {
        vaultUrl="https://recf-keymaster.si2m.tec"
        vaultId="jenkins-vault-recf"
      }
      def pathGitTag = (gitTag == "") ? "" : "/${gitTag}"
      def vaultPath="kv/${trigrammeAppli.toUpperCase()}/${gitProjectName.toLowerCase()}/${codeEnv}${pathGitTag}"
      // Utilisation du plugin Vault pour aller récupérer la valeur dans kv/TRIGRAMME/trigramme_codeappli_description/tag la clé par défaut est values.yaml
      withVault(configuration: [skipSslVerification: true, timeout: 60, vaultCredentialId: vaultId, vaultUrl: vaultUrl], 
      vaultSecrets: [[path: "${vaultPath}", 
      secretValues: [[envVar: 'vaultValue', vaultKey: "${vaultKey}"]]]]) {
        // véirification si le path contient un / à la fin si oui on ne fait sinon on l'ajoute
        vaultKeyPath += vaultKeyPath?.endsWith('/') ? '' : '/'
        sh "rm -f ${vaultKeyPath}${vaultKey}"
        // écriture du fichier à partie de la clé lue sur Vault
        writeFile file: "${env.WORKSPACE}/${vaultKeyPath}${vaultKey}", text: vaultValue
      }
  }
}