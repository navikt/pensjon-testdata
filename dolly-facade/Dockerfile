FROM openjdk:8-jre-alpine

COPY target/dependency/BOOT-INF/lib /app/lib
COPY target/dependency/META-INF /app/META-INF
COPY target/dependency/BOOT-INF/classes /app

ENV SECRET_BASEDIR /

ENTRYPOINT ["java","-cp","app:app/lib/*","no.nav.pensjon.testdata.DollyFacadeApplication"]

