def call() {
	stage('Git Checkout') {
		// Checkout avec l option noTags à false
		// Par défaut le checkout positionne noTags à true : checkout(scm)
			checkout([
			$class: 'GitSCM',
			branches: scm.branches,
			doGenerateSubmoduleConfigurations: scm.doGenerateSubmoduleConfigurations,
			extensions: [[$class: 'CloneOption', noTags: false, shallow: false, depth: 0, reference: ''],[$class: 'WipeWorkspace']],
			userRemoteConfigs: scm.userRemoteConfigs,
			])
		def gitUrl = sh(returnStdout: true, script: 'git config remote.origin.url').trim()
		env.gitProjectName = sh(returnStdout: true, script: "echo '$gitUrl' | awk -F'/si2m/' '{print \$2}' |  awk -F'.git' '{print \$1}'").trim()
		env.trigrammeAppli = sh(returnStdout: true, script: "echo '$env.gitProjectName' | awk -F'_' '{print \$1}'").trim()
		env.codeAppli = sh(returnStdout: true, script: "echo '$env.gitProjectName' | awk -F'_' '{print \$2}'").trim()
		env.projectName = sh(returnStdout: true, script: "echo '$env.gitProjectName' | awk -F'_' '{print \$3}'").trim()
		env.gitTag = sh(returnStdout: true, script: "git describe --tags --abbrev=0 || true").trim()
		println "env.gitTag = ${env.gitTag}"
		if (env.gitTag=~'V' || env.gitTag=~'v') {
			env.gitTag=env.gitTag.drop(1)
		}
		env.gitBranchName=env.BRANCH_NAME
	}
}