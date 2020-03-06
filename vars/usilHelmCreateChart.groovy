import groovy.io.FileType
def call(def chartTemplateName,def gitProjectName) {
stage("Create Chart Helm"){
  import static groovy.io.FileType.FILES
  def dir = new File("${env.WORKSPACE}/${chartTemplateName}")
  dir.eachFileRecurse(FILES) {
    if(it.name.endsWith('.yaml')) {
        println it
      }
    }
  }
}

