# Comment contribuer ?
Si vous souhaitez contribuer au projet JenkinsSharedLibrairies, pour une nouvelle fonctionnalité ou une correction,
la première chose à faire est d'envoyer un mail à la liste de diffusion grpo365_usil3contributors@b1envenue.onmicrosoft.com pour décrire ce que vous voulez faire.

Cela permettra de faire une première validation de l'idée avant de commencer à développer et nous pourrons vous prevenir si un développement est déjà en cours sur le sujet.
Vous serez également inclus au groupe grpo365_usil3contributors@b1envenue.onmicrosoft.com si ce n'est pas déjà le cas.

## Résumé des branches
* feature : branche de développement (environnement de développement)
* develop : correspond à l'environnement de recette (https://int-jenkins-master.si2m.tec/)
* master : correspond à l'environnement de production (https://jenkins-master.si2m.tec/)


Un nouveau développement doit passer par la création d'une branche feature.

Une fois le développement terminé, la branche feature doit faire l'objet d'une pull request pour être mergée dans develop.

Une phase de recette sur develop permettra de valider ou non la nouvelle version.

Une pull request pourra être ouverte pour la livraison de la nouvelle version de develop dans master.

Pour terminé la livraison, un tag devra être fait sur le master et c'est ce nouveau tag qui sera utilisé sur le jenkins de production.

## Phase de développement
Pour tout nouveau developpement sur le projet, vous devrez tirer une branche "feature/ma-contrib" à partir de la branche "develop".

Lors de vos développement, si vous voulez tester votre version des sharedLibrairies, vous pouvez importer votre version dans le jenkinsFile de votre projet de test :
```
@Library('UsilJenkinsLibrary@feature/ma-contrib') _
```

Une fois que votre développement vous semble OK, vous pouvez faire une pull request sur develop pour que les autres contributeurs puissent vérifier votre contribution avant de la merger dans develop.
