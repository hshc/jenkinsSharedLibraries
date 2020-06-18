def call() {
        stage('Check du fichier .env ') {
        usilColorLog("stage", "Stage de control du fichier .env")
        // Initialisation
        String[] parametreEnv = ["VOL_CONTAINER", "VVOL_GLUSTERLOCAL", 
                                    "VOL_CONTAINER_CERTIF", "VOL_GLUSTER_CERTIF", 
                                    "NB_REPLICAS", "LIMIT_CPU", "LIMIT_RAM",
                                    "URL_EXTERNAL", "PORT_INTERNAL"]
        def codeRetour = 0
        def paramMap = [:]
        // lecture du fichier .env
        String[] lignesEnv = new file("${env.WORKSPACE}/.env").text
        File fileEnv = new File("${env.WORKSPACE}/.env").eachLine { 
        def line, noOfLines = 0, paramLine;
        fileEnv.withReader { reader ->
            while ((line = reader.readLine()) != null) {
                usilColorLog("debug", "Ligne ${noOfLines}: ${line}") 
                paramLine = line.split("=")
                paramMap[paramLine[0]] = paramLine[1]
                noOfLines++
            }
        }
        // Controles
        if (controlNbChamps(parametreEnv) == false) {
            codeRetour = 1
            usilColorLog("error", "Les paramètres du fichier .env sont invalides")
        } else {
            usilColorLog("info", "Les paramètres du fichier .env sont valides")
        }
        if (controlVolume(parametreEnv[0], parametreEnv[1]) == false) {
            codeRetour = 1
            usilColorLog("error", "Les paramètres des volumes sont invalides")
        } else {
            usilColorLog("info", "Les parparamètresametres des volumes sont valides")
        }
        if (controlVolume(parametreEnv[2], parametreEnv[3]) == false) {
            codeRetour = 1
            usilColorLog("error", "Les paramètres des volumes certificats sont invalides")
        } else {
            usilColorLog("info", "Les paramètres des volumes certificats sont valides")
        }
        if (controlNumMinMax(parametreEnv[4], 1 , 3, null) == false) {
            codeRetour = 1
            usilColorLog("error", "Le nombre de replica est invalide")
        } else {
            usilColorLog("info", "Le nombre de replica est valide")
        }
        if (controlNumMinMax(parametreEnv[5], 0.05, 1, null) == false) {
            codeRetour = 1
            usilColorLog("error", "La limite CPU est invalide")
        } else {
            usilColorLog("info", "La limite CPU est valide")
        }
        if (controlNumMinMax(parametreEnv[6], 1, 8, "g") == false) {
            codeRetour = 1
            usilColorLog("error", "La limite RAM est invalide")
        } else {
            usilColorLog("info", "La limite RAM est valide")
        }
        if (controlURL(parametreEnv[7]) == false) {
            codeRetour = 1
            usilColorLog("error", "Le format de l'URL est invalide")
        } else {
            usilColorLog("info", "Le format de l'URL est valide")
        }
        if (controlNumMinMax(parametreEnv[8], 0, 9999, null) == false) {
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