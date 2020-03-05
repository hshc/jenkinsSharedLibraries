def call(def vaultUrl, def vaultId,def codeEnv,def trigrammeAppli, def gitProjectName, def gitTag, def vaultKey='value.yaml' ) {
stage("Récupération env Vault env:${codeEnv} version:${gitTag}"){
      withVault(configuration: [timeout: 60, vaultCredentialId: vaultId, vaultUrl: vaultUrl], vaultSecrets: [[path: "kv/${trigrammeAppli.toUpperCase()}/${gitProjectName.toLowerCase()}/${gitTag}", secretValues: [[envVar: 'vaultValue', vaultKey: "${vaultKey}"]]]]) {
       writeYaml file: "${env.WORKSPACE}/${vaultKey}", data: vaultValue
       writeFile file: "${env.WORKSPACE}/${vaultKey}", data: vaultValue
    }
  }
}