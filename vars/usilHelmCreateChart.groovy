def call(def chartTemplateName,def gitProjectName) {
stage("Create Chart Helm"){
  def findTemplate = sh ( script : 'find ./chart_nodejs -maxdepth 1 -type f -name *.yaml',returnStdout: true).trim()       
  println findTemplate
  println findTemplate.getClass()
  
  findTemplate.eachLine {
    line, count ->  println "line $count: $line"  // Output: line 0: Groovy is closely related to Java,
    }
  def list = findTemplate.readLines()
  println list.getClass()
  println list[0]
  println list[1]

  def version = readFile list[0]
  println version
  }
}
  