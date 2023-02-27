# Sujet
## Besoin
Développer une application “serveur” exposant une API REST pour permettre à une application mobile, ou un site Web, d’afficher la liste des parkings à proximité.

  

On utilisera la source de données suivante disponible à Poitiers pour récupérer la liste des parkings :

https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilite-parkings-grand-poitiers-donnees-metiers&rows=1000&facet=nom_du_parking&facet=zone_tarifaire&facet=statut2&facet=statut3

  

Le nombre de places disponibles du parking en temps réel est récupérable via :

https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilites-stationnement-des-parkings-en-temps-reel&facet=nom

## Contexte
Etant en activité tous les jours de la semaine et n'étant pas chez moi tous les soirs (même le week-end), avec mon matériel informatique à disposition, il a fallu trouver un moment de "libre" pour réaliser cette petite application. J'ai donc profité de quelques instants en télétravail pour me mettre sur ce code.
Avec moins d'un an en mission avec Windev comme langage quotidien, certains automatismes ont dû être réactivés. Il était impossible de commencer la moindre ligne de codage chez mon client actuel.

En conclusion, ce contexte n'est pas synonyme de bonnes conditions de travail.


# Choix de développements
## Framework java
Pour réaliser ce service, je suis parti sur une application **Spring Boot**, qui offre des éléments prêt à être utilisés :

 - Fichier de configuration et injection des données dans le service
 - Service REST avec définition d'un contrôler et des points d'accès

D'autres bibliothèques et packages facilitent les développements :
 - Lombok : pour automatiser les méthodes d'un POJO
 - apache.io : pour obtenir un appel HTTP sur une URL et récupérer le contenu
 - org.json : pour transformer un texte en objet JSON générique
 - powermock et junit : pour faire des tests unitaires avec mock d'objets
 

## Architecture
L'application server est découpé par package, avec :
 - parking : l'application et le service
 
Et à l'intérieur, les packages suivants :
 - generic : ce qui est commun à tous les parkings
 - grandPoiters : ce qui est spécifique aux parkings du Grand Poitiers


Rôle des classes :
- ParkingApplication : héberge le Main de Spring boot
- ParkingService : expose les services REST d'accès aux données
- generic.Parking : entité qui représente un parking, de manière générique (pas qu'à Poitiers)
- generic.InvalidJsonException : classe d'exception qui encapsule les erreurs techniques
- grandPoitiers.ParkingReader : récupère les données depuis le service des parkings de Poitiers


## Services de la ville de Poitiers

La ville de Poitiers propose les 2 URL citées plus haut :
 - données métier : nom, position géographique, hauteur, type d'ouvrage, tarif, type d'usage, description, nombre de places, etc.
 - stationnement : nom, taux d'occupation, capacité, places disponibles, position géographique. Quelques fois, le parking n'a pas de position géographique (comme le parking "CORDELIERS"). Ce parking sera donc écarté car non utile pour l'application mobile.

Avec les informations communiquées, il apparait que les 2 JSON retournés par ces URL n'ont pas de clé commune entre les parkings, sauf, peut-être le nom, la position GPS. Mais pour certains parkings, il n'y en a pas tout.

Le besoin est d'avoir : 
 - sur une carte : les positions des parkings -- Cela rend inutile les parkings sans position géographique ;
 - sur une vue de détail : le nom, le nombre de places disponibles (et le nombre de places total) -- Cela rend moins intéressant les parkings qui n'ont pas cette information.

Ainsi, la version actuelle ne prend pas ne charge les données métiers. Il sera possible d'intégrer ces données pour les ajouter aux données de stationnement, pour par exemple, donner les informations détaillées sur le parking choisi par l'utilisateur.

## Limitations
- La version actuelle ne fait appel qu'au service de stationnement. Un travail en amont doit être fait pour définir une clé d'unicité entre les 2 sources de données (qui ne semble pas évident de prime abord).
- Les tests ne sont pas exhastifs. Dans cette version, un test a été réalisé avec un mock de l'appel HTTP au vrai service de stationnement de la ville de Poitiers. Pour assurer un constance dans les tests, même si un nouveau parking voit le jour à Poitier, il est préférable de maitriser son jeu de données.
- L'intégration continue (via la notion de "actions" de GitHub), la couverture de code et métriques, le rapport de test n'ont pas été abordées.
- Au niveau documentation (le référentiel des exigences, le dossier d'architecture,  les spécifications, les métriques, le plan de test, le rapport de test, etc.), rien n'a été produit à part la note "README.md".

# Stratégie de test
Les tests se focalisent sur le code de l'application (mode boite blanche), en testant les fonctionnalités les plus basiques vers les plus globales.
Aucun tests n'est réalisé sur les opérations faites par Java (par exemple, le tri des items), ni dans les bibliothèques (par exemple le Rest Controller). 

Dans les classes de test, chaque methode de test ne se concentre que sur une fonctionnalité (exemple : tester le calcul de distance entre 1 point et un parking), en 2 parties :
 - test que les fonctionnalités correspondent au besoin
 - test en cas d'erreur, pour vérifier que l'erreur est au bon format (et que le comportement est correct) -- non traité actuellement

La structure des classes de test respectent la structure des classes utilisées dans l'application, pour une meilleure lisibilité.

# Execution
## Prérequis
Pour compiler et executer le code, il faut que Maven soit installer avec Java 11. Une connexion Internet est nécessaire pour la récupération des bibliothèques.

## Compilation et execution des tests
mvn clean install

## Configuration
Le fichier "application.yml" permet de configurer le service, avec : 
 - le numéro de port sur lequel est sera à requêter
 - l'URL du service de stationnement du Grand Poitiers

## Démarrage
mvn spring-boot:run

## Appels au service réalisé
Obtenir le parking le plus proche d'un point de la carte, avec une limite sur le nombre d'items à retrouver :

curl 'localhost:9090/parkings/nearOfPoint?lat=46.58792657819305&lon=0.3485457744862052&limit=1' | jq


Obtenir tous les parking connus du service :

curl localhost:9090/parkings/all | jq

# Version en Python
La version en python est plus simple, même s'il n'y a pas de tests unitaires dans ce gist (faite en 1h) :

https://gist.github.com/jchome/8ec333eb3f8cffca4eb7d27840dc2c85#file-parkings-py

Cette version est testable ici :

https://replit.com/@JulienCoron/Parkings-of-Poitiers?v=1
