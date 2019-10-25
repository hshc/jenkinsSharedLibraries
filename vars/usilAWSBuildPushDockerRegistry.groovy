#!/usr/bin/env groovy
def call(def dockerRegistryUrl, def dockerRegistryUser, def dockerRegistryRepoAppli, def dockerFilePath = './Dockerfile', def dockerBuildArgs = ''){
    withCredentials([usernamePassword(credentialsId: dockerRegistryUser, usernameVariable: 'userName', passwordVariable: 'password')]) {
        // Connect build and push image to DTR
        sh "echo ${password} | docker login -u ${userName} --password-stdin ${dockerRegistryUrl}"
        def imageName = dockerRegistryRepoAppli.substring(dockerRegistryRepoAppli.indexOf('/')+1)
        def dtrFQDN = getDtrFQDN(dockerRegistryUrl)
        def dtrImageUri = "${dtrFQDN}/${dockerRegistryRepoAppli}"
        sh "docker build -t ${imageName} -f ${dockerFilePath} --no-cache --rm ${dockerBuildArgs} ."
        sh "docker tag ${imageName} ${dtrImageUri}"
        sh "docker push ${dtrImageUri}"
    }
    setRepoVisibilityPublic(dockerRegistryUrl, dockerRegistryRepoAppli)
}

def setRepoVisibilityPublic(def dockerRegistryUrl, def dockerRegistryRepoAppli) {
    // connect to DTR via API for changing repository vibility: set it to "public"
    def urlRepo= "${dockerRegistryUrl}/api/v0/repositories/${dockerRegistryRepoAppli.replaceAll(/:.*$/, "")}"
    def response = httpRequest authentication: 'DockerDTR', acceptType: 'APPLICATION_JSON', contentType: 'APPLICATION_JSON', httpMode: 'PATCH', requestBody: '{"visibility": "public"}', url: urlRepo, ignoreSslErrors:true
    if (response.status != 200) {
        println response
        println "Repo sur la DTR inexistant"
        currentBuild.result = 'FAILURE'
    }
}
/*
def getDtrFQDN(def dtrUri = usilParams.dockerRegistryUrl) {
    def dtrFQDN = dtrUri.substring("https://".length())
    return dtrFQDN
}
*/
def getDtrFQDN(def dtrUrl = usilParams.dockerRegistryUrl) {
    return new URL(dtrUrl).getHost()
}