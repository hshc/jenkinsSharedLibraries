# Jenkins Shared Librairies 
Repo dédié aux Shared Libraries de Jenkins

## usilAWSBuildPushDockerRegistry
Cette librairie réalise un build docker puis pousse l'image produite sur la DTR
### Inputs :

#### Requis:
- **dockerRegistryUrl** : Url de la DTR
- **dockerRegistryUser** : `credentialId` de l'identifiant Jenkins de type `usernamePassword` qui contient les login\pwd de connexion à la DTR
- **dockerRegistryRepoAppli** : Nom du répository de la DTR (ex: `repo-app`)

#### Optionnels:
- **dockerFilePath** : Chemin d'accès au `Dockerfile`. (défaut: './Dockerfile')
- **dockerBuildArgs** : Arguments du build (défaut: '')

## usilAWSsetupCredentials
Cette librairie réalise les opérations techniques des récupérations des credentials (STS AWS) nécessaires aux déploienemnts AWS
### Inputs :

#### Requis:
- **awsTechnicalAccount** : `credentialId` d'un identifiant Jenkins de typ `usernamePassword` correspondant au compte technique AD utilisé pour récupérer les tokens STS de déploiement AWS.
- **keytabFile** : Fichier "keytab" kerberos utilisé pour l'authentification silencieuse de compte technique AD permettant la récupération des tokens STS de déploiement AWS. Il s'agit du `credentialId` d'un identifiant Jenkins de type `file` contenant la valeur du keytab.

#### Optionnels:
- **kerberosConfigFile** : Fichier de configuration kerberos `krb5.conf`. Il s'agit du `credentialId` d'un identifiant Jenkins de type `file` contenant la valeur du `krb5.conf`. (defaut: 'krb5_conf')