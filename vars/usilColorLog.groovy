def call (def levelLog, def message) {


// liste de couleurs dispo:
//	0 Black, 1 Red, 2 Green, 3 Yellow, 4 Blue, 5 Magenta, 6 Cyan, 7 White

// Récupération du niveau de log à afficher:
switch("${levelLog}.toUpperCase()") { 
    case "DEBUG"
        debut="\033[1;94m[Debug]    \033[0m "
    case "INFO": 
        debut="\033[1;34m[Info]    \033[0m "
    case "WARNING"
        debut="\033[1;35m[Warning]    \033[0m "
    case "SUCCESS"
        debut="\033[1;32m[Success] \033[0m "
    case "ERROR"
        debut="\033[1;31m[Error]   \033[0m "
    default:
        debut=""
} 

wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
    echo "$debut$message" }
}

