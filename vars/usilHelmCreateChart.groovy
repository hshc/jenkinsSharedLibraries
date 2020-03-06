def call(def chartTemplateName,def gitProjectName) {
stage("Create Chart Helm"){
  def findTemplate = sh ( script : "find ${chartTemplateName}/templates -maxdepth 1 -type f -name *.yaml",returnStdout: true).trim()       
  println findTemplate
  println findTemplate.getClass()
  
  findTemplate.eachLine {
    line, count ->  println "line $count: $line"  // Output: line 0: Groovy is closely related to Java,
    }
  def list = findTemplate.readLines()
  println list.getClass()
  println list.size()
  println list[0]
  println list[1]

  def version = readFile list[0]
  println version
  
  // repécupération de la liste des fichiers template
  // modification du contenu et creation du nouveau fichier correctement nommé avec la bonne variabilisation
  version = version.replaceAll( 'modelTemplate', gitProjectName )
  println version
  def templateCible=list[0].replaceAll("${chartTemplateName}",'')
  println templateCible
  println "${gitProjectName}${templateCible}"
  //writeFile file: "${gitProjectName}${templateCible}", text: "This file is useful, need to archive it."
  }
}
  