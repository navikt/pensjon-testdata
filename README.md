# Pensjon testdata


#### Utviklingsmiljø

###### Databaser
Applikasjonen benytter databasetilkoblinger mot PEN,POPP og SAM. Dersom du ikke har alle disse tilgjengelig, så er det mulig å skru av integrasjonen mot disse i `application.properties`

**Properties**
* pen.db.enabled
* popp.db.enabled
* sam.db.enabled

###### Secrets localhost
Kjør `create-secrets-for-dev.sh` og erstatt innhold i underliggende filstruktur med nødvendige brukernavn og passord

###### Server
Applikasjonen kjøres med `mvn spring-boot:run`
Swagger er tilgjengelig på: http://localhost:8080/api/swagger-ui.html#/
Prometheus API er tilgjengelig på: http://localhost:8080/api/prometheus

###### Client
Kjør først `npm install`
Applikasjonen kjøres med `npm run start`

Applikasjonen er tilgjengelig på http://localhost:3000/

#### Docker (localhost)
Kjør `docker-compose up --build`

Swagger er tilgjengelig på: http://localhost:8080/api/swagger-ui.html#/
Applikasjonen er tilgjengelig på http://localhost:9090/

#### Release
Kjør `build-and-deploy-all.sh` som bygger, pusher til repo.adeo.no og legger ut nye versjoner av server 
og client i default nais cluster (koblet mot Q2) 

