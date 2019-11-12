#!/usr/bin/env groovy
def call(def awsTechnicalAccount, def keytabFile, def kerberosConfigFile='krb5_conf') {
    withCredentials([
        file(credentialsId: kerberosConfigFile, variable: 'krb5_conf'),
        file(credentialsId: keytabFile ,variable: 'keytab'),
        usernamePassword(credentialsId: awsTechnicalAccount, passwordVariable: 'pwd', usernameVariable: 'user'), 
        usernamePassword(credentialsId: 'jenkins-bitbucket-common-creds' ,usernameVariable: 'SCM_LOGIN', passwordVariable: 'SCM_PWD')
    ]) {
        // Use url encoding to pass login like 'qsdf@si2m.fr' to the https git login protocol
        // git login protocol: https://${SCM_LOGIN}:${SCM_PWD}@bitbucket.org
        def LOGIN=java.net.URLEncoder.encode("${SCM_LOGIN}", "UTF-8")
        def PWD=java.net.URLEncoder.encode("${SCM_PWD}", "UTF-8")
        sh "echo 'https://${LOGIN}:${PWD}@bitbucket.org' > /home/jenkinsDonD/git.store"
        sh "sudo /home/jenkinsDonD/copyKrb5.sh ${krb5_conf}" 
        sh "kinit -V ${user} -k -t ${keytab}" 
        sh "/usr/local/bin/aws-saml-cli --service --username ${user} -vvv" 
    }
}