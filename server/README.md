## Testdata pensjon

**Forutsetninger**
Applikasjonen er avhengig av at miljøvariablene under finnes:

 - PENDB_URL
 - PENDB_USR
 - PENDB_PW

Kjøres med:   

    mvn spring-boot:run 

  
Swagger: http://localhost:8080/swagger-ui.html  
API: http://localhost:8080/testdata
  

**Eksempler**

    curl -X DELETE "http://localhost:8080/testdata" -H "accept: */*"

	curl -X POST "http://localhost:8080/testdata" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"handlebars\": { \"fnr\": \"12345678911\" }, \"testCaseId\": \"alderspensjon\"}"