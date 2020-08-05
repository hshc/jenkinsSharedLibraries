#!/usr/bin/env groovy
def call(def autoApprove) {
    sh "make version"
    sh "make init"
    sh "make plan"
    def exitCode = readFile('.tfplan_status').trim()
    if (exitCode == "0") {
        currentBuild.result = 'SUCCESS'
    }
    else if (exitCode == "1") {
        currentBuild.result = 'FAILURE'
    }
    else if (exitCode == "2") {
        if (autoApprove != true) {
            timeout(time: 10, unit: 'MINUTES') {
                office365ConnectorSend message: "Terraform Plan Awaiting Approval: ${env.JOB_NAME} - [${env.BUILD_NUMBER}]. [View on Jenkins](${env.JOB_URL})", status: "WAITING", webhookUrl: "${env.ms_teams_notif_url}", color: '4579BA'
                input message: 'Apply the current Terraform Plan? (Click "Apply" to continue)', ok: 'Apply'
            }
        }
        sh "make apply"
    }
}