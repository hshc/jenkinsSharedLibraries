// import groovy.io.FileType
def call(def chartTemplateName,def gitProjectName) {
println GroovySystem.version

stage("Create Chart Helm"){
  new File("${env.WORKSPACE}/${chartTemplateName}").traverse(type: groovy.io.FileType.FILES, nameFilter: "*.yaml") { it ->
    println it
    }
  }
}
