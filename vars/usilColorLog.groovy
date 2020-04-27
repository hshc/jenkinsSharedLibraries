def call (def levelLog, def message) {


// liste de couleurs dispo:
//	0 Black, 1 Red, 2 Green, 3 Yellow, 4 Blue, 5 Magenta, 6 Cyan, 7 White

// Récupération du niveau de log à afficher:
string level=levelLog.toUpperCase()
levelAffich = "[ ${level} ]    "

switch(level) { 
    case "DEBUG":
        color="94"; break;
    case "INFO": 
        color="34"; break;
    case "WARNING":
        color="35"; break;
    case "SUCCESS":
        color="32"; break;
    case "ERROR":
        color="31"; break;
    default:
        levelAffich = ""
        color="30"
} 

wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm']) {
    echo "\033[1;${color}m${levelAffich} ${message} \033[0m" }
}
