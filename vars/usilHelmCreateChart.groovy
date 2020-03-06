def call(def chartTemplateName,def gitProjectName) {
stage("Create Chart Helm"){
  def findTemplate = sh ( script : 'find ./chart_nodejs -maxdepth 1 -type f -name *.yaml',returnStdout: true).trim()       
  println findTemplate
  println findTemplate.getClass()

  }
}
  