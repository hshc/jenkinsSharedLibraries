def call(def chartTemplateName,def helmProjectName) {
stage("Create Chart Helm : ${helmProjectName} template: ${chartTemplateName}"){
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
    // on personnalise le contenu des variables du template modelTemplate par le nom du service (helmProjectName)
    contenuTemplateYaml = contenuTemplateYaml.replaceAll( 'modelTemplate', helmProjectName )
    // on supprime le path 
    def templateCible=listeFichierTemplate[index].replaceAll("${chartTemplateName}/templates/",'')
    println "Préparation pour le service ${appliHelmName} du composant Helm : ${helmProjectName}_${templateCible}"
    // on écrit le fichier dans le repertoire helmProjectName/templates/ et on préfixe les fichiers avec helmProjectName
    writeFile file: "${helmProjectName}/templates/${helmProjectName}-${templateCible}", text: contenuTemplateYaml
    index++
    }
  }
}  