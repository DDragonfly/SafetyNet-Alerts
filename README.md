🧯 SafetyNet Alerts

Projet OpenClassrooms – Parcours Développeur d’application Java
Mission : créer une application web RESTful en Spring Boot pour gérer les données de sécurité d’une ville fictive.

🚀 Objectif du projet

Développer une API REST capable de :

Lire les données depuis un fichier JSON (data.json) ;

Exposer des endpoints REST pour accéder aux informations de personnes, casernes et dossiers médicaux ;

Fournir des endpoints spécifiques pour les alertes :

/firestation?stationNumber={number} – personnes couvertes par une caserne

/childAlert?address={address} – enfants et adultes d’un foyer

/phoneAlert?firestation={number} – numéros de téléphone d’une caserne

/fire?address={address} – informations des habitants en cas d’incendie

/flood/stations?stations={list} – ménages couverts par plusieurs casernes

/personInfo?lastName={name} – détails d’une personne

/communityEmail?city={city} – e-mails des habitants d’une ville

🧱 Architecture du projet

Spring Boot 3.5.4

Java 21

Architecture MVC

Persistance simulée via fichier data.json

Logs avec @Slf4j

Tests :

@WebMvcTest pour les contrôleurs REST

@SpringBootTest pour les tests d’intégration

@ExtendWith(MockitoExtension.class) pour les tests unitaires purs

Couverture > 90 % (JaCoCo)

Rapports disponibles dans target/site/

⚙️ Configuration
Fichiers de propriétés
Fichier	Description
application.properties	configuration principale (lecture/écriture)
application-test.properties	profil de test, lecture seule sur data-test.json
Lancer les tests
mvn clean verify

Lancer l’application
mvn spring-boot:run


URL par défaut : http://localhost:8080

🧪 Profils d’exécution
Profil	Description
default	mode normal (lecture/écriture sur data.json)
test	tests d’intégration en lecture seule
it	tests d’intégration complets avec fichier temporaire
📂 Structure du projet
src/
 ├─ main/java/com/openclassroom/SafetyNet/Alerts/
 │   ├─ controller/
 │   ├─ dto/
 │   ├─ model/
 │   └─ service/
 │
 └─ test/java/com/openclassroom/SafetyNet/Alerts/
     ├─ controller/
     ├─ service/
     └─ integration/

🧰 Technologies principales

Spring Boot

Jackson (ObjectMapper)

Lombok

SLF4J

JUnit 5 / Mockito

JaCoCo / Surefire

🧪 Résultats de test

79 tests unitaires et d’intégration

Couverture globale 91 %

Tous les tests réussissent ✅

🏷️ Versioning & Git

Branche principale : main

Branche de développement : dev

Commits : feat:, fix:, test:, etc.

Créer le tag final
git checkout dev
git merge main
git tag -a v1.0.0 -m "Version finale du projet SafetyNet Alerts"
git push origin main --tags

🧑‍💻 Auteur

Désirée Telaretti
Étudiante OpenClassrooms – Développeur d’application Java


📄 Licence

Projet réalisé dans le cadre du parcours OpenClassrooms.
Usage pédagogique uniquement.
