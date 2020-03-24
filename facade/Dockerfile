FROM navikt/java:13
ENV SECRET_BASEDIR /

COPY target/dependency/BOOT-INF/lib /lib
COPY target/dependency/META-INF /META-INF
COPY target/dependency/BOOT-INF/classes /

ENV MAIN_CLASS no.nav.pensjon.testdata.FacadeApplication

