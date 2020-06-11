def call(def dockerRegistryUrl,def dockerImageName,def nexusRepo,def gitBranchName,def trigrammeAppli){
docker.withRegistry(dockerRegistryUrl) {
    def checkTS=''
	def checkBabel=''
	def checkAngular=''
    def angularLib=''
    docker.image(dockerImageName).inside("--entrypoint=''") {
        stage('install') {
            // installation des devs dependencies 
            sh "npm i --registry ${usilParams.nexusRepoNpm}"
            // lecture du fichier package.json
            def props = readJSON file: 'package.json'
            // vérification si on trouve la dépence de dev TypeScript
            try { checkTS=props.devDependencies.typescript;} catch (err) { checkTS='';}
		    if (!checkTS) { afficheColorText(32,"Pas de dépendance Typescript trouvée => Pas de build sur ce projet")}
             // vérification si on trouve la dépence de dev @angular core pour déterminer si c est un projet @angular core
            try { checkAngular=props.dependencies.'@angular/core';} catch (err) { checkAngular='';}  
		    if (!checkAngular) {
                if (!checkTS) {afficheColorText(32,"Projet NodeJS Javascript") }
                else { afficheColorText(32,"Projet NodeJS Typescript" )  }
			    }
		    else {
                afficheColorText(32,"Projet Angular version : ${checkAngular}")
			    }       
            }
        // Si Typescript et NodeJS test TSLint , test unitaire
	    if ((checkTS) && (!checkAngular)) {
            // test Eslint Node
            readConfRunTest('node.eslintignore','Recopie des confs pour les tests lints ','mv node.eslintignore .eslintignore')       
            readConfRunTest('node.eslintrc.js','Test Eslint Node','mv node.eslintrc.js .eslintrc.js && node ./node_modules/eslint/bin/eslint -c .eslintrc.js --ext .ts,.js .')
            // lancement des TU NodeTS
            readConfRunTest('node.tsconfig.spec.json','Recopie des confs pour les TU','mv node.tsconfig.spec.json tsconfig.spec.json') // && cat tsconfig.spec.json')      
            readConfRunTest('jest.spec.config.js','Test unitaire Node','node ./node_modules/jest/bin/jest.js --forceExit --coverage --verbose --config ./jest.spec.config.js')
        //    withSonarQubeEnv('SONARQUBE_USIL3') {
		//		sh "./node_modules/sonarqube-scanner/dist/bin/sonar-scanner -Dsonar.projectKey=${env.gitProjectName} -Dsonar.exclusions=**/node_modules/**, src/**/*.module.ts, src/environments/**, src/main.ts -Dsonar.test.inclusions=**/*.spec.ts -Dsonar.typescript.lcov.reportPaths=coverage/lcov.info -Dsonar.testExecutionReportPaths=coverage/sonar-report.xml"
		//		}
            }
        // Si Typescript et Angular test TSLint , test unitaire
        else if ((checkTS) && (checkAngular)) {
            // lancement des tests lint 
            readConfRunTest('angular.eslintignore','Recopie des confs pour les tests lints ','mv angular.eslintignore .eslintignore') //  && cat .eslintignore')               
            if (trigrammeAppli.toLowerCase() == 'bpp' || trigrammeAppli.toLowerCase() == 'lib') {
                try {
                    // recherche dans le repertoire projects un fichier package.json
                    def findLib = sh ( script : 'find ./projects -maxdepth 2 -type f -name package.json',returnStdout: true).trim()
                    // lecture du fichier package.json 
                    def repLib = findRepLib(findLib)   
                    def ciamPackage = readJSON file: "${findLib}"
                    // recupération du nom de la lib
                    angularLib=ciamPackage.name;
                    // build de la lib
                    sh "cd projects/${repLib[0]} && npm i --registry ${usilParams.nexusRepoNpm}"
                    sh "node node_modules/@angular/cli/bin/ng build ${repLib[0]}"
                    readConfRunTest('angular.eslintrc.js','Test Eslint Angular','mv angular.eslintrc.js .eslintrc.js && node ./node_modules/eslint/bin/eslint -c .eslintrc.js --ext .ts,.js .')
                    }
                catch (err) {
                    afficheColorText(31,"[Error] Erreur rencontrée lors du build de la lib")
				    currentBuild.result = 'FAILURE'
       			    }
                }
            else
                {
                 readConfRunTest('angular.eslintrc.js','Test Eslint Angular','mv angular.eslintrc.js .eslintrc.js && node ./node_modules/eslint/bin/eslint -c .eslintrc.js --ext .ts,.js .')
                }
            // lancement des TU Angular
            readConfRunTest('angular.tsconfig.spec.json','Recopie des confs pour les TU','mv angular.tsconfig.spec.json tsconfig.spec.json && cat tsconfig.spec.json')      
            readConfRunTest('angular.jest.config.js','Test unitaire Angular','mv angular.jest.config.js jest.config.js && node ./node_modules/@angular/cli/bin/ng test')
         //   withSonarQubeEnv('SONARQUBE_USIL3') {
		 //		sh "./node_modules/sonarqube-scanner/dist/bin/sonar-scanner -Dsonar.projectKey=${env.gitProjectName} -Dsonar.exclusions=**/node_modules/**, src/**/*.module.ts, src/environments/**, src/main.ts -Dsonar.test.inclusions=**/*.spec.ts -Dsonar.typescript.lcov.reportPaths=coverage/lcov.info -Dsonar.testExecutionReportPaths=coverage/sonar-report.xml"
		 //	}
        }
        // Si NodeJS Javascript test ESLint , test unitaire
        else if (!checkTS) {
            // test Eslint Node JS
            readConfRunTest('node.eslintignore','Recopie des confs pour les tests lints ','mv node.eslintignore .eslintignore') //  && cat .eslintignore')      
            readConfRunTest('node.eslintrc.js','Test Eslint Node','mv node.eslintrc.js .eslintrc.js && node ./node_modules/eslint/bin/eslint -c .eslintrc.js --ext .ts,.js .')
            // test TU Node JS
            readConfRunTest('node.mocharc.js','Test unitaire Node','node ./node_modules/mocha/bin/mocha --config node.mocharc.js')
            }
        // Publication librairie
        if (gitBranchName == 'master' && (trigrammeAppli.toLowerCase() == 'bpp' || trigrammeAppli.toLowerCase() == 'lib')) {
            // si Lib Nodejs
            if (!checkAngular) {
			    stage('Librarie publication Nexus Node') {
			    try {
				    sh "npm publish --userconfig=/home/jenkins/npmrc --registry ${usilParams.nexusRepoNpmPublish}"
			 	    } catch (err) {
                        afficheColorText(31,"[Error] Erreur rencontrée lors du push : problème Nexus ou Lib (version) déjà existante sur le Nexus")
				        currentBuild.result = 'FAILURE'
                        }
				    }
                }
            // si Lib Angular
            else {
                stage('Librarie publication Nexus Angular') {
			    try {
                    // push de la lib sur Nexus
				    sh "cd dist/${angularLib} && npm publish --userconfig=/home/jenkins/npmrc --registry ${usilParams.nexusRepoNpmPublish}"
			 	    } catch (err) {
                        afficheColorText(31,"[Error] Erreur rencontrée lors du push : problème Nexus ou Lib (version) déjà existante sur le Nexus")
				        currentBuild.result = 'FAILURE'
       				    }
				    }
                }
			}
		if ((gitBranchName == 'master' || gitBranchName == 'develop') && (trigrammeAppli.toLowerCase() != 'bpp') && trigrammeAppli.toLowerCase() != 'lib') {
            // Npm install pour les projets NodeJS Typescript 
			if (checkTS) {
				stage('npm Build prod') {
					echo 'Version typescript: '+checkTS
                    if (checkAngular) {
                        sh 'node --max_old_space_size=8192 node_modules/@angular/cli/bin/ng build --prod --buildOptimizer --extract-css --build-optimizer --optimization --progress --sourceMap=false'
                        }
                    else {
                        sh 'node ./node_modules/typescript/bin/tsc'
                        }
					}
				}
            else {
                // Npm install pour les projets NodeJS Javascript en mode prod pour supprimer les dev dependencies
			    stage('npm ci prod') {
				    sh "npm ci --prod --registry ${usilParams.nexusRepoNpm}"
				    } 
                }    
            }
        }
    }
}
// Fonction permettant de récupérer les fichiers de confs avec le module configuration File et d éxécuter une commande
def readConfRunTest(def idConfFile, def fileConf , def cmdStage) {
    configFileProvider([configFile(fileId: idConfFile, variable: 'FILECONF')])
		{  
		sh "cat ${env.FILECONF}"
		sh "cp -f ${env.FILECONF} ${WORKSPACE}/${idConfFile}"
        }
    stage("${fileConf}") {
        sh "${cmdStage}"
        }
}
def afficheColorText(def color,def textAfficher) {
     wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
            echo "\033[1;${color}m${textAfficher} \033[0m"
            }
}
@NonCPS
def findRepLib(text) {
   def matcher = text =~ /(?<=^.\/projects\/)(.*)(?=\/package.json)/
  matcher ? matcher[0]: null
}