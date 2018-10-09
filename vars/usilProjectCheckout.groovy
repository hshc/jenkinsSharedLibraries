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
		usilParams.gitProjectName = sh(returnStdout: true, script: "echo '$gitUrl' | awk -F'/si2m/' '{print \$2}' |  awk -F'.git' '{print \$1}'").trim()
		usilParams.trigrammeAppli = sh(returnStdout: true, script: "echo '$gitProjectName' | awk -F'_' '{print \$1}'").trim()
		usilParams.codeAppli = sh(returnStdout: true, script: "echo '$gitProjectName' | awk -F'_' '{print \$2}'").trim()
		usilParams.projectName = sh(returnStdout: true, script: "echo '$gitProjectName' | awk -F'_' '{print \$3}'").trim()
		gitTag = sh(returnStdout: true, script: "git tag -l | sort -V | tail -n 1 | cut -c 2-").trim()
		echo env.BRANCH_NAME
		usilParams.gitBranchName=env.BRANCH_NAME
	}
}