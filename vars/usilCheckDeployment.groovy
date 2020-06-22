def call(def codeEnv) { 
    stage('Validation du déploiement en ' + "${codeEnv}") {
        usilColorLog("stage", "Validation du déploiement en ${codeEnv}")
        switch("${codeEnv}") { 
            case "e4": 
                ENV_TO_DEPLOY = "dev"
                ENV_TO_TEST = ""
                CODE_ENV_TO_TEST = ""
                usilColorLog("warning", "Pas de validation, déploiement autorisé pour le moment pour cet environnement")
                currentBuild.result = 'SUCCESS'
                sh "exit 0"
                return true; 
                break;
            case "e3": 
                ENV_TO_DEPLOY = "intg"
                ENV_TO_TEST = "dev"
                CODE_ENV_TO_TEST = "e4"
                usilColorLog("warning", "Pas de validation, déploiement autorisé pour le moment pour cet environnement")
                currentBuild.result = 'SUCCESS'
                sh "exit 0"
                return true; 
                break;
            case "e2": 
                ENV_TO_DEPLOY = "recf"
                ENV_TO_TEST = "intg"
                CODE_ENV_TO_TEST = "e3";
                usilColorLog("warning", "Pas de validation, déploiement autorisé pour le moment pour cet environnement")
                currentBuild.result = 'SUCCESS'
                sh "exit 0"
                return true;
                break;
            case "el": 
                ENV_TO_DEPLOY = "rec4"
                ENV_TO_TEST = "recf"
                CODE_ENV_TO_TEST = "e2"; 
                usilColorLog("error", "Validation éffectuée, déploiement non autorisé pour cet environnement")
                currentBuild.result = 'FAILURE'
                sh "exit 1"
                return false;
                break;
            case "er": 
                ENV_TO_DEPLOY = "int2"
                ENV_TO_TEST = "recf"
                CODE_ENV_TO_TEST = "e2"; 
                usilColorLog("error", "Validation éffectuée, déploiement non autorisé pour cet environnement")
                currentBuild.result = 'FAILURE'
                sh "exit 1"
                return false;
                break;
            case "e1": 
                ENV_TO_DEPLOY = "pprod"
                ENV_TO_TEST = "recf"
                CODE_ENV_TO_TEST = "e2"; 
                usilColorLog("error", "Validation éffectuée, déploiement non autorisé pour cet environnement")
                currentBuild.result = 'FAILURE'
                sh "exit 1"
                return false;
                break;
            case "e0": 
                ENV_TO_DEPLOY = "prod"
                ENV_TO_TEST = "pprod"
                CODE_ENV_TO_TEST = "e1";
                usilColorLog("error", "Validation éffectuée, déploiement non autorisé pour cet environnement")
                currentBuild.result = 'FAILURE'
                sh "exit 1"
                return false;
                break;
            default:
                usilColorLog("error", "Validation éffectuée, déploiement non autorisé pour cet environnement inconnu de l\'USIL3")
                currentBuild.result = 'FAILURE'
                sh "exit 1"
                return false;
       } 
    }
}
