#!/usr/bin/env groovy
def call(def awsTechnicalAccount, def keytabFile, def kerberosConfigFile='krb5_conf') {
    withCredentials([
        file(credentialsId: kerberosConfigFile, variable: 'krb5_conf')
        file(credentialsId: keytabFile ,variable: 'keytab'),
        usernamePassword(credentialsId: awsTechnicalAccount, passwordVariable: 'pwd', usernameVariable: 'user') 
    ]) {
        sh "sudo /home/jenkinsDonD/copyKrb5.sh ${krb5_conf}" 
        sh "kinit -V ${user} -k -t ${keytab}" 
        sh "/usr/local/bin/aws-saml-cli --service --username ${user} -vvv" 
    }
}