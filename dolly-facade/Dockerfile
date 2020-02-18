FROM openjdk:8-jdk-alpine as build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY settings.xml settings.xml
COPY pom.xml .

RUN ./mvnw -s settings.xml dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests -s settings.xml
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:8-jre-alpine

ARG DEPENDENCY=/app/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENV SECRET_BASEDIR /

ENTRYPOINT ["java","-cp","app:app/lib/*","no.nav.pensjon.testdata.DollyFacadeApplication"]

