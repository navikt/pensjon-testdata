FROM maven:3-jdk-13 as build
WORKDIR /app

COPY pom.xml .

COPY src src

RUN mvn package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM maven:3-jdk-13
ARG DEPENDENCY=/app/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENV SECRET_BASEDIR /

ENTRYPOINT ["java","-cp","app:app/lib/*","no.nav.pensjon.testdata.TestdataApplication"]

