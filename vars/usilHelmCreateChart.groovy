import groovy.io.FileType
def call(def chartTemplateName,def gitProjectName) {
	stage("Create Chart Helm"){
    // liste les fichiers yaml template
    def list = []
    def dir = new File("${env.WORKSPACE}/${chartTemplateName}")
    println dir
    dir.eachFileRecurse (FileType.FILES) { file ->
      list << file
    } 
    list.each {
    println it.path
    }
  }
}