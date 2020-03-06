import groovy.io.FileType
def call(def chartTemplateName,def gitProjectName) {
println GroovySystem.version

stage("Create Chart Helm"){
  def dir = new File("${env.WORKSPACE}/${chartTemplateName}")
  dir.eachFileRecurse(FILES) {
    if(it.name.endsWith('.yaml')) {
        println it
      }
    }
  }
}

