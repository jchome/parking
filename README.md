# Sujet

Développer une application “serveur” exposant une API REST pour permettre à une application mobile, ou un site Web, d’afficher la liste des parkings à proximité.

  

On utilisera la source de données suivante disponible à Poitiers pour récupérer la liste des parkings :

https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilite-parkings-grand-poitiers-donnees-metiers&rows=1000&facet=nom_du_parking&facet=zone_tarifaire&facet=statut2&facet=statut3

  

Le nombre de places disponibles du parking en temps réel est récupérable via :

https://data.grandpoitiers.fr/api/records/1.0/search/?dataset=mobilites-stationnement-des-parkings-en-temps-reel&facet=nom

  
  

# Choix de développements
## Framework java
Pour réaliser ce service, je suis parti sur une application **Spring Boot**, qui défini offre des éléments prêt à être utilisés :

- Fichier de configuration et injection des données dans le service

- Service REST avec définition d'un contrôler et des points d'accès

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
 - données métier : nom, position géographique, hauteur, type d'ouvrage, tarif, typed'usage, description, nombre de places, etc.
 - stationnement : nom, taux d'occupation, capacité, places disponibles, position géographique. Quelques fois, le parking n'a pas de position géographique (commele parking "CORDELIERS").

Avec les informations communiquées, il apparait que les 2 JSON retournés par ces URL n'ont pas de clé commune entre les parkings, sauf, peut-être le nom, la position GPS. Mais pour certains parkings, il n'y a pas tout.

Le besoin est d'avoir : 
 - sur une carte : les positions des parkings -- Cela rend inutile les parkings sans position géographique ;
 - sur une vue de détail : le nom, le nombre de places disponibles (et le nombre de places total) -- Cela rend moins intéressant les parkings qui n'ont pas cette information

Ainsi, la version actuelle ne prend pas ne charge les données métiers. Il sera possible d'intégrer ces données pour les ajouter aux données de stationnement, pour par exemple, donner les informations détaillées sur le parking choisi par l'utilisateur.


# Stratégie de test
Les tests se focalisent sur le code de l'application (boite blanche), en testant les fonctionnalités les plus basiques vers les plus globales.
Aucun tests n'est réalisé sur les opérations faites par Java (tri des items), ni dans Spring (Rest Controller). 

Dans les classes de test, chaque methode de test ne se concentre que sur une fonctionnalité (exemple : tester le calcul de distance entre 1 point et un parking), en 2 parties :
 - test que les fonctionnalités correspondent au besoin
 - test en cas d'erreur, pour vérifier que l'erreur est au bon format (et que le comportement est correct)


La structure des classes de test respectent la structure des classes utilisées dans l'application, pour une meilleure visibilité.


# Execution

## Compilation

mvn clean install


## Démarrage

mvn spring-boot:run

## Appels au service
Obtenir le parking le plus proche d'un point de la carte, avec une limite sur le nombre d'items à retrouver :

curl 'localhost:9090/parkings/nearOfPoint?lat=46.58792657819305&lon=0.3485457744862052&limit=1' | jq


Obtenir tous les parking connus du service :

curl localhost:9090/parkings/all | jq
