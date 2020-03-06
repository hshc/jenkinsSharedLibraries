def call(def chartTemplateName,def gitProjectName) {
stage("Create Chart Helm : ${gitProjectName} template: ${chartTemplateName}"){
  // on liste les fichiers présent dans le repertoire ${chartTemplateName}/templates
  def findTemplate = sh ( script : "find ${chartTemplateName}/templates -maxdepth 1 -type f -name *.yaml",returnStdout: true).trim()       
  // on créé une liste contenant la liste des fichiers avec leurs paths
  def listeFichierTemplate = findTemplate.readLines()
  // la fonction eachWithIndex nétant pas disponible une variable d'index est instancié 
  def index=0
  // fonctionne qui par la liste des fichiers
  listeFichierTemplate.each {
    // lecture de du fichier récupéré sur la closure it dont le contenu est stocké dans contenuTemplateYaml
    contenuTemplateYaml = readFile it
    // on personnalise le contenu des variables du template modelTemplate par le nom du service (gitProjectName)
    contenuTemplateYaml = contenuTemplateYaml.replaceAll( 'modelTemplate', gitProjectName )
    // on supprime le path 
    def templateCible=listeFichierTemplate[index].replaceAll("${chartTemplateName}/templates/",'')
    println "Préparation pour le service ${gitProjectName} du composant Helm : ${gitProjectName}_${templateCible}"
    // on écrit le fichier dans le repertoire gitProjectName/templates/ et on préfixe les fichiers avec gitProjectName
    writeFile file: "${gitProjectName}/templates/${gitProjectName}-${templateCible}", text: contenuTemplateYaml
    index++
    }
  }
}  