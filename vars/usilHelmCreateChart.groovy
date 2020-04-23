def call(def chartTemplateName,def helmServiceName) {
stage("Create Chart Helm : ${helmServiceName} template: ${chartTemplateName}"){
  println("helmServiceName : ${helmServiceName}")
  // on liste les fichiers présent dans le repertoire ${chartTemplateName}/templates
  def findTemplate = sh ( script : "find ${chartTemplateName}/templates -maxdepth 1 -type f -name *.yaml",returnStdout: true).trim()       
  // on créé une liste contenant la liste des fichiers avec leurs paths
  println("findTemplate : ${findTemplate}")
  def listeFichierTemplate = findTemplate.readLines()
  println listeFichierTemplate
  // la fonction eachWithIndex nétant pas disponible une variable d'index est instancié 
  def index=0
  // fonctionne qui par la liste des fichiers
  listeFichierTemplate.each {
    // lecture de du fichier récupéré sur la closure it dont le contenu est stocké dans contenuTemplateYaml
    contenuTemplateYaml = readFile it
    // on personnalise le contenu des variables du template modelTemplate par le nom du service (helmServiceName)
    contenuTemplateYaml = contenuTemplateYaml.replaceAll( 'modelTemplate', helmServiceName )
    println listeFichierTemplate
    // on supprime le path 
    def templateCible=listeFichierTemplate[index].replaceAll("${chartTemplateName}/templates/",'')
    println "Préparation pour le service ${helmServiceName} du composant Helm : ${helmServiceName}_${templateCible}"
    // on écrit le fichier dans le repertoire helmServiceName/templates/ et on préfixe les fichiers avec helmServiceName
    writeFile file: "${helmServiceName}/templates/${helmServiceName}-${templateCible}", text: contenuTemplateYaml
    index++
    }
  }
}  