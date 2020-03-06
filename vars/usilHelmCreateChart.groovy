import groovy.io.FileType
def call(def chartTemplateName,def gitProjectName) {
println GroovySystem.version

stage("Create Chart Helm"){
  new File"${env.WORKSPACE}/${chartTemplateName}").eachFileRecurse(FileType.FILES) {
    if(it.name.endsWith('.yaml')) {
        println it
        }
    }
  }
}
  