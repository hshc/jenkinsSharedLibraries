def call() {
        stage('Check du fichier .env ') {
        usilColorLog("stage", "Stage de control du fichier .env")
        String[] parametreEnv = ["VDOCKER", "VLOCAL", 
                                    "VLOCAL_CERTIF", "VDOCKER_CERTIF", 
                                    "DOCKER_REPLICAS", "DOCKER_LIMIT_CPU", "DOCKER_LIMIT_RAM",
                                    "URL", "PORT"]
        def codeRetour = 0
        // lecture du fichier .env
        // A faire 
        if (controlNbChamps(parametreEnv) == false) {
            codeRetour = 1
            usilColorLog("error", "Les paramètres du fichier .env sont invalides")
        } else {
            usilColorLog("info", "Les paramètres du fichier .env sont valides")
        }
        if (controlVolume(parametreEnv[0], parametreEnv[1]) ==1 {
            codeRetour = 1
            usilColorLog("error", "Les paramètres des volumes sont invalides")
        } else {
            usilColorLog("info", "Les parparamètresametres des volumes sont valides")
        }
        controlVolume(parametreEnv[2], parametreEnv[3]) {
            codeRetour = 1
            usilColorLog("error", "Les paramètres des volumes certificats sont invalides")
        } else {
            usilColorLog("info", "Les paramètres des volumes certificats sont valides")
        }
        controlNumMinMax(parametreEnv[4], 1 , 3, null) {
            codeRetour = 1
            usilColorLog("error", "Le nombre de replica est invalide")
        } else {
            usilColorLog("info", "Le nombre de replica est valide")
        }
        controlNumMinMax(parametreEnv[5], 0.05, 1, null) {
            codeRetour = 1
            usilColorLog("error", "La limite CPU est invalide")
        } else {
            usilColorLog("info", "La limite CPU est valide")
        }
        controlNumMinMax(parametreEnv[6], 1, 8, "g") {
            codeRetour = 1
            usilColorLog("error", "La limite RAM est invalide")
        } else {
            usilColorLog("info", "La limite RAM est valide")
        }
        controlURL(parametreEnv[7]) {
            codeRetour = 1
            usilColorLog("error", "Le format de l'URL est invalide")
        } else {
            usilColorLog("info", "Le format de l'URL est valide")
        }
        controlNumMinMax(parametreEnv[8], 0, 9999, null) {
            codeRetour = 1
            usilColorLog("error", "Le port est invalide")
        } else {
            usilColorLog("info", "Le port est valide")
        }

        return codeRetour
    }
}

def controlNbChamps(String[] nbChamps) {
    // A faire 
    return true
}

def controlVolume(def docker, def local) {
    // A faire 
    return true
}

def controlNumMinMax(def value, def min, def max, def unite) {
    // A faire 
    return true
}

def controlURL(def url) {
    // A faire 
    return true
}