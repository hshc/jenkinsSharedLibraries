import groovy.json.JsonSlurper
def call() { 
    script {
    // Récupération du Dockerfile du projet
    def props = readProperties interpolate: false, file: "Dockerfile";
    println('props: '+props);
    // Récupération de la ligne FROM du Dockerfile
    def imageDockerfile=props.FROM;
    println('imageDockerfile: '+imageDockerfile);
    // Test du nom du repository commencant par dtr.docker.si2m.tec/tools-store/ 
    if ( imageDockerfile =~ /^dtr.docker.si2m.tec\/tools-store\/*/) {
        // Récupération du repository {namespace}/{reponame} (tools-store/*** sans tag)
        String repoTools = imageDockerfile.split('/')[1] + '/' + imageDockerfile.split('/')[2].split(':')[0];
        println('repoTools: '+repoTools);
        // Appel API pour rechercher l'image sur la DTR
        def response = httpRequest authentication: 'DockerDTR', url: "https://dtr.docker.si2m.tec/api/v0/repositories/${repoTools}", ignoreSslErrors:true, validResponseCodes: '200:404';
        // Test du code retour de l'appel de l'API
        if (response.status.toString()=='200') {
            println "Repo sur la DTR existant";
            currentBuild.result = 'SUCCESS';
            return '200';
        } else {
            println('Repo sur la DTR inexistant : '+repoTools);
            currentBuild.result = 'FAILURE';
            return '404';
        }
    } else {
        println('Le Dockerfile ne pointe pas sur dtr.docker.si2m.tec/tools-store/ : '+imageDockerfile);
        currentBuild.result = 'FAILURE';
        return '404';
    }
}}