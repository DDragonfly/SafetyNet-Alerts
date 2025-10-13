# SafetyNet Alerts

**Projet OpenClassrooms – Parcours Développeur d’application Java**

Ce projet a pour objectif de créer une application web RESTful en **Spring Boot** permettant de gérer les données de sécurité d’une ville fictive.

---

## Objectif du projet

Développer une API REST capable de :

- Lire les données depuis un fichier JSON (`data.json`)
    
- Exposer des **endpoints REST** pour accéder aux informations de personnes, casernes et dossiers médicaux
    
- Fournir des endpoints spécifiques pour les **alertes** :
    

|Endpoint|Description|
|---|---|
|`/firestation?stationNumber={number}`|Personnes couvertes par une caserne|
|`/childAlert?address={address}`|Enfants et adultes d’un foyer|
|`/phoneAlert?firestation={number}`|Numéros de téléphone d’une caserne|
|`/fire?address={address}`|Informations des habitants en cas d’incendie|
|`/flood/stations?stations={list}`|Ménages couverts par plusieurs casernes|
|`/personInfo?lastName={name}`|Détails d’une personne|
|`/communityEmail?city={city}`|E-mails des habitants d’une ville|

---

## Architecture du projet

- Spring Boot 3.5.4
    
- Java 21
    
- Architecture MVC
    
- Persistance simulée via le fichier `data.json`
    
- Logging avec **SLF4J** (`@Slf4j`)
    
- Tests unitaires et d’intégration avec **JUnit 5** et **Mockito**
    

---

## Configuration

### Fichiers de propriétés

|Fichier|Description|
|---|---|
|`application.properties`|Configuration principale (lecture/écriture sur `data.json`)|
|`application-test.properties`|Profil de test (lecture seule sur `data-test.json`)|

---

### Lancer les tests

`mvn clean verify`

### Lancer l’application

`mvn spring-boot:run`

Puis accéder à :  
[http://localhost:8080](http://localhost:8080)

---

## Profils d’exécution

|Profil|Description|
|---|---|
|`default`|Mode normal (lecture/écriture sur `data.json`)|
|`test`|Tests d’intégration en lecture seule|
|`it`|Tests d’intégration complets sur un fichier temporaire|

---

## Structure du projet

``` 
src/
  ├─ main/java/com/openclassroom/SafetyNet/Alerts/  
  │	  ├─ controller/  
  │	  ├─ dto/  
  │   ├─ model/  
  │   └─ service/  
  │  
  └─ test/java/com/openclassroom/SafetyNet/Alerts/      
	  ├─ controller/      
	  ├─ service/      
	  └─ integration/
```

---

## Technologies principales

- Spring Boot
    
- Jackson (ObjectMapper)
    
- Lombok
    
- SLF4J
    
- JUnit 5 / Mockito
    
- JaCoCo / Surefire
    

---

## Résultats de test

- 79 tests unitaires et d’intégration
    
- Couverture globale : **91 %**
    
- Tous les tests réussissent
    

---

## Versioning & Git

- Branche principale : `main`
    
- Branche de développement : `dev`
    
---

## Auteur

**Désirée Telaretti**  
Étudiante OpenClassrooms – _Développeur d’application Java_  

---

## 📄 Licence

Projet réalisé dans le cadre du parcours OpenClassrooms.  
Usage pédagogique uniquement.
