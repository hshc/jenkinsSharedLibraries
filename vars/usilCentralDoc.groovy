#!/usr/bin/env groovy
def call buildAndUploadDocToCentral() {
    buildDoc()
    updateDocToCentral()
}

def buildDoc() {
    docker.image("${usilParams.dockerRegistryRepo}/usil3-mkdocs:latest").inside {
        sh 'mkdocs build'
    }    
}
def updateDocToCentral() {
    withCredentials([usernamePassword(credentialsId: 'jenkins-bitbucket-common-creds', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
        def bbuser = java.net.URLEncoder.encode("${GIT_USERNAME}", "UTF-8")
        sh "git clone https://${bbuser}:${GIT_PASSWORD}@bitbucket.org/si2m/usil3_docs_portal.git usil3_docs_portal"
    }

    if (env.gitTag == null || env.gitTag == '') {
        env.gitTag = 'latest'
    }

    // Force the selection of a branch
    dir ('usil3_docs_portal') {
        sh "git checkout master"
    }    

    // Clean old version of documentation.
    sh "rm -rf usil3_docs_portal/external_generated_docs/${env.gitProjectName}"
    sh "mkdir -p usil3_docs_portal/external_generated_docs"
    sh "mv site usil3_docs_portal/external_generated_docs/${env.gitProjectName}"

    dir ('usil3_docs_portal') {
        sh "git add external_generated_docs/${env.gitProjectName}"
        sh "git commit -m \"docs(${env.gitProjectName}): update generated documentation version ${env.gitTag}.\""
        withCredentials([usernamePassword(credentialsId: 'jenkins-bitbucket-common-creds', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            def bbuser = java.net.URLEncoder.encode("${GIT_USERNAME}", "UTF-8")
            sh "git push origin master"
        }
    }
}