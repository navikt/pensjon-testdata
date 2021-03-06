# Pensjon testdata
![Build and deploy client](https://github.com/navikt/pensjon-testdata/workflows/Build%20and%20deploy%20client/badge.svg)
![Build and deploy server](https://github.com/navikt/pensjon-testdata/workflows/Build%20and%20deploy%20server/badge.svg)
![Build and deploy facade](https://github.com/navikt/pensjon-testdata/workflows/Build%20and%20deploy%20facade/badge.svg)

Løsningen består av følgende komponenter: 

1. pensjon-testdata-client
2. pensjon-testdata-server
3. pensjon-testdata-facade (distribusjon av testdata til pensjonsområdets testmiljøer)

#### Utviklingsmiljø

###### Databaser
Applikasjonen benytter databasetilkoblinger mot PEN,POPP og SAM og en database (Moog) som benyttes for å kunne trekke ut endringer som er gjennomført i Q2 miljøet. 
Dersom du ikke har alle disse tilgjengelig, så er det mulig å skru av integrasjonen mot disse i `application.properties`

**Properties**
* pen.db.enabled
* popp.db.enabled
* sam.db.enabled
* moog.db.enabled
NB! Moog er deaktivert i Q2 pga manglende behov

###### Secrets localhost
Kjør `create-secrets-for-dev.sh` og erstatt innhold i underliggende filstruktur med nødvendige brukernavn og passord

###### Server
Applikasjonen kjøres med `mvn spring-boot:run`
Swagger er tilgjengelig på: http://localhost:8080/swagger-ui.html#/
Prometheus API er tilgjengelig på: http://localhost:8080/api/prometheus

###### Client
Kjør først `npm install`
Applikasjonen kjøres med `npm run start`

Applikasjonen er tilgjengelig på http://localhost:3000/

#### Docker (localhost)
Kjør `docker-compose up --build`

Swagger er tilgjengelig på: http://localhost:8081/swagger-ui.html
Frontend er tilgjengelig på http://localhost:8081/ og http://localhost:9090/

#### Release
Gjøres fra github-actions ved push til master branch. Ved commit til hver applikasjon, skal det trigges bygg som deployes ut til aktuelle testmiljø. 

Bygg kan overvåkes her: https://github.com/navikt/pensjon-testdata/actions


#### Uthenting av testdata/database logger
Uthenting av testdata-sql er muliggjort ved at det er etablert en sepparat database som speiler (Oracle GoldenGate) all aktivitet som gjennomføres i Pesys i Q2.
Spesielt for  denne databasen er at den også gir mulighet til å benytte verktøyet Oracle Logminer, fra dette verktøyet kan vi hente ut REDO logger for å gjenskape de tilstandsendringene som er gjennomført fra pesys. 

Se: `fetch-testdata-log.sql` for hvordan uthentingen gjennomføres.