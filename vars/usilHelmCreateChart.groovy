import groovy.io.FileType
def call(def chartTemplateName,def gitProjectName) {
println GroovySystem.version

stage("Create Chart Helm"){
  new File(env.WORKSPACE).eachFileRecurse(FileType.FILES) {
  println it
  //  file ->
   //   list << file
  //  println file
    //if(it.name.endsWith('*.yaml')) {
     //   println it
     //   }
    }
  }
}
  