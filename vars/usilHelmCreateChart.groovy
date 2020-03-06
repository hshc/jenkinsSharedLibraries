def call(def chartTemplateName,def gitProjectName) {
stage("Create Chart Helm"){
  def findTemplate = sh ( script : "find ${chartTemplateName}/templates -maxdepth 1 -type f -name *.yaml",returnStdout: true).trim()       
  println findTemplate
  println findTemplate.getClass()
  
  //findTemplate.eachLine {
  //  line, count ->  println "line $count: $line"  // Output: line 0: Groovy is closely related to Java,
  //  }
  def listeFichierTemplate = findTemplate.readLines()
  listeFichierTemplate.each {
    println "Number ${it}"
    version = readFile listeFichierTemplate[it]
        println "Number ${it} : ${version}"
      }
  /*
  println listeFichierTemplate.getClass()
  println listeFichierTemplate.size()
  println listeFichierTemplate[0]
  println listeFichierTemplate[1]

  def version = readFile listeFichierTemplate[0]
  println version
  
  // repécupération de la liste des fichiers template
  // modification du contenu et creation du nouveau fichier correctement nommé avec la bonne variabilisation
  version = version.replaceAll( 'modelTemplate', gitProjectName )
  println version
  def templateCible=listeFichierTemplate[0].replaceAll("${chartTemplateName}/templates/",'')
  println templateCible
  println "${gitProjectName}/templates/${gitProjectName}-${templateCible}"
  writeFile file: "${gitProjectName}/templates/${gitProjectName}-${templateCible}", text: version
  */
  }
}

  